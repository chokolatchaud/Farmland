package fr.kevyn.farmland.gamemanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GameManagercommands implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		
		Player player = (Player) sender;
		if(player == null) {
			return true;
		}
		
		if(args.length == 0) {
			player.sendMessage("veuiller mettre la sous commande souhaité");
			return true;
		}
		
		switch (args[0]) {
		case "join" -> {
			GameManager.getInstance().addplayerwait(player);
			return true;
			
			
		}
		case "leave" -> {
			GameManager.getInstance().removeplayerwait(player);
			return true;
			
		}
		 
		default -> 
			player.sendMessage("sous commande inconnu");
		}
		
		
		
		// TODO Auto-generated method stubreturn true;return true;return true;
		return false;
	}

}
