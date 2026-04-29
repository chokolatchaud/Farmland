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
        
        
        Market NewMarket = new Market(NewMoneyCreativité, NewMoneyArchitecture, NewMoneyDensité, NewMoneyFinition, NewMoneyÉquilibre);
        MarketSave.saveMarket(plugin,NewMarket);
	}

}
