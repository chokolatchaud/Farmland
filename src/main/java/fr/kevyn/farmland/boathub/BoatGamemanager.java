package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatGamemanager {
	
	
	
	
	public static void join(Player player, JavaPlugin plugin) {
		if(BoatGameHashMap.getListgameboat().isEmpty()) {
			ConfigStartEndZone gamecreate = new ConfigStartEndZone(plugin);	
		}
		ConfigStartEndZone game = BoatGameHashMap.getListgameboat().get(0);
		if(!boatgame.teleportplayertoboat(game, player)) {
			player.sendMessage("veuiller attendre la prochaine partie");
			
		}
		game.finishline.setglass(plugin);
		

		
		
		
		
		
		
	}
	
	

}
