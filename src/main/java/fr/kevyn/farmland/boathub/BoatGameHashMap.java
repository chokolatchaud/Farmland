package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

public class BoatGameHashMap {
	static ArrayList<ConfigStartEndZone> listgameboat = new ArrayList<ConfigStartEndZone>();
	
	public static ArrayList<ConfigStartEndZone> getListgameboat() {
		return listgameboat;
	}
	public static void addListgameboat(ConfigStartEndZone config) {
		listgameboat.add(config);
		
	}

}
