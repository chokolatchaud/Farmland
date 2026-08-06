package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;

import fr.kevyn.farmland.menu.TypeMenu;

public class NameFarmItem {
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
		if(menu == TypeMenu.SHOP) {
			switch (material) {
			case WHEAT_SEEDS: return "Graine Blé";
			case CARROT: return "Graine Carotte";
			case POTATO: return "Graine Patate";
			case PUMPKIN_SEEDS: return "Graine Citrouille";
			case PIG_SPAWN_EGG: return "Cochon";
			case COW_SPAWN_EGG: return "Vache";
	        case CHICKEN_SPAWN_EGG: return "Poulet";
	        case SHEEP_SPAWN_EGG: return "Mouton";
	        case ZOMBIE_SPAWN_EGG: return "Zombie";
	        case SKELETON_SPAWN_EGG: return "Squelette";
	        case CREEPER_SPAWN_EGG: return "Creeper";
	        case SHULKER_SPAWN_EGG: return "Shulker";
			


			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}

	}
		
		if(menu == TypeMenu.PASSIF_MOB) {
			switch (material) {
			case PIG_SPAWN_EGG: return "Cochon";
			case COW_SPAWN_EGG: return "Vache";
			case CHICKEN_SPAWN_EGG: return "Poulet";
			case SHEEP_SPAWN_EGG: return "Mouton";
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
			
			
		}
		if(menu == TypeMenu.AGGRESSIVE_MOB) {
			switch (material) {
			case ZOMBIE_SPAWN_EGG: return "Zombie";
			case SKELETON_SPAWN_EGG: return "Squelette";
			case CREEPER_SPAWN_EGG: return "Creeper";
			case SHULKER_SPAWN_EGG: return "Shulker";
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
		}
		return null;


		
	} 



}
