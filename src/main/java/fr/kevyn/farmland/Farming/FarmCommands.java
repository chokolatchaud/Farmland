package fr.kevyn.farmland.Farming;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FarmCommands implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		Player player = (Player) sender;
		
		player.give(HoueFarmeur.create());
		
		// TODO Auto-generated method stub
		return false;
	}

}
