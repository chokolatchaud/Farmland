package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;

import org.bukkit.Material;

import fr.kevyn.farmland.menu.TypeMenu;

public class ListFarmItem {

	public static ArrayList<Material> getlistfarmitem() {
		ArrayList<Material> farmingitem = new ArrayList<Material>();
		farmingitem.add(Material.WHEAT);
		farmingitem.add(Material.CARROT);
		farmingitem.add(Material.POTATO);
		farmingitem.add(Material.PUMPKIN);

		farmingitem.add(Material.COAL);
		farmingitem.add(Material.IRON_INGOT);
		farmingitem.add(Material.GOLD_INGOT);
		farmingitem.add(Material.DIAMOND);

		farmingitem.add(Material.PORKCHOP);
		farmingitem.add(Material.BEEF);
		farmingitem.add(Material.CHICKEN);
		farmingitem.add(Material.MUTTON);

		farmingitem.add(Material.COD);
		farmingitem.add(Material.SALMON);
		farmingitem.add(Material.TROPICAL_FISH);
		farmingitem.add(Material.PUFFERFISH);

		farmingitem.add(Material.ROTTEN_FLESH);
		farmingitem.add(Material.BONE);
		farmingitem.add(Material.GUNPOWDER);
		farmingitem.add(Material.SHULKER_SHELL);
		return farmingitem;
	}
	
	public static ArrayList<Material> getlistSeeditem() {
		ArrayList<Material> farmingSeed = new ArrayList<Material>();
		farmingSeed.add(Material.WHEAT_SEEDS);
		farmingSeed.add(Material.CARROT);
		farmingSeed.add(Material.POTATO);
		farmingSeed.add(Material.PUMPKIN_SEEDS);
		return farmingSeed;
		
		
		
		
	}
	
	

	public static String ReturnNameofMaterial(Material material,TypeMenu menu) {
		if(menu == TypeMenu.BAG) {
			switch (material) {
			
			case WHEAT: return "Blé";
			case CARROT: return "Carotte";
			case POTATO: return "Patate";
			case PUMPKIN: return "Citrouille";
			case COAL: return "Charbon";
			case IRON_INGOT: return "Fer";
			case GOLD_INGOT: return "Or";
			case DIAMOND: return "Diamant";
			case PORKCHOP: return "Cochon";
			case BEEF: return "Vache";
			case CHICKEN: return "Poulet";
			case MUTTON: return "Mouton";
			case COD: return "Morue";
			case SALMON: return "Saumon";
			case TROPICAL_FISH: return "Guppy";
			case PUFFERFISH: return "Poisson-Globe";
			case ROTTEN_FLESH: return "Chair périmée";
			case BONE: return "Os";
			case GUNPOWDER: return "Poudre à Canon";
			case SHULKER_SHELL: return "Coquille";

			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
		
		}
		if(menu == TypeMenu.SEEDS) {
			switch (material) {
			
			case WHEAT_SEEDS: return "Graine Blé";
			case CARROT: return "Graine Carotte";
			case POTATO: return "Graine Patate";
			case PUMPKIN_SEEDS: return "Graine Citrouille";
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);


			
		}
	}
		return null; 
	} 
		

	public static Integer ReturnSlotofMaterial(Material material, TypeMenu menu) {
		if(menu == TypeMenu.BAG) {
			switch (material) {
			case WHEAT: return 9;
			case CARROT: return 17;
			case POTATO: return 25;
			case PUMPKIN: return 33;
			case COAL: return 11;
			case IRON_INGOT: return 19;
			case GOLD_INGOT: return 27;
			case DIAMOND: return 35;
			case PORKCHOP: return 13;
			case BEEF: return 21;
			case CHICKEN: return 29;
			case MUTTON: return 37;
			case COD: return 15;
			case SALMON: return 23;
			case TROPICAL_FISH: return 31;
			case PUFFERFISH: return 39;
			case ROTTEN_FLESH: return 43;
			case BONE: return 44;
			case GUNPOWDER: return 45;
			case SHULKER_SHELL: return 41;
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
			

	}
		if(menu == TypeMenu.SEEDS) {
			switch (material) {
			case WHEAT_SEEDS: return 1;
			case CARROT: return 2;
			case POTATO: return 3;
			case PUMPKIN_SEEDS: return 4;
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
}
		return null;
	
}
	
}
	