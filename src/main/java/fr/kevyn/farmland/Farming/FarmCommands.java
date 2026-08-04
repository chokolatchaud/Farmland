package fr.kevyn.farmland.Farming;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /houe - donne la Houe du Farmeur (clic droit dans le vide pour choisir sa
 * graine, clic droit sur terre labourée pour planter).
 */
public class FarmCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
			return true;
		}

		Player player = (Player) sender;
		player.give(HoueFarmeur.create());

		player.sendMessage("§aTu as reçu ta Houe du Farmeur !");
		player.sendMessage("§7Clic droit dans le vide pour choisir une graine, clic droit sur de la terre labourée pour planter.");
		return true;
	}

}
