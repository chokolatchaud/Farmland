package fr.kevyn.farmland.boathub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	HashMap<Integer, Player> playeringame = new HashMap<Integer, Player>();
	
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
	
	
	
	public Location getBlocstatueWaypoint1piste1() {
		return blocstatueWaypoint1player1;
	}
	
	public Location getBlocstatueWaypoint1piste2() {
		return blocstatueWaypoint1player2;
	}
	public Location getBlocstatueWaypoint1piste3() {
		return blocstatueWaypoint1player3;
	}
	
	public Location getBlocstatueWaypoint1piste4() {
		return blocstatueWaypoint1player4;
	}
	
	
	public Location getBlocstatueWaypoint2piste1() {
		return blocstatueWaypoint2player1;
	}
	
	public Location getBlocstatueWaypoint2piste2() {
		return blocstatueWaypoint2player2;
	}
	public Location getBlocstatueWaypoint2piste3() {
		return blocstatueWaypoint2player3;
	}
	
	public Location getBlocstatueWaypoint2piste4() {
		return blocstatueWaypoint2player4;
	}
	
	public Location getBlocstatueWaypoint3piste1() {
		return blocstatueWaypoint3player1;
	}
	
	public Location getBlocstatueWaypoint3piste2() {
		return blocstatueWaypoint3player2;
	}
	public Location getBlocstatueWaypoint3piste3() {
		return blocstatueWaypoint3player3;
	}
	
	public Location getBlocstatueWaypoint3piste4() {
		return blocstatueWaypoint3player4;
	}
	
	public ArrayList<Location> getpiste1(ConfigStartEndZone game) {
		ArrayList<Location> Locationwaypointpiste1 = new ArrayList<Location>();
		Locationwaypointpiste1.add(blocstatueWaypoint1player1);
		Locationwaypointpiste1.add(blocstatueWaypoint2player1);
		Locationwaypointpiste1.add(blocstatueWaypoint3player1);
		return Locationwaypointpiste1;
	}
	
	public ArrayList<Location> getpiste2(ConfigStartEndZone game) {
		ArrayList<Location> Locationwaypointpiste2 = new ArrayList<Location>();
		Locationwaypointpiste2.add(blocstatueWaypoint1player2);
		Locationwaypointpiste2.add(blocstatueWaypoint2player2);
		Locationwaypointpiste2.add(blocstatueWaypoint3player2);
		return Locationwaypointpiste2;
	}
	
	public ArrayList<Location> getpiste3(ConfigStartEndZone game) {
		ArrayList<Location> Locationwaypointpiste3 = new ArrayList<Location>();
		Locationwaypointpiste3.add(blocstatueWaypoint1player3);
		Locationwaypointpiste3.add(blocstatueWaypoint2player3);
		Locationwaypointpiste3.add(blocstatueWaypoint3player3);
		return Locationwaypointpiste3;
	}
	
	public ArrayList<Location> getpiste4(ConfigStartEndZone game) {
		ArrayList<Location> Locationwaypointpiste4 = new ArrayList<Location>();
		Locationwaypointpiste4.add(blocstatueWaypoint1player4);
		Locationwaypointpiste4.add(blocstatueWaypoint2player4);
		Locationwaypointpiste4.add(blocstatueWaypoint3player4);
		return Locationwaypointpiste4;
	}
	
	public HashMap<Integer, ArrayList<Location>> allpiste(ConfigStartEndZone game) {
		HashMap<Integer, ArrayList<Location>> pisteingame = new HashMap<Integer, ArrayList<Location>>();
		pisteingame.put(1, game.getpiste1(game));
		pisteingame.put(2, game.getpiste1(game));
		pisteingame.put(3, game.getpiste1(game));
		pisteingame.put(4, game.getpiste1(game));
		return pisteingame;
		
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
	
	public HashMap<Integer, Player> getPlayeringame() {
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
		HashMap<Integer,Player> playeringame = game.getPlayeringame();
		playeringame.put(i, player);
		
		
		
	}
	
	public void removeplayeringame(ConfigStartEndZone game,int i) {
		HashMap<Integer,Player> playeringame = game.getPlayeringame();
		playeringame.remove(i);
		
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
			HashMap<Integer,Player> playeringame = game.getPlayeringame();
			if(playeringame.isEmpty()) {
				game.killgame(plugin, game);
			}
			for(Player player : playeringame.values()) {
				if(!player.isInsideVehicle()) {
					game.getPlayeringame().remove(player);
				}
			}

			
			if(game.getStatus() == StatutBoatGame.waitplayer) {				
				game.timetolaunch -= 1;
				for(Player player : playeringame.values()) {
					player.sendMessage("Depart dans " + game.timetolaunch + " secondes");
					if(game.timetolaunch == 0) {
						game.setStatus(StatutBoatGame.race);
					}
				}
				
			}
			
			if(game.getStatus() == StatutBoatGame.race) {
				for(Map.Entry<Integer, Player> entry : playeringame.entrySet()) {
					int piste = entry.getKey();
			        Player player = entry.getValue();
				GameRegionHashMap.getInstance().Playerwhatistregion(player);
				if(GameRegionHashMap.getInstance().Playerwhatistregion(player) != null) {
					GameRegion waypoint = GameRegionHashMap.getInstance().Playerwhatistregion(player);
					if(waypoint.gettype() == TypeRegion.BoatraceWaypoint) {
						int playerinpiste = game.allpiste(game).;
						
						
						
					}
					
				}
				
				
				
				}
				
				
				}
				
			
			
			
			
			

        }, 20L, 20L);

	}
	
	
	

	
	

}
