package fr.kevyn.farmland.menufarm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class BagCommands implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		Player player = (Player) sender; 
		if(player == null) {
			System.out.println("vous devait etre un joueur");
		}
		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		
		player.openInventory(MenuBag.createmenu(ps));
		return true;
	}

}
