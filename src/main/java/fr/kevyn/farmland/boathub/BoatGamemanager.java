package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatGamemanager {
	static JavaPlugin plugin;
	
	public BoatGamemanager(JavaPlugin plugin) {
		this.plugin = plugin;
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	public static void join(Player player) {
		if(!BoatGameHashMap.getListgameboat().isEmpty()) {
			ConfigStartEndZone gamecreate = new ConfigStartEndZone(plugin);	
		}
		ConfigStartEndZone game = BoatGameHashMap.getListgameboat().get(0);
		if(!boatgame.teleportplayertoboat(game, player)) {
			player.sendMessage("veuiller attendre la prochaine partie");
			
		}
		game.playeringame.add(player);
		

		
		
		
		
		
		
	}
	
	

}
