package fr.kevyn.farmland.gamemaps;

import java.util.List;

import fr.kevyn.farmland.region.GameRegion;

public class Gamemaps {
	String name;
	GameRegion hub;
	List<GameRegion> buildzone;
	List<GameRegion> startingzone;
	List<GameRegion> waypointzone;
	GameRegion end;
	int numberplayermax;
	int numberplayermin; 
	int buildTime;        
	int raceTime; 
	
	public Gamemaps(String name, GameRegion hub,List<GameRegion> buildzone,List<GameRegion> startingzone,List<GameRegion> waypointzone,int numberplayermax,int numberplayermin, 
	int buildTime,        
	int raceTime, GameRegion end) {
		this.name = name;
		this.hub = hub;
		this.buildzone = buildzone;
		this.startingzone = startingzone;
		this.waypointzone = waypointzone;
		this.numberplayermax = numberplayermax;
		this.numberplayermin = numberplayermin;
		this.buildTime = buildTime;
		this.raceTime = raceTime;
		this.end = end;
		
		
	}
	
	public int getBuildTime() {
		return buildTime;
	}
	public List<GameRegion> getBuildzone() {
		return buildzone;
	}
	public GameRegion getEnd() {
		return end;
	}
	public GameRegion getHub() {
		return hub;
	}
	public int getNumberplayermax() {
		return numberplayermax;
	}
	public int getNumberplayermin() {
		return numberplayermin;
	}
	public int getRaceTime() {
		return raceTime;
	}
	public List<GameRegion> getStartingzone() {
		return startingzone;
	}
	public List<GameRegion> getWaypointzone() {
		return waypointzone;
	}
	public String getName() {
		return name;
	}
	
	
	
	
	
	
}


