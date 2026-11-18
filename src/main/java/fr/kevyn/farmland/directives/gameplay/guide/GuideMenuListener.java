package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.gameplay.menus.MenuVenteJetons;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.GameMenuHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Clics dans le menu du guide, et clic droit sur l'objet Guide pour l'ouvrir.
 */
public final class GuideMenuListener implements Listener {

    private final JavaPlugin plugin;

    public GuideMenuListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Ouvre la page du guide pour ce joueur (au tick suivant si on est dans un clic). */
    public static void ouvrir(JavaPlugin plugin, Player joueur, GuideMenu.Page page, boolean tickSuivant) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
        if (ps == null) return;
        Runnable action = () -> {
            if (joueur.isOnline()) joueur.openInventory(GuideMenu.ouvrir(joueur, ps, page));
        };
        if (tickSuivant) Bukkit.getScheduler().runTask(plugin, action);
        else action.run();
    }

    @EventHandler
    public void onClicGuide(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!GuideLivre.estLeGuide(event.getItem())) return;
        event.setCancelled(true);
        ouvrir(plugin, event.getPlayer(), GuideMenu.Page.ACCUEIL, false);
    }

    @EventHandler
    public void onClic(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player joueur)) return;

        GameMenu trouve = null;
        for (GameMenu g : GameMenuHashMap.getInstance().getMenulist()) {
            if (event.getInventory().equals(g.getInventory())) { trouve = g; break; }
        }
        if (!(trouve instanceof GuideMenu menu)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) return; // clic dans son inventaire
        String action = menu.actionDuSlot(slot);
        if (action == null) return;
        joueur.playSound(joueur.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        if (action.equals("fermer")) {
            Bukkit.getScheduler().runTask(plugin, () -> joueur.closeInventory());
        } else if (action.startsWith("page:")) {
            ouvrir(plugin, joueur, GuideMenu.Page.valueOf(action.substring(5)), true);
        } else if (action.startsWith("cmd:")) {
            String commande = action.substring(4);
            Bukkit.getScheduler().runTask(plugin, () -> {
                joueur.closeInventory();
                joueur.performCommand(commande);
            });
        } else if (action.startsWith("vente:")) {
            String metier = action.substring(6);
            PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
            if (ps == null) return;
            int stock = GuideMenu.jetonsDe(ps, metier);
            if (stock <= 0) return;
            Inventory vente = MenuVenteJetons.createmenu(metier, stock);
            Bukkit.getScheduler().runTask(plugin, () -> joueur.openInventory(vente));
        }
    }
}
