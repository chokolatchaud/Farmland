package fr.kevyn.farmland.directives.metiers.tueur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /hache - donne la Hache du Tueur (tue les mobs hostiles issus de tes
 * spawners, les envoie directement dans ton /bag).
 */
public class épeeCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
			return true;
		}

		Player player = (Player) sender;
		player.give(epeeFarm.create());

		player.sendMessage("§aTu as reçu ton épée du Tueur !");
		return true;
	}

}
