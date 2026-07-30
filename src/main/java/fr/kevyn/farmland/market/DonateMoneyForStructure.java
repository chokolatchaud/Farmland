package fr.kevyn.farmland.market;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.save.MarketSave;
import fr.kevyn.farmland.structure.CoefStructure;
import fr.kevyn.farmland.structure.GetStructure;

public class DonateMoneyForStructure {

	public static void AllPlayer(FarmlandMain plugin) {
		int totalStructures = GetStructure.getallStructure().size();
		plugin.getLogger().info("[Marche] Distribution des revenus → " + totalStructures + " structure(s)");

		for (GameRegion structure : GetStructure.getallStructure()) {
			Player player = Bukkit.getPlayer(structure.getPropriétaire());
			int moneystructureRaw = moneycalc(structure, plugin);

			PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(structure.getPropriétaire());
			if (ps == null) continue;

			// vrai garde-fou anti-triche (valeur totalement aberrante), plus haut
			// que le plafond legitime desormais (150-500)
			if (moneystructureRaw > 2000) {
				if (player != null) player.sendMessage(MessageColor.RED.apply("Erreur Sur vos Structure, Veuillez Voir avec Un Membre du Staff"));
				plugin.getLogger().warning("[Marche] Revenu anormal pour " + structure.getName() + " : " + moneystructureRaw);
				continue;
			}

			// chaque structure rapporte entre 150 et 500 $FB, quelle que soit sa note/le marche
			int moneystructure = Math.max(150, Math.min(500, moneystructureRaw));

			if (player != null && player.isOnline()) {
				// En ligne : revenu plein
				ps.setMoney(moneystructure + ps.getMoney());
				player.sendMessage(MessageColor.GREEN.apply("+" + moneystructure + " $FB pour " + structure.getName()));

				if (plugin.getWebApi() != null) {
					int nbStructures = 0;
					for (GameRegion r : GetStructure.getallStructure()) {
						if (r.getPropriétaire().equals(player.getUniqueId())) nbStructures++;
					}
					plugin.getWebApi().pushPlayerBalance(ps.getName(), ps.getMoney(), nbStructures, ps.getBlocposetotal());
				}
			} else {
				// Hors ligne : penalite de 80% (le joueur ne touche que 20%)
				// pour eviter que l'economie explose avec les revenus deconnectes
				int offlineGain = Math.round(moneystructure * 0.20f);
				ps.setMoney(ps.getMoney() + offlineGain);
				ps.setMoneyoffline(ps.getMoneyoffline() + offlineGain);
			}
		}
	}

	public static int moneycalc(GameRegion structure, FarmlandMain plugin) {
		Market market = MarketSave.loadMarket(plugin);
		return
			Math.round((CoefStructure.ScoreToCoefCréativité(structure.getScore())   / 100f) * market.getMoneyforcoefCréativité())   +
			Math.round((CoefStructure.ScoreToCoefArchitecture(structure.getScore()) / 100f) * market.getMoneyforcoefArchitecture()) +
			Math.round((CoefStructure.ScoreToCoefDensité(structure.getScore())      / 100f) * market.getMoneyforcoefDensité())      +
			Math.round((CoefStructure.ScoreToCoefFinition(structure.getScore())     / 100f) * market.getMoneyforcoefFinition())     +
			Math.round((CoefStructure.ScoreToCoefÉquilibre(structure.getScore())    / 100f) * market.getMoneyforcoefÉquilibre());
	}
}
