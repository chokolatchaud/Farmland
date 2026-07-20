package fr.kevyn.farmland.region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class GameRegion {
	double minX;
	double minY;
	double minZ;
	double maxX;
	double maxY;
	double maxZ;
	double spawnX;
	double spawnY;
	double spawnZ;
	String worldname;
	String circuit;
	String name;
	TypeRegion type;
	UUID propriétaire;
	Boolean canbuild;
	float score = 0;

	
	
	
	
	public GameRegion() {}
	
	public GameRegion(double minX,double minY,double minZ,double maxX,double maxY,double maxZ,double spawnX,double spawnY,double spawnZ,String name,Boolean canbuild, String worldname,TypeRegion type
			,String circuit) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.worldname = worldname;
		this.name = name;
		this.canbuild = canbuild;
		this.propriétaire = null;
		this.type = type;
		this.circuit = circuit;
		this.score = 0;
		
		GameRegionHashMap.getInstance().addregion(this);
		
	}
	public String getCircuit() {
		return circuit;
	}
	public void setCircuit(String circuit) {
		this.circuit = circuit;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}

	public double getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(double spawnX) {
		this.spawnX = spawnX;
	}

	public double getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(double spawnY) {
		this.spawnY = spawnY;
	}

	public double getSpawnZ() {
		return spawnZ;
	}

	public void setSpawnZ(double spawnZ) {
		this.spawnZ = spawnZ;
	}

	public String getWorldname() {
		return worldname;
	}

	public void setWorldname(String worldname) {
		this.worldname = worldname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeRegion gettype() {
		return type;
	}


	public UUID getPropriétaire() {
		return propriétaire;
	}

	public void setPropriétaire(UUID propriétaire) {
		this.propriétaire = propriétaire;
	}

	public Boolean getCanbuild() {
		return canbuild;
	}

	public void setCanbuild(Boolean canbuild) {
		this.canbuild = canbuild;
	}
	public TypeRegion getType() {
		return type;
	}
	
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}

	
	public void removebloc(JavaPlugin plugin) {
	    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	        // ✅ calcul des blocs en async
	        List<Location> toRemove = new ArrayList<>();
	        for (int x = (int)minX; x <= maxX; x++)
	            for (int y = (int)minY; y <= maxY; y++)
	                for (int z = (int)minZ; z <= maxZ; z++)
	                    toRemove.add(new Location(Bukkit.getWorld(worldname), x, y, z));
	        
	        // ✅ modification des blocs sur le thread principal
	        Bukkit.getScheduler().runTask(plugin, () -> {
	            for (Location loc : toRemove)
	                loc.getBlock().setType(Material.AIR);
	        });
	    });
	}
	
	public void setglass(JavaPlugin plugin) {
	    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	        // ✅ calcul des blocs en async
	        List<Location> toRemove = new ArrayList<>();
	        for (int x = (int)minX; x <= maxX; x++)
	            for (int y = (int)minY; y <= maxY; y++)
	                for (int z = (int)minZ; z <= maxZ; z++)
	                    toRemove.add(new Location(Bukkit.getWorld(worldname), x, y, z));
	        
	        // ✅ modification des blocs sur le thread principal
	        Bukkit.getScheduler().runTask(plugin, () -> {
	            for (Location loc : toRemove)
	                loc.getBlock().setType(Material.GLASS);
	        });
	    });
	}

	/** Retire le verre pose par setglass() en remettant AIR (a appeler en fin de partie) */
	public void removeglass(JavaPlugin plugin) {
	    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	        List<Location> toClear = new ArrayList<>();
	        for (int x = (int)minX; x <= maxX; x++)
	            for (int y = (int)minY; y <= maxY; y++)
	                for (int z = (int)minZ; z <= maxZ; z++)
	                    toClear.add(new Location(Bukkit.getWorld(worldname), x, y, z));

	        Bukkit.getScheduler().runTask(plugin, () -> {
	            for (Location loc : toClear)
	                loc.getBlock().setType(Material.AIR);
	        });
	    });
	}
	
	

	
	
	
	
	
	
	
	   
	   
   }

