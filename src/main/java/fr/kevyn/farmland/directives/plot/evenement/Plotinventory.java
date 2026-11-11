package fr.kevyn.farmland.directives.plot.evenement;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.directives.cosmetic.CosmeticShop;
import fr.kevyn.farmland.directives.gameplay.objet.CustomItemType;
import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.directives.plot.gestion.Meteo;
import fr.kevyn.farmland.directives.plot.commands.Plotcommands;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotConfig;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotUpgrade;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotVisit;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.GameMenuHashMap;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public final class Plotinventory implements Listener {
    private final FarmlandMain plugin;

    public Plotinventory(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Tous les clics sur les menus de plots passent par ce point d'entrée.

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

        // Visite d'un plot depuis le menu.
        if (gameMenu.getTypemenu() == TypeMenu.PLOTVISIT
                && clickedItem.getType() == Material.PLAYER_HEAD) {
            handlePlotVisit(player, clickedItem);
            return;
        }

        // ── COSMETICS : achat/equipement d'un chapeau ─────────────────────────
        if (gameMenu.getTypemenu() == TypeMenu.COSMETICS) {
            int slot = event.getSlot();
            if (slot < 0 || slot >= CosmeticShop.COSMETICS.size()) {
                return;
            }

            CosmeticShop.Cosmetic cosmetic =
                    CosmeticShop.COSMETICS.get(slot);

            PlayerServer playerServer = PlayerserverHashMap.getInstance()
                    .getplayerHaspMaps(player.getUniqueId());
            if (playerServer == null) {
                return;
            }

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
            JournalEvenements.enregistrer("achat_cosmetique", player.getName(), cosmetic.id + " -" + cosmetic.price + " $FB, solde " + playerServer.getMoney() + " $FB");
            player.getInventory().setHelmet(cosmetic.createItem());
            player.sendMessage(MessageColor.GREEN.apply("✔ " + cosmetic.name + " acheté et équipé ! (-" + cosmetic.price + " $FB)"));
            player.closeInventory();
            return;
        }

        // ── CUSTOM ITEMS ──────────────────────────────────────────────────────
        CustomItemType customItemType = CustomItemType.fromItem(clickedItem);
        if (customItemType == null) return;

        // ── PLOTUPGRADE ───────────────────────────────────────────────────────
        if (gameMenu.getTypemenu() == TypeMenu.PLOTUPGRADE) {
            if (customItemType == CustomItemType.UPGRADE_LOCKED) {
                PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                if (playerServer == null) { player.kickPlayer("erreur 23"); return; }
                if (playerServer.getPlotdata() == null) { player.sendMessage(MessageColor.RED.apply("Erreur : plot introuvable !")); return; }

                int rank = playerServer.getUpgrade();

                // Le systeme est infini : plus de "tu as tout achete", juste un palier suivant
                // Le slot clique doit correspondre au PROCHAIN upgrade DANS LE PALIER ACTUEL
                int upgradesPerPage = MenuPlotUpgrade.getMaxUpgrades();
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
                JournalEvenements.enregistrer("achat_upgrade", player.getName(), "-" + cost + " $FB, rang " + playerServer.getUpgrade() + ", bordure " + playerServer.getPlotdata().getWorldborder() + ", solde " + playerServer.getMoney() + " $FB");
                player.sendMessage(MessageColor.GREEN.apply("Upgrade acheté ! (-" + cost + " $FB, +5 de bordure)"));

                // nouveau palier atteint : prevenir le joueur que les prix montent
                if ((rank + 1) % upgradesPerPage == 0) {
                    player.sendMessage(MessageColor.GOLD.apply("✦ Nouveau palier ! Les prix des prochains upgrades montent de 20 $FB."));
                }

                player.closeInventory();
            }
        }

        // ── PLOTCONFIG ────────────────────────────────────────────────────────
        else if (gameMenu.getTypemenu() == TypeMenu.PLOTCONFIG) {

            PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (playerServer == null) { player.kickPlayer("erreur 23"); return; }
            World plot = Bukkit.getWorld(playerServer.getPlotdata().getNameWorld());
            if (plot == null) {
                player.kickPlayer("erreur 23");
                return;
            }

            if (customItemType == CustomItemType.CLOCK_DAYNIGHT) {
                if (playerServer.getPlotdata().getMeteoTime() == Meteo.DAY) {
                    playerServer.getPlotdata().setMeteoTime(Meteo.NIGHT);
                    player.sendMessage(MessageColor.DARK_BLUE.apply("La nuit approche"));
                    plot.setTime(13000);
                } else {
                    playerServer.getPlotdata().setMeteoTime(Meteo.DAY);
                    player.sendMessage(MessageColor.YELLOW.apply("Le jour approche"));
                    plot.setTime(1000);
                }
                JournalEvenements.enregistrerDetail("plot_meteo", player.getName(), playerServer.getPlotdata().getMeteoTime());
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
            if (customItemType == CustomItemType.RAIN_TOGGLE) {
                if (playerServer.getPlotdata().getMeteoRain() == Meteo.RAINSTOP) {
                    playerServer.getPlotdata().setMeteoRain(Meteo.RAIN);
                    plot.setStorm(true);
                    plot.setWeatherDuration(Integer.MAX_VALUE);

                    player.sendMessage(MessageColor.BLUE.apply("La pluie approche"));
                } else {
                    playerServer.getPlotdata().setMeteoRain(Meteo.RAINSTOP);
                    plot.setStorm(false);
                    plot.setClearWeatherDuration(Integer.MAX_VALUE);

                    player.sendMessage(MessageColor.BLUE.apply("La pluie s'éloigne"));
                }
                JournalEvenements.enregistrerDetail("plot_meteo", player.getName(), playerServer.getPlotdata().getMeteoRain());
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
            if (customItemType == CustomItemType.TIME_FREEZE) {
                if (playerServer.getPlotdata().getMeteoActive() == Meteo.TIMEFALSE){
                    playerServer.getPlotdata().setMeteoActive(Meteo.TIMETRUE);
                    plot.setGameRule(GameRules.ADVANCE_TIME, true);
                    player.sendMessage(MessageColor.GOLD.apply("La météo se met à bouger"));
                } else {
                    playerServer.getPlotdata().setMeteoActive(Meteo.TIMEFALSE);
                    plot.setGameRule(GameRules.ADVANCE_TIME, false);
                    player.sendMessage(MessageColor.GOLD.apply("La météo se fige"));
                }
                JournalEvenements.enregistrerDetail("plot_meteo", player.getName(), playerServer.getPlotdata().getMeteoActive());
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
            if (customItemType == CustomItemType.WATERLAVASELECTION) {
                playerServer.getPlotdata().setwaterlava(!playerServer.getPlotdata().getwaterlava());
                JournalEvenements.enregistrer("plot_config", player.getName(), "eau/lave " + (playerServer.getPlotdata().getwaterlava() ? "interdite" : "autorisée"));
                player.sendMessage(MessageColor.YELLOW.apply("Option eau/lave modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
            if (customItemType == CustomItemType.DOOR_PRIVACY) {
                playerServer.getPlotdata().setPrivateplot(!playerServer.getPlotdata().getPrivateplot());
                JournalEvenements.enregistrer("plot_config", player.getName(), playerServer.getPlotdata().getPrivateplot() ? "privé" : "public");
                player.sendMessage(MessageColor.YELLOW.apply("Visibilité du plot modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }

            if (customItemType == CustomItemType.MOB_SPAWN) {
                playerServer.getPlotdata().setMobSpawn(!playerServer.getPlotdata().getMobSpawn());
                JournalEvenements.enregistrer("plot_config", player.getName(), "spawn des mobs " + (playerServer.getPlotdata().getMobSpawn() ? "activé" : "désactivé"));
                player.sendMessage(MessageColor.YELLOW.apply("Spawn des mobs modifié"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", playerServer));
            }
        }

        // ── PLOTVISIT navigation ──────────────────────────────────────────────
        else if (gameMenu.getTypemenu() == TypeMenu.PLOTVISIT) {
            // La page vient du menu ouvert : chaque ouverture de /plot visit repart de la page 1.
            int p = gameMenu.getPage();
            if (customItemType == CustomItemType.ARROW_NEXT) {
                Inventory newInventory = MenuPlotVisit.createmenuplotvisit("Visiter un plot", p + 1, player);
                if (newInventory != null) player.openInventory(newInventory);
            } else if (customItemType == CustomItemType.ARROW_PREV && p > 1) {
                Inventory newInventory = MenuPlotVisit.createmenuplotvisit("Visiter un plot", p - 1, player);
                if (newInventory != null) player.openInventory(newInventory);
            }
        }
    }

    private void handlePlotVisit(Player player, ItemStack clickedItem) {
        if (!(clickedItem.getItemMeta() instanceof SkullMeta)) {
            return;
        }

        SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
        OfflinePlayer owningPlayer = meta.getOwningPlayer();
        if (owningPlayer == null) {
            player.sendMessage(MessageColor.RED.apply("Erreur : propriétaire introuvable !"));
            return;
        }

        PlayerServer targetServer =
                PlayerserverHashMap.getInstance().getplayerHaspMaps(owningPlayer.getUniqueId());
        // Même logique que /plot visit <nom> : contrôle "privé", chargement du plot, teleportSafe.
        if (Plotcommands.visiterPlot(plugin, player, targetServer)) {
            // Fermeture au tick suivant : on ne ferme pas un inventaire pendant son propre clic.
            Bukkit.getScheduler().runTask(plugin, () -> player.closeInventory());
        }
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