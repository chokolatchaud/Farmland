package fr.kevyn.farmland.mineur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MineCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
			return true;
		}

		Player player = (Player) sender;
		player.give(PiocheFarm.create());

		player.sendMessage("§aTu as reçu ta Pioche du Mineur !");
		player.sendMessage("§7Casse le minerai généré par ton Cobblegenerator pour le récupérer dans ton /bag.");
		return true;
	}

}
