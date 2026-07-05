package fr.kevyn.farmland.EventBuild;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.game.CustomItemType;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.GameMenuHashMap;
import fr.kevyn.farmland.menu.MenuPlotConfig;
import fr.kevyn.farmland.menu.MenuPlotVisit;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.Plot;

public class Plotinventory implements Listener {
    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final FarmlandMain plugin;

    public Plotinventory(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        GameMenu gamemenu = null;
        for (GameMenu g : GameMenuHashMap.getInstance().getMenulist()) {
            if (event.getInventory().equals(g.getInventory())) { gamemenu = g; break; }
        }
        if (gamemenu == null) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // ── PLOTVISIT : têtes de joueurs ──────────────────────────────────────
        if (gamemenu.getTypemenu() == TypeMenu.PLOTVISIT && clicked.getType() == Material.PLAYER_HEAD) {
            if (!(clicked.getItemMeta() instanceof SkullMeta)) return;
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            OfflinePlayer owning = meta.getOwningPlayer();
            if (owning == null) { player.sendMessage(MessageColor.RED.apply("Erreur : propriétaire introuvable !")); return; }

            PlayerServer ps1 = PlayerserverHashMap.getInstance().getplayerHaspMaps(owning.getUniqueId());
            if (ps1 == null || ps1.getPlotdata() == null) { player.sendMessage(MessageColor.RED.apply("Erreur : plot introuvable !")); return; }
            if (ps1.getPlotdata().getPrivateplot()) { player.sendMessage(MessageColor.RED.apply("Ce plot est privé")); return; }

            String plotName = ps1.getPlotdata().getPlotProprety();
            World plotWorld = Plot.getWorldforname(plotName);
            if (plotWorld == null) {
                player.sendMessage(MessageColor.GRAY.apply("Chargement du plot en cours..."));
                new fr.kevyn.plot.Plot(UUID.fromString(plotName), plugin);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    World loaded = Plot.getWorldforname(plotName);
                    if (loaded == null) { player.sendMessage(MessageColor.RED.apply("Impossible de charger le plot !")); return; }
                    teleportToPlot(player, ps1, loaded);
                }, 60L);
                return;
            }
            teleportToPlot(player, ps1, plotWorld);
            return;
        }

        // ── CUSTOM ITEMS ──────────────────────────────────────────────────────
        CustomItemType customType = CustomItemType.fromItem(clicked);
        if (customType == null) return;

        // ── PLOTUPGRADE ───────────────────────────────────────────────────────
        if (gamemenu.getTypemenu() == TypeMenu.PLOTUPGRADE) {
            if (customType == CustomItemType.UPGRADE_LOCKED) {
                ItemMeta meta = clicked.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;
                String name = ChatColor.stripColor(meta.getDisplayName());
                if (!name.contains("Coût")) return;
                int cost;
                try { cost = Integer.parseInt(name.replace("Coût :", "").trim()); }
                catch (NumberFormatException e) { return; }
                PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                if (ps == null) { player.kickPlayer("erreur 23"); return; }
                if (ps.getMoney() < cost) { player.sendMessage(MessageColor.RED.apply("Tu n'as pas assez d'argent")); return; }
                ps.setMoney(ps.getMoney() - cost);
                ps.setUpgrade(ps.getUpgrade() + 1);
                ps.getPlotdata().setWorldborder(ps.getPlotdata().getWorldborder() + 5);
                player.sendMessage(MessageColor.GREEN.apply("Upgrade acheté !"));
                player.closeInventory();
            }
        }

        // ── PLOTCONFIG ────────────────────────────────────────────────────────
        else if (gamemenu.getTypemenu() == TypeMenu.PLOTCONFIG) {
            PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (ps == null) { player.kickPlayer("erreur 23"); return; }

            if (customType == CustomItemType.CLOCK_DAYNIGHT) {
                if (ps.getPlotdata().getMeteoTime().equalsIgnoreCase("day")) {
                    ps.getPlotdata().setMeteoTime("night", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.DARK_BLUE.apply("La nuit approche"));
                } else {
                    ps.getPlotdata().setMeteoTime("day", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.YELLOW.apply("Le jour approche"));
                }
            }
            if (customType == CustomItemType.RAIN_TOGGLE) {
                if (ps.getPlotdata().getMeteoRain().equalsIgnoreCase("weatherclear")) {
                    ps.getPlotdata().setMeteoRain("weatherain", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.BLUE.apply("La pluie approche"));
                } else {
                    ps.getPlotdata().setMeteoRain("weatherclear", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.BLUE.apply("La pluie s'éloigne"));
                }
            }
            if (customType == CustomItemType.TIME_FREEZE) {
                if (ps.getPlotdata().getMeteoActive().equalsIgnoreCase("minecraftActive")) {
                    ps.getPlotdata().setMeteoActive("minecraftDeactive", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.GOLD.apply("La météo se met à bouger"));
                } else {
                    ps.getPlotdata().setMeteoActive("minecraftActive", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                    player.sendMessage(MessageColor.GOLD.apply("La météo se fige"));
                }
            }
            if (customType == CustomItemType.WATERLAVASELECTION) {
                ps.getPlotdata().setwaterlava(!ps.getPlotdata().getwaterlava());
                player.sendMessage(MessageColor.YELLOW.apply("Option eau/lave modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", ps));
            }
            if (customType == CustomItemType.DOOR_PRIVACY) {
                ps.getPlotdata().setPrivateplot(!ps.getPlotdata().getPrivateplot());
                player.sendMessage(MessageColor.YELLOW.apply("Visibilité du plot modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", ps));
            }
        }

        // ── PLOTVISIT navigation ──────────────────────────────────────────────
        else if (gamemenu.getTypemenu() == TypeMenu.PLOTVISIT) {
            if (customType == CustomItemType.ARROW_NEXT) {
                int p = playerPages.getOrDefault(player.getUniqueId(), 1);
                playerPages.put(player.getUniqueId(), p + 1);
                Inventory newMenu = MenuPlotVisit.createmenuplotvisit("Visite", p + 1);
                if (newMenu != null) player.openInventory(newMenu);
            } else if (customType == CustomItemType.ARROW_PREV) {
                int p = playerPages.getOrDefault(player.getUniqueId(), 1);
                if (p > 1) {
                    playerPages.put(player.getUniqueId(), p - 1);
                    Inventory newMenu = MenuPlotVisit.createmenuplotvisit("Visite", p - 1);
                    if (newMenu != null) player.openInventory(newMenu);
                }
            }
        }
    }

    // ── TP vers un plot avec position sûre ───────────────────────────────────
    private void teleportToPlot(Player player, PlayerServer ps1, World plotWorld) {
        int spawnX = ps1.getPlotdata().getLocationspawnX();
        int spawnY = ps1.getPlotdata().getLocationspawnY();
        int spawnZ = ps1.getPlotdata().getLocationspawnZ();

        int tx = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 0 : spawnX;
        int tz = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 0 : spawnZ;
        int ty = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 64 : spawnY;

        plugin.getLogger().info("[DEBUG-TP] Spawn sauvegardé: " + spawnX + "/" + spawnY + "/" + spawnZ);
        plugin.getLogger().info("[DEBUG-TP] Target: " + tx + "/" + ty + "/" + tz + " monde: " + plotWorld.getName());

        plotWorld.loadChunk(tx >> 4, tz >> 4, true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location start = new Location(plotWorld, tx + 0.5, ty, tz + 0.5);
            int highY = plotWorld.getHighestBlockYAt(tx, tz);
            plugin.getLogger().info("[DEBUG-TP] highY=" + highY + " startY=" + ty);
            Location safe = findSafeLocation(start);
            plugin.getLogger().info("[DEBUG-TP] Position safe finale: " + safe.getBlockX() + "/" + safe.getBlockY() + "/" + safe.getBlockZ());
            player.teleport(safe);
            player.closeInventory();
            player.sendMessage(MessageColor.GREEN.apply("Téléportation vers le plot de " + ps1.getName()));
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
}