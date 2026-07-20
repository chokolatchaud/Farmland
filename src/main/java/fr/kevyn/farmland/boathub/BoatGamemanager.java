package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatGamemanager {
	
	
	
	
	public static void join(Player player, JavaPlugin plugin) {
		if (BoatGameHashMap.getListgameboat().isEmpty()) {
			ConfigStartEndZone gamecreate = new ConfigStartEndZone(plugin);
		}
		ConfigStartEndZone game = BoatGameHashMap.getListgameboat().get(0);

		if (!boatgame.teleportplayertoboat(game, player)) {
			player.sendMessage("§cVeuillez attendre la prochaine partie !");
			plugin.getLogger().info("[BoatRace][DEBUG] " + player.getName() + " n'a pas pu rejoindre (aucune place)");
			return;
		}

		plugin.getLogger().info("[BoatRace][DEBUG] " + player.getName() + " a rejoint la partie");
	}
	
	

}
