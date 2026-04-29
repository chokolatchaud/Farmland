package fr.kevyn.farmland.menu;

import java.util.ArrayList;





public class GameMenuHashMap {
	ArrayList<GameMenu> menulist = new ArrayList<GameMenu>();
	private static final GameMenuHashMap INSTANCE = new GameMenuHashMap();
	
	public static GameMenuHashMap getInstance() {
        return INSTANCE;
    }
	

	
	public void AddMenulist(GameMenu gamemenu) {
		menulist.add(gamemenu);
	}
	
	public ArrayList<GameMenu> getMenulist() {
		return menulist;
	}
	
	
	

}
