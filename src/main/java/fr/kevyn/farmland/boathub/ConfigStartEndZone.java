package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.TypeRegion;

public class ConfigStartEndZone {
	World world = Bukkit.getWorld("world");
	
	Location zonespawn1 = new Location(null, 83, 33, -36);
	Location zonespawn2 = new Location(null, 83, 33, -39);
	Location zonespawn3 = new Location(null, 83, 33, -42);
	Location zonespawn4 = new Location(null, 83, 33, -45);
	
	
	Location blocstatuszonespawn1 = new Location(null, 83, 32, -36);
	Location blocstatuszonespawn2 = new Location(null, 83, 32, -36);
	Location blocstatuszonespawn3 = new Location(null, 83, 32, -36);
	Location blocstatuszonespawn4 = new Location(null, 83, 32, -36);
	
	
	GameRegion finishline = new GameRegion(80,33,-34,80,39,-48,0,0,0,"boat",false,"world",TypeRegion.Boatrace,null);
	ArrayList<Player> playeringame = new ArrayList<Player>();
	
	int timetolaunch = 30;
	int timegame = 0;
	
	
	
	public ConfigStartEndZone(JavaPlugin plugin) {
		BoatGameHashMap.addListgameboat(this);
		finishline.setglass(plugin);;

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
		this.timegame = timegame++;
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
	
	
	

	
	

}
