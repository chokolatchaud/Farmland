package fr.kevyn.farmland.pecheur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.mineur.PiocheFarm;

public class FishingCommands implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		Player player = (Player) sender;
		player.give(PecheurFarm.create());
		
	
	
	
	
	// TODO Auto-generated method stub
	return true;

	}

}
