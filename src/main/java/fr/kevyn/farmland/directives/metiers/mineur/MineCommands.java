package fr.kevyn.farmland.directives.metiers.mineur;

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
		// Un seul outil de metier de chaque type dans l'inventaire (avant : un nouveau a chaque commande)
		if (fr.kevyn.farmland.directives.gameplay.menus.Outils.possedeOutil(player, org.bukkit.Material.NETHERITE_PICKAXE)) {
			player.sendMessage("§cTu as déjà ta Pioche du Mineur dans ton inventaire !");
			return true;
		}
		player.give(PiocheFarm.create());

		player.sendMessage("§aTu as reçu ta Pioche du Mineur !");
		player.sendMessage("§7Casse le minerai généré par ton Cobblegenerator pour le récupérer dans ton /bag.");
		return true;
	}

}
