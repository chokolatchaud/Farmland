package fr.kevyn.farmland.directives.metiers.farmeur;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mode Farm rapide : Maj + clic droit avec une houe de Farmland active ou désactive
 * la récolte automatique de la culture mature visée. Le délai dépend du niveau Farmeur.
 * Aucun maintien du clic gauche n'est nécessaire (non observable côté serveur).
 */
public final class FarmRapideListener implements Listener {

    private final Map<UUID, Integer> actifs = new HashMap<>();
    private int tick = 0;

    public FarmRapideListener(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            tick++;
            for (UUID uuid : java.util.List.copyOf(actifs.keySet())) {
                Player joueur = Bukkit.getPlayer(uuid);
                if (joueur == null || !joueur.isOnline()
                        || !estHoue(joueur.getInventory().getItemInMainHand())) {
                    desactiver(uuid, joueur);
                    continue;
                }

                int prochainTick = actifs.getOrDefault(uuid, tick);
                if (tick < prochainTick) continue;

                PlayerServer ps = PlayerserverHashMap.getInstance()
                        .getplayerHaspMaps(uuid);
                if (ps == null) {
                    desactiver(uuid, joueur);
                    continue;
                }

                int delai = Math.max(1, 5 - Math.max(0, ps.getHoueLevel()) / 20);
                actifs.put(uuid, tick + delai);

                Block vise = joueur.getTargetBlockExact(5);
                if (vise == null || !estCultureMature(vise)) continue;

                // breakBlock appelle le BlockBreakEvent normal : les protections de
                // parcelle et HarvestFarmEvent (XP, jetons, Houe du Maître) restent actifs.
                joueur.breakBlock(vise);
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onToggle(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || !event.getPlayer().isSneaking()
                || (event.getAction() != Action.RIGHT_CLICK_AIR
                    && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                || !estHoue(event.getItem())) {
            return;
        }

        // Ne pas planter ou interagir avec le bloc pendant le toggle.
        event.setCancelled(true);

        Player joueur = event.getPlayer();
        UUID uuid = joueur.getUniqueId();
        if (actifs.containsKey(uuid)) {
            desactiver(uuid, joueur);
        } else {
            actifs.put(uuid, tick);
            joueur.sendMessage("§a✔ Farm rapide activé ! §7Vise des cultures mûres pour les récolter.");
            joueur.sendMessage("§7Maj + clic droit avec la houe pour désactiver.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        desactiver(event.getPlayer().getUniqueId(), null);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        desactiver(event.getPlayer().getUniqueId(), event.getPlayer());
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        if (actifs.containsKey(event.getPlayer().getUniqueId())
                && !estHoue(event.getPlayer().getInventory().getItem(event.getNewSlot()))) {
            desactiver(event.getPlayer().getUniqueId(), event.getPlayer());
        }
    }

    private void desactiver(UUID uuid, Player joueur) {
        if (actifs.remove(uuid) != null && joueur != null && joueur.isOnline()) {
            joueur.sendMessage("§c✖ Farm rapide désactivé.");
        }
    }

    private static boolean estHoue(ItemStack item) {
        // Même tag pour /houe et la Houe du Maître.
        return Outils.isOutilsAttendu(item, Material.NETHERITE_HOE);
    }

    private static boolean estCultureMature(Block bloc) {
        switch (bloc.getType()) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART -> {
                if (bloc.getBlockData() instanceof Ageable ageable) {
                    return ageable.getAge() >= ageable.getMaximumAge();
                }
            }
            default -> { }
        }
        return false;
    }
}
