package fr.kevyn.farmland.directives.metiers.farmeur;

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
		// Un seul outil de metier de chaque type dans l'inventaire (avant : un nouveau a chaque commande)
		if (fr.kevyn.farmland.directives.gameplay.menus.Outils.possedeOutil(player, org.bukkit.Material.NETHERITE_HOE)) {
			player.sendMessage("§cTu as déjà ta Houe du Farmeur dans ton inventaire !");
			return true;
		}
		player.give(HoueFarmeur.create());

		player.sendMessage("§aTu as reçu ta Houe du Farmeur !");
		return true;
	}

}
