package fr.kevyn.farmland.market;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;
import fr.kevyn.farmland.save.MarketSave;
import fr.kevyn.farmland.structure.CoefStructure;
import fr.kevyn.farmland.structure.GetStructure;

public class DonateMoneyForStructure {
	
	public static void AllPlayer(FarmlandMain plugin) {
		plugin.getLogger().info("Total regions: " + GameRegionHashMap.getInstance().getRegionhashmap().size());
		for(GameRegion r : GameRegionHashMap.getInstance().getRegionhashmap()) {
		    plugin.getLogger().info("Region: " + r.getName() + " Type: " + r.gettype());
		}
		plugin.getLogger().info("Nombre de structures: " + GetStructure.getallStructure().size());
		for(GameRegion structure : GetStructure.getallStructure()) {
			Player player = Bukkit.getPlayer(structure.getPropriétaire());
			Market market = MarketSave.loadMarket(plugin);
			int moneystructure = 
				Math.round((CoefStructure.ScoreToCoefCréativité(structure.getScore()) / 100f) * market.getMoneyforcoefCréativité()) +
				Math.round((CoefStructure.ScoreToCoefArchitecture(structure.getScore()) / 100f)* market.getMoneyforcoefArchitecture()) +
				Math.round((CoefStructure.ScoreToCoefDensité(structure.getScore()) / 100f) * market.getMoneyforcoefDensité()) +
				Math.round((CoefStructure.ScoreToCoefFinition(structure.getScore()) / 100f)* market.getMoneyforcoefFinition()) +
				Math.round((CoefStructure.ScoreToCoefÉquilibre(structure.getScore()) / 100f)* market.getMoneyforcoefÉquilibre()) ;
			plugin.getLogger().info("Structure: " + structure.getName());
			plugin.getLogger().info("Score: " + structure.getScore());
			plugin.getLogger().info("Market Créativité: " + market.getMoneyforcoefCréativité());
			plugin.getLogger().info("Money structure: " + moneystructure);
			
				
			if(player == null) {
				continue;
			}
			
			
			if(moneystructure > 750) {
				player.sendMessage("Erreur Sur vos Structure, Veuillez Voir avec Un Membre du Staff");
				continue;
			}
			
			PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
			if(ps == null) {
				continue;
			}
			if(player.isOnline()) {
				ps.setMoney(moneystructure + ps.getMoney());
				player.sendMessage("§a+" + moneystructure + "$ pour " + structure.getName());
			}
			
			
			
			
		}
		
		
	}

}
