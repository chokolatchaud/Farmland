package fr.kevyn.farmland.agriculteur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * /epee - donne l'Épée de l'Agriculteur (tue les mobs passifs issus de tes
 * spawners, les envoie directement dans ton /bag).
 */
public class HacheCommands implements CommandExecutor {

	@Override
	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
			return true;
		}

        player.give(HacheFarm.createHache());

		player.sendMessage("§aTu as reçu ta Hache D'Agriculteur !");
		player.sendMessage("§7Tue les mobs passifs de tes spawners pour récupérer leurs ressources dans ton /bag.");
		return true;
	}


}
