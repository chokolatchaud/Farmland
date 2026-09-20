package fr.kevyn.farmland.EventBuild;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.cosmetics.CosmeticShop;
import fr.kevyn.farmland.game.CustomItemType;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.GameMenuHashMap;
import fr.kevyn.farmland.menu.MenuPlotConfig;
import fr.kevyn.farmland.menu.MenuPlotUpgrade;
import fr.kevyn.farmland.menu.MenuPlotVisit;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.Plot;

public class Plotinventory implements Listener {
    private final Map<UUID, Integer> playerPageById = new HashMap<>();
    private final FarmlandMain plugin;

    public Plotinventory(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        GameMenu gameMenu = null;
        for (GameMenu g : GameMenuHashMap.getInstance().getMenulist()) {
            if (event.getInventory().equals(g.getInventory())) { gameMenu = g; break; }
        }
        if (gameMenu == null) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // ── PLOTVISIT : têtes de joueurs ──────────────────────────────────────
        if (gameMenu.getTypeMenu() == TypeMenu.PLOTVISIT && clickedItem.getType() == Material.PLAYER_HEAD) {
            if (!(clickedItem.getItemMeta() instanceof SkullMeta)) return;
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            OfflinePlayer owning = meta.getOwningPlayer();
            if (owning == null) { player.sendMessage(MessageColor.RED.apply("Erreur : propriétaire introuvable !")); return; }

            PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(owning.getUniqueId());
            if (targetServer == null || targetServer.getPlotdata() == null) { player.sendMessage(MessageColor.RED.apply("Erreur : plot introuvable !")); return; }
            if (targetServer.getPlotdata().getPrivateplot()) { player.sendMessage(MessageColor.RED.apply("Ce plot est privé")); return; }

            String targetPlotName = targetServer.getPlotdata().getPlotProprety();
            World targetPlotWorld = Plot.getWorldforname(targetPlotName);
            if (targetPlotWorld == null) {
                player.sendMessage(MessageColor.GRAY.apply("Chargement du plot en cours..."));
                new Plot(UUID.fromString(targetPlotName), plugin);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    World loaded = Plot.getWorldforname(targetPlotName);
                    if (loaded == null) { player.sendMessage(MessageColor.RED.apply("Impossible de charger le plot !")); return; }
                    teleportToPlot(player, targetServer, loaded);
                }, 60L);
                return;
            }
            teleportToPlot(player, targetServer, targetPlotWorld);
            return;
        }

        // ── COSMETICS : achat/equipement d'un chapeau ─────────────────────────
        if (gameMenu.getTypeMenu() == TypeMenu.COSMETICS) {
            int slot = event.getSlot();
            if (slot < 0 || slot >= CosmeticShop.COSMETICS.size()) return;

            CosmeticShop.Cosmetic cosmetic =
                    CosmeticShop.COSMETICS.get(slot);

            PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (playerServer == null) return;

            if (playerServer.getCosmeticsOwned().contains(cosmetic.id)) {
                player.getInventory().setHelmet(cosmetic.createItem());
                player.sendMessage(MessageColor.GREEN.apply("✔ " + cosmetic.name + " équipé !"));
                player.closeInventory();
                return;
            }

            if (playerServer.getMoney() < cosmetic.price) {
                player.sendMessage(MessageColor.RED.apply("Tu n'as pas assez d'argent (" + cosmetic.price + " $FB)"));
                return;
            }

            playerServer.setMoney(playerServer.getMoney() - cosmetic.price);
            playerServer.getCosmeticsOwned().add(cosmetic.id);
            player.getInventory().setHelmet(cosmetic.createItem());
            player.sendMessage(MessageColor.GREEN.apply("✔ " + cosmetic.name + " acheté et équipé ! (-" + cosmetic.price + " $FB)"));
            player.closeInventory();
            return;
        }

        // ── CUSTOM ITEMS ──────────────────────────────────────────────────────
        CustomItemType customItemType = CustomItemType.fromItem(clickedItem);
        if (customItemType == null) return;

        // ── PLOTUPGRADE ───────────────────────────────────────────────────────
        if (gameMenu.getTypeMenu() == TypeMenu.PLOTUPGRADE) {
            if (customItemType == CustomItemType.UPGRADE_LOCKED) {
                PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                if (playerServer == null) { player.kickPlayer("erreur 23"); return; }
                if (playerServer.getPlotdata() == null) { player.sendMessage(MessageColor.RED.apply("Erreur : plot introuvable !")); return; }

                int rank = playerServer.getUpgrade();

                // Le systeme est infini : plus de "tu as tout achete", juste un palier suivant
                // Le slot clique doit correspondre au PROCHAIN upgrade DANS LE PALIER ACTUEL
                int upgradesPerPage = fr.kevyn.farmland.menu.MenuPlotUpgrade.getMaxUpgrades();
                int rankInPage = rank % upgradesPerPage;
                int slotIndex = upgradeSlotIndex(event.getSlot());
                if (slotIndex != rankInPage) {
                    player.sendMessage(MessageColor.RED.apply("Achète d'abord les upgrades précédents !"));
                    return;
                }

                // Prix récupéré côté serveur (rang absolu, jamais depuis le nom de l'item)
                int cost = MenuPlotUpgrade.getCost(rank);
                if (playerServer.getMoney() < cost) { player.sendMessage(MessageColor.RED.apply("Tu n'as pas assez d'argent (" + cost + " $FB)")); return; }

                playerServer.setMoney(playerServer.getMoney() - cost);
                playerServer.setUpgrade(playerServer.getUpgrade() + 1);
                playerServer.getPlotdata().setWorldborder(playerServer.getPlotdata().getWorldborder() + 5);
                player.sendMessage(MessageColor.GREEN.apply("Upgrade acheté ! (-" + cost + " $FB, +5 de bordure)"));

                // nouveau palier atteint : prevenir le joueur que les prix montent
                if ((rank + 1) % upgradesPerPage == 0) {
                    player.sendMessage(MessageColor.GOLD.apply("✦ Nouveau palier ! Les prix des prochains upgrades montent de 20 $FB."));
                }

                player.closeInventory();
            }
        }

        // ── PLOTCONFIG ────────────────────────────────────────────────────────
        else if (gameMenu.getTypeMenu() == TypeMenu.PLOTCONFIG) {
            PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (playerServer == null) { player.kickPlayer("erreur 23"); return; }

            if (customItemType == CustomItemType.CLOCK_DAYNIGHT) {
                if (playerServer.getPlotdata().getMeteoTime().equalsIgnoreCase("day")) {
                    playerServer.getPlotdata().setMeteoTime("night", Bukkit.getWorld(playerServer.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.DARK_BLUE.apply("La nuit approche"));
                } else {
                    playerServer.getPlotdata().setMeteoTime("day", Bukkit.getWorld(playerServer.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.YELLOW.apply("Le jour approche"));
                }
            }
            if (customItemType == CustomItemType.RAIN_TOGGLE) {
                if (playerServer.getPlotdata().getMeteoRain().equalsIgnoreCase("weatherclear")) {
                    playerServer.getPlotdata().setMeteoRain("weatherain", Bukkit.getWorld(playerServer.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.BLUE.apply("La pluie approche"));
                } else {
                    playerServer.getPlotdata().setMeteoRain("weatherclear", Bukkit.getWorld(playerServer.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.BLUE.apply("La pluie s'éloigne"));
                }
            }
            if (customItemType == CustomItemType.TIME_FREEZE) {
                World plot = Bukkit.getWorld(playerServer.getPlotdata().getNameWorld());
                if (playerServer.getPlotdata().getMeteoActive().equalsIgnoreCase("minecraftActive")) {
                    playerServer.getPlotdata().setMeteoActive("minecraftDeactive", plot);
                    plot.setGameRule(GameRules.ADVANCE_TIME, true);
                    player.sendMessage(MessageColor.GOLD.apply("La météo se met à bouger"));
                } else {
                    playerServer.getPlotdata().setMeteoActive("minecraftActive", plot);
                    plot.setGameRule(GameRules.ADVANCE_TIME, false);
                    player.sendMessage(MessageColor.GOLD.apply("La météo se fige"));
                }
            }
            if (customItemType == CustomItemType.WATERLAVASELECTION) {
                playerServer.getPlotdata().setwaterlava(!playerServer.getPlotdata().getwaterlava());
                player.sendMessage(MessageColor.YELLOW.apply("Option eau/lave modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
            if (customItemType == CustomItemType.DOOR_PRIVACY) {
                playerServer.getPlotdata().setPrivateplot(!playerServer.getPlotdata().getPrivateplot());
                player.sendMessage(MessageColor.YELLOW.apply("Visibilité du plot modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
        }

        // ── PLOTVISIT navigation ──────────────────────────────────────────────
        else if (gameMenu.getTypeMenu() == TypeMenu.PLOTVISIT) {
            if (customItemType == CustomItemType.ARROW_NEXT) {
                int p = playerPageById.getOrDefault(player.getUniqueId(), 1);
                playerPageById.put(player.getUniqueId(), p + 1);
                Inventory newInventory = MenuPlotVisit.createmenuplotvisit("Visite", p + 1);
                if (newInventory != null) player.openInventory(newInventory);
            } else if (customItemType == CustomItemType.ARROW_PREV) {
                int p = playerPageById.getOrDefault(player.getUniqueId(), 1);
                if (p > 1) {
                    playerPageById.put(player.getUniqueId(), p - 1);
                    Inventory newInventory = MenuPlotVisit.createmenuplotvisit("Visite", p - 1);
                    if (newInventory != null) player.openInventory(newInventory);
                }
            }
        }
    }

    // ── TP vers un plot avec position sûre ───────────────────────────────────
    private void teleportToPlot(Player player, PlayerServer targetServer, World targetPlotWorld) {
        int spawnX = targetServer.getPlotdata().getLocationspawnX();
        int spawnY = targetServer.getPlotdata().getLocationspawnY();
        int spawnZ = targetServer.getPlotdata().getLocationspawnZ();

        int tx = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 0 : spawnX;
        int tz = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 0 : spawnZ;
        int ty = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 64 : spawnY;

        plugin.getLogger().info("[DEBUG-TP] Spawn sauvegardé: " + spawnX + "/" + spawnY + "/" + spawnZ);
        plugin.getLogger().info("[DEBUG-TP] Target: " + tx + "/" + ty + "/" + tz + " monde: " + targetPlotWorld.getName());

        targetPlotWorld.loadChunk(tx >> 4, tz >> 4, true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location start = new Location(targetPlotWorld, tx + 0.5, ty, tz + 0.5);
            int highY = targetPlotWorld.getHighestBlockYAt(tx, tz);
            plugin.getLogger().info("[DEBUG-TP] highY=" + highY + " startY=" + ty);
            Location safe = findSafeLocation(start);
            plugin.getLogger().info("[DEBUG-TP] Position safe finale: " + safe.getBlockX() + "/" + safe.getBlockY() + "/" + safe.getBlockZ());
            player.teleport(safe);
            player.closeInventory();
            player.sendMessage(MessageColor.GREEN.apply("Téléportation vers le plot de " + targetServer.getName()));
        }, 10L);
    }

    private Location findSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return loc;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        // Point de départ : bloc le plus haut de la colonne
        int highY = world.getHighestBlockYAt(x, z);
        if (highY < world.getMinHeight() + 5) highY = 64;

        int startY = Math.max(loc.getBlockY(), highY);
        int maxY = world.getMaxHeight() - 2;

        for (int y = startY; y <= maxY; y++) {
            if (!world.getBlockAt(x, y, z).getType().isSolid()
                && !world.getBlockAt(x, y + 1, z).getType().isSolid()
                && world.getBlockAt(x, y - 1, z).getType().isSolid()) {
                return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
            }
        }
        return new Location(world, x + 0.5, highY + 1, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GameMenuHashMap.getInstance().getMenulist().removeIf(
            menu -> menu.getInventory().equals(event.getInventory())
        );
    }

    /**
     * Convertit un slot du menu upgrade en index d'upgrade (0-20).
     * Slots utilisés : 1-7 (rangs 0-6), 19-25 (rangs 7-13), 37-43 (rangs 14-20).
     * Retourne -1 si le slot n'est pas un slot d'upgrade.
     */
    private int upgradeSlotIndex(int slot) {
        if (slot >= 1 && slot <= 7)   return slot - 1;
        if (slot >= 19 && slot <= 25) return slot - 19 + 7;
        if (slot >= 37 && slot <= 43) return slot - 37 + 14;
        return -1;
    }
}