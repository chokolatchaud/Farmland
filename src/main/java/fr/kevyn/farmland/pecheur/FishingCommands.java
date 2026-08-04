package fr.kevyn.farmland.pecheur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /peche - donne la Canne du Pêcheur (chaque poisson pêché avec va
 * directement dans ton /bag, plus de vrai drop dans l'eau).
 */
public class FishingCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
			return true;
		}

		Player player = (Player) sender;
		player.give(PecheurFarm.create());

		player.sendMessage("§aTu as reçu ta Canne à Pêche du Pêcheur !");
		player.sendMessage("§7Chaque poisson pêché avec ira directement dans ton /bag.");
		return true;
	}

}
