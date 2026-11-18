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
		// Un seul outil de metier de chaque type dans l'inventaire (avant : un nouveau a chaque commande)
		if (fr.kevyn.farmland.directives.gameplay.menus.Outils.possedeOutil(player, org.bukkit.Material.NETHERITE_SWORD)) {
			player.sendMessage("§cTu as déjà ton Épée du Tueur dans ton inventaire !");
			return true;
		}
		fr.kevyn.farmland.directives.gameplay.objet.ObjetsJoueur.donner(player, epeeFarm.create());

		player.sendMessage("§aTu as reçu ton Épée du Tueur !");
		return true;
	}

}
