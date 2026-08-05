package fr.kevyn.farmland.tueur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.agriculteur.HacheFarm;

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

		player.sendMessage("§aTu as reçu ta Hache du Tueur !");
		player.sendMessage("§7Tue les mobs hostiles de tes spawners pour récupérer leurs ressources dans ton /bag.");
		return true;
	}

}
