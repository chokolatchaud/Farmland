package fr.kevyn.farmland.market;

import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.save.MarketSave;
import fr.kevyn.farmland.structure.CoefStructure;
import fr.kevyn.farmland.structure.GetStructure;

public class MarketCalc {	
	
	public static void Calcforcoef(FarmlandMain plugin) {
		float countcoefCreativité = 0;
		float countcoefArchitecture = 0;
		float countcoefDensité = 0;
		float countcoefFinition = 0;
		float countcoefÉquilibre = 0;
		
		
		for(GameRegion structure : GetStructure.getallStructure()) {
			countcoefCreativité += CoefStructure.ScoreToCoefCréativité(structure.getScore());
			countcoefArchitecture += CoefStructure.ScoreToCoefArchitecture(structure.getScore());
			countcoefDensité += CoefStructure.ScoreToCoefDensité(structure.getScore());
			countcoefFinition += CoefStructure.ScoreToCoefFinition(structure.getScore());
			countcoefÉquilibre += CoefStructure.ScoreToCoefÉquilibre(structure.getScore());
		}
		
			//on evite de diviser par 0 si y a pas encore assez de structures (seuil : 10)
		if(GetStructure.getallStructure().size() < 10) {
			// pas de structures : fluctuation aleatoire ±5% pour animer les courbes du site
			Market lastKnown = MarketSave.loadMarket(plugin);
			if (lastKnown == null) {
				// premier demarrage : le marche doit s'initialiser meme sans structures
				lastKnown = new Market(50, 50, 50, 50, 50);
				MarketSave.saveMarket(plugin, lastKnown);
				plugin.getLogger().info("[Marche] Premier demarrage : marche initialise a 50 par coef");
			}
			if (lastKnown != null) {
				java.util.Random rand = new java.util.Random();
				int newCreativite   = Math.max(5, Math.min(90, Math.round(lastKnown.getMoneyforcoefCréativité()   * (0.92f + rand.nextFloat() * 0.16f))));
				int newArchitecture = Math.max(5, Math.min(90, Math.round(lastKnown.getMoneyforcoefArchitecture() * (0.92f + rand.nextFloat() * 0.16f))));
				int newDensite      = Math.max(5, Math.min(90, Math.round(lastKnown.getMoneyforcoefDensité()      * (0.92f + rand.nextFloat() * 0.16f))));
				int newEquilibre    = Math.max(5, Math.min(90, Math.round(lastKnown.getMoneyforcoefÉquilibre()    * (0.92f + rand.nextFloat() * 0.16f))));
				int newFinition     = Math.max(5, Math.min(90, Math.round(lastKnown.getMoneyforcoefFinition()     * (0.92f + rand.nextFloat() * 0.16f))));
				Market newMarket = new Market(newCreativite, newArchitecture, newDensite, newEquilibre, newFinition);
				MarketSave.saveMarket(plugin, newMarket);
				FarmlandMain main = (FarmlandMain) plugin;
				plugin.getLogger().info("[Marche] Fluctuation ±15% (" + GetStructure.getallStructure().size() + "/10 structures pour marche reel)");
				if (main.getWebApi() != null) {
					main.getWebApi().pushStructurePrice("Creativite",   newCreativite,   "Marche");
					main.getWebApi().pushStructurePrice("Architecture", newArchitecture, "Marche");
					main.getWebApi().pushStructurePrice("Densite",      newDensite,      "Marche");
					main.getWebApi().pushStructurePrice("Equilibre",    newEquilibre,    "Marche");
					main.getWebApi().pushStructurePrice("Finition",     newFinition,     "Marche");
					plugin.getLogger().info("[WebAPI] Marche fluctue (±15%) → Creativite:" + newCreativite + " | Architecture:" + newArchitecture + " | Densite:" + newDensite + " | Equilibre:" + newEquilibre + " | Finition:" + newFinition);
				}
			}
			return;
		}
		
		float moyenneCreativité = countcoefCreativité / GetStructure.getallStructure().size();
		float moyenneArchitecture = countcoefArchitecture / GetStructure.getallStructure().size();
		float moyenneDensité = countcoefDensité / GetStructure.getallStructure().size();
		float moyenneFinition = countcoefFinition / GetStructure.getallStructure().size();
		float moyenneÉquilibre = countcoefÉquilibre / GetStructure.getallStructure().size();
		
		
		
		
		
		float evolutionCreativité = Math.max(0.5f, Math.min(1.5f, 20f / moyenneCreativité));
		float evolutionArchitecture = Math.max(0.5f, Math.min(1.5f, 15f / moyenneArchitecture));
		float evolutionDensité = Math.max(0.5f, Math.min(1.5f, 8f / moyenneDensité));
		float evolutionFinition = Math.max(0.5f, Math.min(1.5f, 10f / moyenneFinition));
		float evolutionÉquilibre = Math.max(0.5f, Math.min(1.5f, 11f / moyenneÉquilibre));

		Market lastMarket = MarketSave.loadMarket(plugin);
        if (lastMarket == null) {
        	// premier demarrage : marche initialise a 50 (valeur neutre)
        	lastMarket = new Market(50, 50, 50, 50, 50);
        	MarketSave.saveMarket(plugin, lastMarket);
        	plugin.getLogger().info("[Marche] Premier demarrage : marche initialise a 50 par coef");
        }

        int NewMoneyCreativité = Math.round(lastMarket.getMoneyforcoefCréativité() * evolutionCreativité);
        int NewMoneyArchitecture = Math.round(lastMarket.getMoneyforcoefArchitecture() * evolutionArchitecture);
        int NewMoneyDensité = Math.round(lastMarket.getMoneyforcoefDensité() * evolutionDensité);
        int NewMoneyFinition = Math.round(lastMarket.getMoneyforcoefFinition() * evolutionFinition);
        int NewMoneyÉquilibre = Math.round(lastMarket.getMoneyforcoefÉquilibre() * evolutionÉquilibre);
        
        if(NewMoneyCreativité >= 100) {
        	NewMoneyCreativité = 100;
        }
        if(NewMoneyArchitecture >= 100) {
        	NewMoneyArchitecture = 100;
        }
        if(NewMoneyFinition >= 100) {
        	NewMoneyFinition = 100;
        }
    
        if(NewMoneyDensité >= 100) {
        	NewMoneyDensité = 100;
        }
        if(NewMoneyÉquilibre >= 100) {
        	NewMoneyÉquilibre = 100;
        }
        
        if(NewMoneyCreativité <= 5) {
        	NewMoneyCreativité = 5;
        }
        if(NewMoneyArchitecture <= 5) {
        	NewMoneyArchitecture = 5;
        }
        if(NewMoneyFinition <= 5) {
        	NewMoneyFinition = 5;
        }
        if(NewMoneyDensité <= 5) {
        	NewMoneyDensité = 5;
        }
        if(NewMoneyÉquilibre <= 5) {
        	NewMoneyÉquilibre = 5;
        }
        
        plugin.getLogger().info("Moyenne Créativité: " + moyenneCreativité);
        plugin.getLogger().info("Évolution Créativité: " + evolutionCreativité);
        plugin.getLogger().info("Ancien Money: " + lastMarket.getMoneyforcoefCréativité());
        plugin.getLogger().info("Nouveau Money: " + NewMoneyCreativité);
        
        plugin.getLogger().info("Moyenne Architecture: " + moyenneArchitecture);
        plugin.getLogger().info("Évolution Architecture: " + evolutionArchitecture);
        plugin.getLogger().info("Ancien Money: " + lastMarket.getMoneyforcoefArchitecture());
        plugin.getLogger().info("Nouveau Money: " + NewMoneyArchitecture);

        plugin.getLogger().info("Moyenne Densité: " + moyenneDensité);
        plugin.getLogger().info("Évolution Densité: " + evolutionDensité);
        plugin.getLogger().info("Ancien Money: " + lastMarket.getMoneyforcoefDensité());
        plugin.getLogger().info("Nouveau Money: " + NewMoneyDensité);
        
        plugin.getLogger().info("Moyenne Finition: " + moyenneFinition);
        plugin.getLogger().info("Évolution Finition: " + evolutionFinition);
        plugin.getLogger().info("Ancien Money: " + lastMarket.getMoneyforcoefFinition());
        plugin.getLogger().info("Nouveau Money: " + NewMoneyFinition);
        
        plugin.getLogger().info("Moyenne Équilibre: " + moyenneÉquilibre);
        plugin.getLogger().info("Évolution Équilibre: " + evolutionÉquilibre);
        plugin.getLogger().info("Ancien coef: " + lastMarket.getMoneyforcoefÉquilibre());
        plugin.getLogger().info("Nouveau coef: " + NewMoneyÉquilibre);
        
        
        Market NewMarket = new Market(NewMoneyCreativité, NewMoneyArchitecture, NewMoneyDensité, NewMoneyÉquilibre, NewMoneyFinition);
        MarketSave.saveMarket(plugin,NewMarket);

        // pousse les prix vers le site farm-land.fr
        // Fix Emergent : noms sans accents pour eviter les problemes d'encoding
        FarmlandMain main = (FarmlandMain) plugin;
        if (main.getWebApi() != null) {
            main.getWebApi().pushStructurePrice("Creativite",   NewMoneyCreativité,   "Marche");
            main.getWebApi().pushStructurePrice("Architecture", NewMoneyArchitecture, "Marche");
            main.getWebApi().pushStructurePrice("Densite",      NewMoneyDensité,      "Marche");
            main.getWebApi().pushStructurePrice("Equilibre",    NewMoneyÉquilibre,    "Marche");
            main.getWebApi().pushStructurePrice("Finition",     NewMoneyFinition,     "Marche");
            plugin.getLogger().info("[WebAPI] Marche pousse vers farm-land.fr → Creativite:" + NewMoneyCreativité
                + " | Architecture:" + NewMoneyArchitecture
                + " | Densite:" + NewMoneyDensité
                + " | Equilibre:" + NewMoneyÉquilibre
                + " | Finition:" + NewMoneyFinition);
        }
	}

	// pousse le dernier marche connu vers le site (appele au demarrage apres init WebAPI)
	public static void pushMarketToWebApi(JavaPlugin plugin) {
		FarmlandMain main = (FarmlandMain) plugin;
		if (main.getWebApi() == null) return;

		Market lastMarket = MarketSave.loadMarket(plugin);
		if (lastMarket == null) {
			plugin.getLogger().warning("[WebAPI] Aucun marche sauvegarde, impossible de pousser vers le site");
			return;
		}

		main.getWebApi().pushStructurePrice("Creativite",   lastMarket.getMoneyforcoefCréativité(),   "Marche");
		main.getWebApi().pushStructurePrice("Architecture", lastMarket.getMoneyforcoefArchitecture(), "Marche");
		main.getWebApi().pushStructurePrice("Densite",      lastMarket.getMoneyforcoefDensité(),      "Marche");
		main.getWebApi().pushStructurePrice("Equilibre",    lastMarket.getMoneyforcoefÉquilibre(),    "Marche");
		main.getWebApi().pushStructurePrice("Finition",     lastMarket.getMoneyforcoefFinition(),     "Marche");
		plugin.getLogger().info("[WebAPI] Marche initial pousse vers farm-land.fr au demarrage");
	}

}