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

import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.game.CustomItemType; // IMPORT DE L'ENUM
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
	// =========================
    // EVENT INVENTAIRE
    // =========================
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();    
        GameMenu gamemenu = null;
        for(GameMenu gamemenuforlist : GameMenuHashMap.getInstance().getMenulist()) {
        	if(event.getInventory().equals(gamemenuforlist.getInventory())) {
        		gamemenu = gamemenuforlist;
        		break;
        	}
        }
        
        if(gamemenu == null) {
            return;
        }
        event.setCancelled(true);

        



        


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // IDENTIFIER L'ITEM CUSTOM CLIQUÉ
        CustomItemType customType = CustomItemType.fromItem(clicked);
        if(customType == null) { return;}

        if (gamemenu.getTypemenu() == TypeMenu.PLOTUPGRADE) {

            // Vérifier si c'est l'upgrade disponible (rouge)
            if (customType == CustomItemType.UPGRADE_LOCKED) {

                ItemMeta meta = clicked.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;

                String name = ChatColor.stripColor(meta.getDisplayName());

                if (!name.contains("Coût")) return;

                int cost;
                try {
                    cost = Integer.parseInt(name.replace("Coût :", "").trim());
                } catch (NumberFormatException e) {
                    return;
                }

                PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                if (ps == null) {
                	player.kickPlayer("erreur 23");
                	return;
                }

                if (ps.getMoney() < cost) {
                    player.sendMessage(MessageColor.RED.apply("Tu n'as pas assez d'argent"));
                    return;
                }

                ps.setMoney(ps.getMoney() - cost);
                ps.setUpgrade(ps.getUpgrade() + 1);
                ps.getPlotdata().setWorldborder(ps.getPlotdata().getWorldborder() + 5);

                player.sendMessage(MessageColor.GREEN.apply("Upgrade acheté !"));
                player.closeInventory();
            }
            
        }
        else if (gamemenu.getTypemenu() == TypeMenu.PLOTCONFIG) {

        	PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (ps == null) {
            	player.kickPlayer("erreur 23");
            	return;
            }
            

            // CLOCK - Jour/Nuit
            if (customType == CustomItemType.CLOCK_DAYNIGHT) {
            	
                if(ps.getPlotdata().getMeteoTime().equalsIgnoreCase("day")) {
                	ps.getPlotdata().setMeteoTime("night", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                	player.sendMessage(MessageColor.DARK_BLUE.apply("La nuit approche"));
                	
                }else if(ps.getPlotdata().getMeteoTime().equalsIgnoreCase("night")) {
                	ps.getPlotdata().setMeteoTime("day", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                	player.sendMessage(MessageColor.YELLOW.apply("Le jour approche"));
                	
                }
               
            }
            
            // LAPIS - Pluie
            if(customType == CustomItemType.RAIN_TOGGLE) {
            	if(ps.getPlotdata().getMeteoRain().equalsIgnoreCase("weatherclear")) {
                	ps.getPlotdata().setMeteoRain("weatherain", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                	player.sendMessage(MessageColor.BLUE.apply("La pluie approche"));
                	
                }else if(ps.getPlotdata().getMeteoRain().equalsIgnoreCase("weatherain")) {
                	ps.getPlotdata().setMeteoRain("weatherclear", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                	player.sendMessage(MessageColor.BLUE.apply("La pluie s'eloigne"));
    
            	}
            	
            }
            
            // CYAN WOOL - Temps figé
            if(customType == CustomItemType.TIME_FREEZE) {
            	if(ps.getPlotdata().getMeteoActive().equalsIgnoreCase("minecraftActive")) {
                	ps.getPlotdata().setMeteoActive("minecraftDeactive", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                	player.sendMessage(MessageColor.GOLD.apply("La meteo se met a bouger"));
                	
                }else if(ps.getPlotdata().getMeteoActive().equalsIgnoreCase("minecraftDeactive")) {
                	ps.getPlotdata().setMeteoActive("minecraftActive", Bukkit.getWorld(ps.getPlotdata().getNameWorld()));
                	player.sendMessage(MessageColor.GOLD.apply("La meteo se fige"));
    
            	}
            	
            }

            // WATER BUCKET - Eau/Lave
            if (customType == CustomItemType.WATERLAVASELECTION) {
                ps.getPlotdata().setwaterlava(!ps.getPlotdata().getwaterlava());
                player.sendMessage(MessageColor.YELLOW.apply("Option eau/lave modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", ps));
            }

            // DOOR - Privé/Public
            if (customType == CustomItemType.DOOR_PRIVACY) {
                ps.getPlotdata().setPrivateplot(!ps.getPlotdata().getPrivateplot());
                player.sendMessage(MessageColor.YELLOW.apply("Visibilité du plot modifiée"));
                player.openInventory(MenuPlotConfig.createmenuplotconfig("Plot Configuration", ps));
            }
        }
        
        else if (gamemenu.getTypemenu() == TypeMenu.PLOTVISIT) {

        	// NAVIGATION - Page suivante
            if (customType == CustomItemType.ARROW_NEXT) {
                int currentPage = playerPages.getOrDefault(player.getUniqueId(), 1);
                playerPages.put(player.getUniqueId(), currentPage + 1);

                Inventory newMenu = MenuPlotVisit.createmenuplotvisit("Visite", currentPage);
                if (newMenu != null) player.openInventory(newMenu);
                return;
            }

            // NAVIGATION - Page précédente
            if (customType == CustomItemType.ARROW_PREV) {
                int currentPage = playerPages.getOrDefault(player.getUniqueId(), 1);
                if (currentPage > 1) {
                    playerPages.put(player.getUniqueId(), currentPage - 1);

                    Inventory newMenu = MenuPlotVisit.createmenuplotvisit("Visite", currentPage);
                    if (newMenu != null) player.openInventory(newMenu);
                }
                return;
            }

            // TÊTES DE JOUEURS - Téléportation
        	if (!(clicked.getItemMeta() instanceof SkullMeta)) return;

        	SkullMeta meta = (SkullMeta) clicked.getItemMeta();

        	OfflinePlayer owning = meta.getOwningPlayer();
        	if (owning == null) {
        	    player.sendMessage(MessageColor.RED.apply("Erreur : propriétaire introuvable !"));
        	    return;
        	}
        

        	UUID targetUUID = owning.getUniqueId();


                PlayerServer ps1 = PlayerserverHashMap.getInstance().getplayerHaspMaps(targetUUID);
                if (ps1 == null || ps1.getPlotdata() == null) {
                    player.sendMessage(MessageColor.RED.apply("Erreur : plot introuvable !"));
                    return;
                }

                String plotName = ps1.getPlotdata().getPlotProprety();
                World plotWorld = Plot.getWorldforname(plotName);
                if (plotWorld == null) {
                    player.sendMessage(MessageColor.RED.apply("Erreur : monde du plot introuvable !"));
                    return;
                }
                
                if(ps1.getPlotdata().getPrivateplot()) {
                	player.sendMessage(MessageColor.RED.apply("Ce plot est privée"));
                	return;
                	
                }

                Location loc = new Location(
                        plotWorld,
                        ps1.getPlotdata().getLocationspawnX(),
                        ps1.getPlotdata().getLocationspawnY(),
                        ps1.getPlotdata().getLocationspawnZ()
                );

                player.teleport(loc);
                player.closeInventory();
                player.sendMessage(MessageColor.GREEN.apply("Téléportation vers le plot de " + ps1.getName()));
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GameMenuHashMap.getInstance().getMenulist().removeIf(
            menu -> menu.getInventory().equals(event.getInventory())
        );
    }
  
}