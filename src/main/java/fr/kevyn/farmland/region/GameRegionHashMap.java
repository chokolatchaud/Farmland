package fr.kevyn.farmland.region;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.RegionSave;

public class GameRegionHashMap {
	
	ArrayList<GameRegion> GameRegionHashmap = new ArrayList<GameRegion>();
	private static final GameRegionHashMap INSTANCE = new GameRegionHashMap();
	
	public static GameRegionHashMap getInstance() {
        return INSTANCE;
    }
	
	
	public ArrayList<GameRegion> getRegionhashmap() {
		return GameRegionHashmap;
		
	}
	
	
	
	
	public void addregion(GameRegion region) {
		GameRegionHashmap.add(region);
	}
	
	public void removeregion(GameRegion region) {
		GameRegionHashmap.remove(region);
	}
	

	public GameRegion Playerwhatistregion(Player player) {
		for(GameRegion region : GameRegionHashmap) {
			Location loc = player.getLocation();
			if(loc.getX() >= region.minX && loc.getBlockX() <= region.maxX &&
			   loc.getY() >= region.minY && loc.getBlockY() <= region.maxY &&
			   loc.getZ() >= region.minZ && loc.getBlockZ() <= region.maxZ) {
				
				return region;}

		}
		return null;
		}
			
		
	public GameRegion Blockwhatistregion(Block bloc) {
		for(GameRegion region : GameRegionHashmap) {
			Location loc = bloc.getLocation();
			if(loc.getX() >= region.minX && loc.getBlockX() <= region.maxX &&
			   loc.getY() >= region.minY && loc.getBlockY() <= region.maxY &&
			   loc.getZ() >= region.minZ && loc.getBlockZ() <= region.maxZ) {
				return region;}

		}
		return null;
		}
	public GameRegion getregionbyname(String name) {
		for(GameRegion gameregion : GameRegionHashmap) {
			if(gameregion.getName().equalsIgnoreCase(name)) {
				return gameregion;
			}
			
		}
		return null;
		
	}
	public GameRegion LocationwhatisRegion(Location location) {
	    for (GameRegion region : GameRegionHashmap) {
	        if (location.getX() >= region.getMinX() && location.getX() <= region.getMaxX() &&
	            location.getY() >= region.getMinY() && location.getY() <= region.getMaxY() &&
	            location.getZ() >= region.getMinZ() && location.getZ() <= region.getMaxZ() &&
	            location.getWorld().getName().equals(region.getWorldname())) {
	            return region;
	        }
	    }
	    return null;
	}
   
}
