package fr.kevyn.farmland.doonees.bateaux;

import fr.kevyn.farmland.directives.bateau.course.ConfigStartEndZone;
import fr.kevyn.farmland.directives.bateau.course.boatgame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatGamemanager {
	
	
	
	
	public static void join(Player player, JavaPlugin plugin) {
		// /joinboat pendant une partie = quitter (avant : le joueur restait sur place)
		for (ConfigStartEndZone partie : BoatGameHashMap.getListgameboat()) {
			if (partie.getProgression().containsKey(player)) {
				boatgame.quitterPartie(player, partie, plugin);
				return;
			}
		}

		if (BoatGameHashMap.getListgameboat().isEmpty()) {
			ConfigStartEndZone gamecreate = new ConfigStartEndZone(plugin);
		}
		ConfigStartEndZone game = BoatGameHashMap.getListgameboat().get(0);

		// course déjà partie : on ne rejoint pas en plein milieu
		if (game.getStatus() != StatutBoatGame.waitplayer) {
			player.sendMessage("§cUne course est en cours, attends la prochaine partie !");
			return;
		}

		if (!boatgame.teleportplayertoboat(game, player, plugin)) {
			player.sendMessage("§cVeuillez attendre la prochaine partie !");
			return;
		}
		player.sendMessage(ConfigStartEndZone.messageArrivee(game.getTimetolaunch()));
	}
	
	

}
