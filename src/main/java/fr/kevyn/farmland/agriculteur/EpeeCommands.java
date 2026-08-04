package fr.kevyn.farmland.agriculteur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /epee - donne l'Épée de l'Agriculteur (tue les mobs passifs issus de tes
 * spawners, les envoie directement dans ton /bag).
 */
public class EpeeCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
			return true;
		}

		Player player = (Player) sender;
		player.give(EpeeFarm.create());

		player.sendMessage("§aTu as reçu ton Épée de l'Agriculteur !");
		player.sendMessage("§7Tue les mobs passifs de tes spawners pour récupérer leurs ressources dans ton /bag.");
		return true;
	}

}
