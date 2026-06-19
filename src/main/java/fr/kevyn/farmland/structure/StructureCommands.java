package fr.kevyn.farmland.structure;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.market.DonateMoneyForStructure;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;

public class StructureCommands implements CommandExecutor {
	FarmlandMain plugin = null;
	
	public StructureCommands(FarmlandMain plugin) {
		this.plugin = plugin;
		
	}
	

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		
		Player player = (Player) sender;
		PlayerServer playerserversender = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		
		if(command.getName().equalsIgnoreCase("liststructure")) {
			ArrayList<GameRegion> listplayerstructure = new ArrayList<GameRegion>();
			for(GameRegion structure : GetStructure.getallStructure()) {
				if(structure.getPropriétaire().equals(player.getUniqueId())){
					listplayerstructure.add(structure);
				}
				
			}
			if(listplayerstructure.isEmpty()){
				player.sendMessage(MessageColor.RED.apply("Vous N'avez pas de Structure"));
				return true;
				
			}
			else {
				player.sendMessage("Structure: " + "   " + listplayerstructure.size() +  " / 5");
			}
			for(GameRegion structure : listplayerstructure) {
				
				player.sendMessage("Nom: " + structure.getName() + "Score: " + structure.getScore());
				
			}
			player.sendMessage("--------------------");
			return true;
			
		}else if(command.getName().equalsIgnoreCase("viewmoney")) {
			GameRegion Structure = GameRegionHashMap.getInstance().Playerwhatistregion(player);
			if(Structure == null) {
				player.sendMessage(MessageColor.RED.apply("Veuillez vous Tenir Dans Votre Structure"));
				return true;
			}
			if(Structure.getPropriétaire().equals(player.getUniqueId())) {
				int money = DonateMoneyForStructure.moneycalc(Structure, plugin);
				player.sendMessage(MessageColor.AQUA.apply("Votre Structure génere:"));
				player.sendMessage(money + "");
				
				
			}else {
				player.sendMessage("vous n'etes pas le proprietaire de cette Structure");
				
			}
			return true;
			
		}
			
		
		return false;
	}

}
