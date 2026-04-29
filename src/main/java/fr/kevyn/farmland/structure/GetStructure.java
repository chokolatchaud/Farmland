package fr.kevyn.farmland.structure;

import java.util.ArrayList;
import java.util.List;

import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;
import fr.kevyn.farmland.region.TypeRegion;

public class GetStructure {
	
	public static List<GameRegion> getallStructure() {
		List<GameRegion> result = new ArrayList<>();

		 for(GameRegion gameregion : GameRegionHashMap.getInstance().getRegionhashmap()) {
			 if(gameregion.gettype()== TypeRegion.STRUCTURE) {
				 result.add(gameregion);
			 }

		 }
		 return result;

}
	
	
}
