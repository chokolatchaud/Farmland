package fr.kevyn.farmland.gamemaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;
import fr.kevyn.farmland.region.TypeRegion;


public class GameMapsCreator {
	
	
	
	
	

	public static List<Gamemaps> getallmaps() {
		Map<String, GameRegion> mapshub              = new HashMap<>();
		Map<String, List<GameRegion>> mapsbuildzone  = new HashMap<>();
		Map<String, List<GameRegion>> startingzone   = new HashMap<>();
		Map<String, List<GameRegion>> waypointzone   = new HashMap<>();
		Map<String, GameRegion> endzone              = new HashMap<>();

		 
		 
		 for(GameRegion gameregion : GameRegionHashMap.getInstance().getRegionhashmap()) {
			 if(gameregion.gettype()== TypeRegion.HUBGAME) {mapshub.put(gameregion.getCircuit(), gameregion);}
			 else if(gameregion.gettype() == TypeRegion.BUILDZONEGAME) { mapsbuildzone.computeIfAbsent(gameregion.getCircuit(), k -> new ArrayList<>()).add(gameregion); }
			 else if(gameregion.gettype() == TypeRegion.STARTINGZONEGAME) { startingzone.computeIfAbsent(gameregion.getCircuit(), k -> new ArrayList<>()).add(gameregion);}
			 else if(gameregion.gettype() == TypeRegion.WAYPOINTZONEGAME) {waypointzone.computeIfAbsent(gameregion.getCircuit(), k -> new ArrayList<>()).add(gameregion);}
			 else if(gameregion.gettype()== TypeRegion.ENDZONEGAME) {endzone.put(gameregion.getCircuit(), gameregion);}

		 }
		 
		 List<Gamemaps> result = new ArrayList<>();
		 for (String circuit : mapshub.keySet()) {
			 result.add(new Gamemaps(circuit,
					    mapshub.get(circuit),                              // hub ✅
					    mapsbuildzone.getOrDefault(circuit, new ArrayList<>()),  // buildzone ✅
					    startingzone.getOrDefault(circuit, new ArrayList<>()),   // startingzone ✅
					    waypointzone.getOrDefault(circuit, new ArrayList<>()),   // waypointzone ✅
					    8,                                                 // numberplayermax ✅
					    2,                                                 // numberplayermin ✅
					    300,                                               // buildTime ✅
					    180,                                               // raceTime ✅
					    endzone.get(circuit)                               // end ✅ à la fin !
					));
		 }
		 
		 System.out.println("Total régions: " + GameRegionHashMap.getInstance().getRegionhashmap().size());
		 for (GameRegion r : GameRegionHashMap.getInstance().getRegionhashmap()) {
		     System.out.println("Région: " + r.getName() + " type: " + r.gettype() + " circuit: " + r.getCircuit());
		 }
		 return result;
		
		
	 }
		
		
}
	
	
	
	


