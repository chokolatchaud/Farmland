package fr.kevyn.farmland.doonees.bateaux;

import fr.kevyn.farmland.directives.bateau.course.ConfigStartEndZone;
import fr.kevyn.farmland.directives.bateau.course.boatgame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatGamemanager {
	
	
	
	
	public static void join(Player player, JavaPlugin plugin) {
		if (BoatGameHashMap.getListgameboat().isEmpty()) {
			ConfigStartEndZone gamecreate = new ConfigStartEndZone(plugin);
		}
		ConfigStartEndZone game = BoatGameHashMap.getListgameboat().get(0);

		if (!boatgame.teleportplayertoboat(game, player, plugin)) {
			player.sendMessage("§cVeuillez attendre la prochaine partie !");
			return;
		}

	}
	
	

}
