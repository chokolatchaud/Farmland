package fr.kevyn.farmland.boathub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;
import fr.kevyn.farmland.region.TypeRegion;

public class ConfigStartEndZone {
	World world = Bukkit.getWorld("world");
	
	Location zonespawn1 = new Location(world, 83, 34, -36);
	Location zonespawn2 = new Location(world, 83, 34, -39);
	Location zonespawn3 = new Location(world, 83, 34, -42);
	Location zonespawn4 = new Location(world, 83, 34, -45);
	
	
	Location blocstatuszonespawn1 = new Location(world, 83, 32, -36);
	Location blocstatuszonespawn2 = new Location(world, 83, 32, -39);
	Location blocstatuszonespawn3 = new Location(world, 83, 32, -42);
	Location blocstatuszonespawn4 = new Location(world, 83, 32, -45);
	
	GameRegion Waypoint1 = new GameRegion(49,39,-4,35,33,-4,0,0,0,"Waypoint1",false,"world",TypeRegion.BoatraceWaypoint,null);
	Location blocstatueWaypoint1player1 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint1player2 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint1player3 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint1player4 = new Location(world, 83, 32, -36);

	GameRegion Waypoint2 = new GameRegion(49,39,-4,35,33,-4,0,0,0,"Waypoint2",false,"world",TypeRegion.BoatraceWaypoint,null);
	Location blocstatueWaypoint2player1 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint2player2 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint2player3 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint2player4 = new Location(world, 83, 32, -36);
	
	GameRegion Waypoint3 = new GameRegion(49,39,-4,35,33,-4,0,0,0,"Waypoint3",false,"world",TypeRegion.BoatraceWaypoint,null);
	Location blocstatueWaypoint3player1 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint3player2 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint3player3 = new Location(world, 83, 32, -36);
	Location blocstatueWaypoint3player4 = new Location(world, 83, 32, -36);

	
	
	GameRegion finishline = new GameRegion(80,33,-34,80,39,-48,0,0,0,"finishlineboat",false,"world",TypeRegion.Boatrace,null);
	HashMap<Player,Integer> playeringame = new HashMap<Player,Integer>();
	
	int timetolaunch = 30;
	int timegame = 0;
	StatutBoatGame status = StatutBoatGame.waitplayer;
	
	
	
	public ConfigStartEndZone(JavaPlugin plugin) {
		BoatGameHashMap.addListgameboat(this);
		finishline.setglass(plugin);
		starttime(plugin, this);

	}
	
	public GameRegion getWaypoint1() {
		return Waypoint1;
	}
	
	public Location getBlocstatueWaypoint1player1() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint1player2() {
		return blocstatueWaypoint1player1;
	}
	public Location getBlocstatueWaypoint1player3() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint1player4() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint2player1() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint2player2() {
		return blocstatueWaypoint1player1;
	}
	public Location getBlocstatueWaypoint2player3() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint2player4() {
	}
	
	public Location getBlocstatueWaypoint3player1() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint3player2() {
		return blocstatueWaypoint1player1;
	}
	public Location getBlocstatueWaypoint3player3() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint3player4() {
		return blocstatueWaypoint1player1;
	}
	
	public GameRegion getWaypoint2() {
		return Waypoint2;
	}
	
	public GameRegion getWaypoint3() {
		return Waypoint3;
	}
	
	public Location getZonespawn1() {
		return zonespawn1;
	}
	public Location getZonespawn2() {
		return zonespawn2;
	}
	public Location getZonespawn3() {
		return zonespawn3;
	}
	public Location getZonespawn4() {
		return zonespawn4;
	}
	public GameRegion getFinishline() {
		return finishline;
	}
	
	public Location getBlocstatuszonespawn1() {
		return blocstatuszonespawn1;
	}	
	
	public Location getBlocstatuszonespawn2() {
		return blocstatuszonespawn2;
	}
	
	public Location getBlocstatuszonespawn3() {
		return blocstatuszonespawn3;
	}
	public Location getBlocstatuszonespawn4() {
		return blocstatuszonespawn4;
	}
	public World getWorld() {
		return world;
	}
	
	public int getTimetolaunch() {
		return timetolaunch;
	}
	
	public int getTimegame() {
		return timegame;
	}
	
	public void add1secondeTimegame() {
		this.timegame++;
	}
	
	public HashMap<Player, Integer> getPlayeringame() {
		return playeringame;
	}
	
	public StatutBoatGame getStatus() {
		return status;
	}
	public void setStatus(StatutBoatGame status) {
		this.status = status;
	}
	
	public void killgame(JavaPlugin plugin, ConfigStartEndZone game) {
		Bukkit.getScheduler().cancelTasks(plugin);
		BoatGameHashMap.removeListgameboat(game);
		
	}
	
	public void addplayeringame(ConfigStartEndZone game,Player player, int i) {
		HashMap<Player, Integer> playeringame = game.getPlayeringame();
		playeringame.put(player, i);
		
		
		
	}
	
	public void removeplayeringame(ConfigStartEndZone game,Player player) {
		HashMap<Player, Integer> playeringame = game.getPlayeringame();
		playeringame.remove(player);
		
	}
	
	public ArrayList<Location> allgetzonespawn(ConfigStartEndZone zonespawn) {
		ArrayList<Location> allspawn = new ArrayList<Location>();
		allspawn.add(zonespawn.zonespawn1);
		allspawn.add(zonespawn.zonespawn2);
		allspawn.add(zonespawn.zonespawn3);
		allspawn.add(zonespawn.zonespawn4);
		return allspawn;
		
	}
	
	
	
	public ArrayList<Location> allgetzoneblocstatus(ConfigStartEndZone zonebloc) {
		ArrayList<Location> allblocstatus = new ArrayList<Location>();
		allblocstatus.add(zonebloc.blocstatuszonespawn1);
		allblocstatus.add(zonebloc.blocstatuszonespawn2);
		allblocstatus.add(zonebloc.blocstatuszonespawn3);
		allblocstatus.add(zonebloc.blocstatuszonespawn4);
		return allblocstatus;
		
	}
	
	public ArrayList<GameRegion> allgetWaypoint(ConfigStartEndZone waypoint) {
		ArrayList<GameRegion> allwaypoint = new ArrayList<GameRegion>();
		allwaypoint.add(waypoint.Waypoint1);
		allwaypoint.add(waypoint.Waypoint2);
		allwaypoint.add(waypoint.Waypoint3);
		return allwaypoint;
		
	}
	
	public static void starttime(JavaPlugin plugin,ConfigStartEndZone game){
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			game.add1secondeTimegame();
			HashMap<Player, Integer> playeringame = game.getPlayeringame();
			if(playeringame.isEmpty()) {
				game.killgame(plugin, game);
			}
			for(Player player : playeringame.keySet()) {
				if(!player.isInsideVehicle()) {
					game.getPlayeringame().remove(player);
				}
			}

			
			if(game.getStatus() == StatutBoatGame.waitplayer) {				
				game.timetolaunch -= 1;
				for(Player player : playeringame.keySet()) {
					player.sendMessage("Depart dans " + game.timetolaunch + " secondes");
					if(game.timetolaunch == 0) {
						game.setStatus(StatutBoatGame.race);
					}
				}
				
			}
			
			if(game.getStatus() == StatutBoatGame.race) {
				for(Integer player : playeringame.values()) {
				GameRegionHashMap.getInstance().Playerwhatistregion(player);
				if(GameRegionHashMap.getInstance().Playerwhatistregion(player) != null) {
					GameRegion waypoint = GameRegionHashMap.getInstance().Playerwhatistregion(player);
					if(waypoint.gettype() == TypeRegion.BoatraceWaypoint) {
						
						
					}
					
				}
				
				
				
				}
				
				
				}
				
			
			
			
			
			

        }, 20L, 20L);

	}
	
	
	

	
	

}
