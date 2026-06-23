package fr.kevyn.farmland.market;



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
		
			//on evite de diviser par 0 si y a pas encore de structure
		if(GetStructure.getallStructure().size() == 0) {
			// on pousse quand meme le dernier marche connu vers le site
			Market lastKnown = MarketSave.loadMarket(plugin);
			if (lastKnown != null) {
				FarmlandMain main = (FarmlandMain) plugin;
				if (main.getWebApi() != null) {
					// Fix Emergent : noms sans accents pour eviter les problemes d'encoding
					main.getWebApi().pushStructurePrice("Creativite",   lastKnown.getMoneyforcoefCréativité(),   "Marche");
					main.getWebApi().pushStructurePrice("Architecture", lastKnown.getMoneyforcoefArchitecture(), "Marche");
					main.getWebApi().pushStructurePrice("Densite",      lastKnown.getMoneyforcoefDensité(),      "Marche");
					main.getWebApi().pushStructurePrice("Equilibre",    lastKnown.getMoneyforcoefÉquilibre(),    "Marche");
					main.getWebApi().pushStructurePrice("Finition",     lastKnown.getMoneyforcoefFinition(),     "Marche");
					plugin.getLogger().info("[WebAPI] Marche pousse vers farm-land.fr (aucune structure, dernier marche connu)");
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
            return;
        }
        

        int NewMoneyCreativité = Math.round(lastMarket.getMoneyforcoefCréativité() * evolutionCreativité);
        int NewMoneyArchitecture = Math.round(lastMarket.getMoneyforcoefArchitecture() * evolutionArchitecture);
        int NewMoneyDensité = Math.round(lastMarket.getMoneyforcoefDensité() * evolutionDensité);
        int NewMoneyFinition = Math.round(lastMarket.getMoneyforcoefFinition() * evolutionFinition);
        int NewMoneyÉquilibre = Math.round(lastMarket.getMoneyforcoefÉquilibre() * evolutionÉquilibre);
        
        if(NewMoneyCreativité >= 150) {
        	NewMoneyCreativité = 149;
        }
        if(NewMoneyArchitecture >= 150) {
        	NewMoneyArchitecture = 149;
        }
        if(NewMoneyFinition >= 150) {
        	NewMoneyFinition = 149;
        }
    
        if(NewMoneyDensité >= 150) {
        	NewMoneyDensité = 149;
        }
        if(NewMoneyÉquilibre >= 150) {
        	NewMoneyÉquilibre = 149;
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

}
