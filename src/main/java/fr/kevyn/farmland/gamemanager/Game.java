package fr.kevyn.farmland.gamemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.gamemaps.Gamemaps;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;

public class Game {
	Statuegame statue;
	private Gamemaps gamemap;
	ArrayList<Player> playerwait = new ArrayList<Player>();
	private int taskId = -1;
	JavaPlugin plugin;
	int chronowaitforhub = 30;
	int chronoforbuild = 30;
	HashMap<Player, ArrayList<GameRegion>> waypointspassed = new HashMap<>();
	Set<Player> warnedPlayers = new HashSet<>();
	Set<Player> finishedPlayers = new HashSet<>();
	
	
	

	public Game(Gamemaps gamemap, JavaPlugin plugin) {
		statue = Statuegame.WAITPLAYER;
		this.setGamemap(gamemap);
		this.plugin = plugin;
		}
	
	public void setGamemap(Gamemaps gamemap) {
		this.gamemap = gamemap;
	}
	
	
	
	
	

	// ✅ Lance une repeating task
	public void startTask() {
	taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
	tick();
	        }, 0L, 20L).getTaskId(); // toutes les 20 ticks = 1 seconde
	    }

	// ✅ Arrête la task
	public void stopTask() {
	if (taskId != -1) {
	  Bukkit.getScheduler().cancelTask(taskId);
	  taskId = -1;
	        }
	    }

	    private void tick() {
	        // logique qui tourne chaque seconde
	    	System.out.println("tick! statue: " + statue + " joueurs: " + playerwait.size() + " chrono: " + chronowaitforhub);
	    	if(playerwait.size() == 0) {
        		this.endgame();
        		return;
        		
        	}
	        switch (statue) {
	            case WAITPLAYER -> {
	            	
	            	if(playerwait.size() >= 2) {
	            		chronowaitforhub--;
	            		for(Player player : playerwait) {
	            			player.sendTitle("Temps restant: ", "" + chronowaitforhub, 0, 25, 5);
	            		}
	            		if(chronowaitforhub == 0) {
	            			launchbuilding(playerwait);	
	            			this.setStatue(Statuegame.BUILDING);  
	            			return;
	            		}
	            	}
	            	if(playerwait.size() == 8) {
	            		launchbuilding(playerwait);
	            		this.setStatue(Statuegame.BUILDING);
	            		return;
	            	}
	            }
	            case BUILDING -> {
	            	System.out.println("chrono build: " + chronoforbuild);
	            	chronoforbuild--;
	            	if(chronoforbuild <= 15) {
	            		for(Player player : playerwait) {
	            			player.sendTitle("Temps restant: ", "" + chronoforbuild, 0, 25, 5);
	            		}
	            		if(chronoforbuild == 0) {
		            		stopbuilding(playerwait);
	            	}
	            	
	            		
	            		
	            		this.setStatue(Statuegame.RACING);
	            		return;
	            		
	            	}
	            }
	            case RACING -> { 
	            	
	            	
	            }
			default -> System.out.println("erreur game");
	        }
	        
	        
	        
	        
	    }
	    
	   
	    
	    private void stopbuilding(ArrayList<Player> player) {
	        // ✅ 1. Désactive toutes les buildzones
	        for (GameRegion region : getGamemap().getBuildzone()) {
	            region.setCanbuild(false);
	        }

	        // ✅ 2. Pour chaque buildzone → tp propriétaire → crée voiture à la startingzone
	        for (int i = 0; i < getGamemap().getBuildzone().size(); i++) {
	            GameRegion buildzone = getGamemap().getBuildzone().get(i);
	            Player owner = Bukkit.getPlayer(buildzone.getPropriétaire());
	            if (owner == null) continue;

	            // ✅ Tp à la startingzone correspondante
	            if (i < getGamemap().getStartingzone().size()) {
	                GameRegion startzone = getGamemap().getStartingzone().get(i);
	                Location location = new Location(
	                    Bukkit.getWorld(startzone.getWorldname()),
	                    startzone.getSpawnX(), startzone.getSpawnY(), startzone.getSpawnZ()
	                );
	                owner.teleport(location);

	            }
	        }
	    }
			 
		 
		 
			// TODO Auto-generated method stub
			
		

	 private void launchbuilding(ArrayList<Player> player) {
		    System.out.println("launchbuilding appelé ! joueurs: " + player.size() + " buildzones: " + getGamemap().getBuildzone().size());
		    
		    int limit = Math.min(getGamemap().getBuildzone().size(), player.size());
		    System.out.println("limit: " + limit);
		    
		    for (int i = 0; i < limit; i++) {
		        GameRegion region = getGamemap().getBuildzone().get(i);
		        Player p = player.get(i);
		        System.out.println("tp " + p.getName() + " vers " + region.getName() + " world: " + region.getWorldname());
		        
		        Location location = new Location(
		            Bukkit.getWorld(region.getWorldname()),
		            region.getSpawnX(), region.getSpawnY(), region.getSpawnZ()
		        );
		        
		        p.teleport(location);
		        region.setPropriétaire(p.getUniqueId());
		        region.setCanbuild(true);
		        System.out.println("Region " + region.getName() + " proprio: " + region.getPropriétaire() + " canbuild: " + region.getCanbuild());
		        
		    }
		}
			
			

	 public void addplayerwait(Player player) {
		 playerwait.add(player);
		 Location location = new Location(Bukkit.getWorld(getGamemap().getHub().getWorldname()), getGamemap().getHub().getSpawnX(), getGamemap().getHub().getSpawnY(), getGamemap().getHub().getSpawnZ());
		 player.teleport(location);
	 	}
	 
	 public void removeplayerwait(Player player) {
		 playerwait.remove(player);
		 PlayerServer playerserver = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		 player.teleport(new Location(Bukkit.getWorld(playerserver.getPlotdata().getNameWorld()), playerserver.getPlotdata().getLocationspawnX(),
				 playerserver.getPlotdata().getLocationspawnY(),playerserver.getPlotdata().getLocationspawnZ()));
	 }
	 
	 public Statuegame getStatue() {
		return statue;
	}
	 public void setStatue(Statuegame statue) {
		this.statue = statue;
	}
	 
	 
	 public ArrayList<Player> getplayer() {
		 return playerwait;
	 }
	 
	 
	 public void playerEnteredEndzone(Player player) {
		    if (finishedPlayers.contains(player)) return;

		    ArrayList<GameRegion> passed = waypointspassed.getOrDefault(player, new ArrayList<>());
		    if (passed.size() < getGamemap().getWaypointzone().size()) {
		        if (!warnedPlayers.contains(player)) { // ✅ anti-spam warning
		            warnedPlayers.add(player);
		            player.sendMessage("§cTu n'as pas passé tous les waypoints !");
		        }
		        return;
		    }

		    finishedPlayers.add(player);
		    player.sendMessage("§a🏆 Tu as gagné !");
		    this.endgame();
		}
	 
	 
	 public void playerEnteredWaypoint(Player player, GameRegion gameregion) {
		    waypointspassed.computeIfAbsent(player, k -> new ArrayList<>());

		    if (!waypointspassed.get(player).contains(gameregion)) {
		        waypointspassed.get(player).add(gameregion);
		        warnedPlayers.remove(player); // ✅ peut retenter l'endzone
		        player.sendMessage("§aWaypoint passé ! §f" + waypointspassed.get(player).size() + "/" + getGamemap().getWaypointzone().size());
		    }
		}
	 
	 
	 
	 
	 





	 




	 
	 
	 
	 public void endgame() {
		    this.stopTask();
		    
		    for (Player player : new ArrayList<>(playerwait)) {
		        this.removeplayerwait(player);
		    }
		    
		    GameManager.getInstance().removeGame(this);
		    
		    for (GameRegion gameregion : this.getGamemap().getBuildzone()) {
		        gameregion.removebloc(plugin);
		        gameregion.setPropriétaire(null);
		        gameregion.setCanbuild(false);
		    }
		    
		}

	 public Gamemaps getGamemap() {
		return gamemap;
	 }


}
