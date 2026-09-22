package fr.kevyn.farmland.doonees.bateaux;

import fr.kevyn.farmland.directives.bateau.course.ConfigStartEndZone;

import java.util.ArrayList;

public class BoatGameHashMap {
	static ArrayList<ConfigStartEndZone> listgameboat = new ArrayList<ConfigStartEndZone>();
	
	public static ArrayList<ConfigStartEndZone> getListgameboat() {
		return listgameboat;
	}
	public static void addListgameboat(ConfigStartEndZone config) {
		listgameboat.add(config);
		
	}
	
	public static void removeListgameboat(ConfigStartEndZone config) {
		listgameboat.remove(config);
		
	}
	

}
