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
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.Plot;

public class Plotinventory implements Listener {
	private final Map<UUID, Integer> playerPages = new HashMap<>();
	private final FarmlandMain plugin;

	public Plotinventory(FarmlandMain plugin) {
		this.plugin = plugin;
	}
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

        // Cas spécial PLOTVISIT : les têtes de joueur ne sont pas des CustomItemType
        if (gamemenu.getTypemenu() == TypeMenu.PLOTVISIT && clicked.getType() == Material.PLAYER_HEAD) {
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
            if (ps1.getPlotdata().getPrivateplot()) {
                player.sendMessage(MessageColor.RED.apply("Ce plot est privé"));
                return;
            }
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
                    // Monde pas chargé — on le charge d'abord
                    player.sendMessage(MessageColor.GRAY.apply("Chargement du plot en cours..."));
                    new fr.kevyn.plot.Plot(UUID.fromString(plotName), plugin);
                    // Attendre que le monde soit chargé puis TP
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        World loadedWorld = Plot.getWorldforname(plotName);
                        if (loadedWorld == null) {
                            player.sendMessage(MessageColor.RED.apply("Erreur : impossible de charger le plot !"));
                            return;
                        }
                        if (ps1.getPlotdata().getPrivateplot()) {
                            player.sendMessage(MessageColor.RED.apply("Ce plot est privé"));
                            return;
                        }
                        int spawnX2 = ps1.getPlotdata().getLocationspawnX();
                        int spawnY2 = ps1.getPlotdata().getLocationspawnY();
                        int spawnZ2 = ps1.getPlotdata().getLocationspawnZ();
                        Location loc2 = (spawnX2 == 0 && spawnY2 == 0 && spawnZ2 == 0)
                            ? loadedWorld.getSpawnLocation()
                            : new Location(loadedWorld, spawnX2, spawnY2, spawnZ2);
                        player.teleport(loc2);
                        player.closeInventory();
                        player.sendMessage(MessageColor.GREEN.apply("Téléportation vers le plot de " + ps1.getName()));
                    }, 60L);
                    return;
                }
                
                if(ps1.getPlotdata().getPrivateplot()) {
                	player.sendMessage(MessageColor.RED.apply("Ce plot est privée"));
                	return;
                	
                }

                Location loc;
                int spawnX = ps1.getPlotdata().getLocationspawnX();
                int spawnY = ps1.getPlotdata().getLocationspawnY();
                int spawnZ = ps1.getPlotdata().getLocationspawnZ();

                // Si spawn pas encore défini (0,0,0), utiliser le spawn du monde
                if (spawnX == 0 && spawnY == 0 && spawnZ == 0) {
                    loc = plotWorld.getSpawnLocation();
                } else {
                    loc = new Location(plotWorld, spawnX, spawnY, spawnZ);
                }

                player.teleport(loc);
                player.closeInventory();
                player.sendMessage(MessageColor.GREEN.apply("Téléportation vers le plot de " + ps1.getName()));
        }
    }
    
    private void teleportToPlot(Player player, PlayerServer ps1, World plotWorld) {
        int spawnX = ps1.getPlotdata().getLocationspawnX();
        int spawnY = ps1.getPlotdata().getLocationspawnY();
        int spawnZ = ps1.getPlotdata().getLocationspawnZ();

        Location loc;
        if (spawnX == 0 && spawnY == 0 && spawnZ == 0) {
            loc = findSafeLocation(new Location(plotWorld, 0, 64, 0));
        } else {
            // +1 car setspawnpoint sauvegarde le Y des pieds = niveau du sol
            loc = findSafeLocation(new Location(plotWorld, spawnX + 0.5, spawnY + 1, spawnZ + 0.5));
        }

        player.teleport(loc);
        player.closeInventory();
        player.sendMessage(MessageColor.GREEN.apply("Téléportation vers le plot de " + ps1.getName()));
    }

    private Location findSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return loc;

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        // Toujours partir du bloc le plus haut de la colonne
        int highY = world.getHighestBlockYAt(x, z);

        // Si le monde est vide (highY très bas), on utilise Y=64 comme défaut
        if (highY < world.getMinHeight() + 5) {
            highY = 64;
        }

        int startY = Math.max(loc.getBlockY(), highY);
        int maxY = world.getMaxHeight() - 2;

        // Cherche depuis startY vers le haut 2 blocs non-solides avec sol en dessous
        for (int y = startY; y <= maxY; y++) {
            org.bukkit.block.Block feet  = world.getBlockAt(x, y, z);
            org.bukkit.block.Block head  = world.getBlockAt(x, y + 1, z);
            org.bukkit.block.Block ground = world.getBlockAt(x, y - 1, z);
            if (!feet.getType().isSolid() && !head.getType().isSolid() && ground.getType().isSolid()) {
                return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
            }
        }

        // Fallback absolu
        return new Location(world, x + 0.5, highY + 1, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    private Location findSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return loc;

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        // getHighestBlockYAt retourne le Y du bloc solide le plus haut + 1 (air au-dessus)
        int safeY = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, safeY + 1, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GameMenuHashMap.getInstance().getMenulist().removeIf(
            menu -> menu.getInventory().equals(event.getInventory())
        );
    }
  
}