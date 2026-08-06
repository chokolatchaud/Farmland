package fr.kevyn.farmland.menufarm;

import fr.kevyn.farmland.menu.TypeMenu;
import org.bukkit.Material;

public class NameFarmItem {
	public static String ReturnNameofMaterial(Material material, TypeMenu menu) {
		if(menu == TypeMenu.BAG) {
            return switch (material) {
                case WHEAT -> "Blé";
                case CARROT -> "Carotte";
                case POTATO -> "Patate";
                case PUMPKIN -> "Citrouille";
                case COAL -> "Charbon";
                case IRON_INGOT -> "Fer";
                case GOLD_INGOT -> "Or";
                case DIAMOND -> "Diamant";
                case PORKCHOP -> "Cochon";
                case BEEF -> "Vache";
                case CHICKEN -> "Poulet";
                case MUTTON -> "Mouton";
                case COD -> "Morue";
                case SALMON -> "Saumon";
                case TROPICAL_FISH -> "Guppy";
                case PUFFERFISH -> "Poisson-Globe";
                case ROTTEN_FLESH -> "Chair périmée";
                case BONE -> "Os";
                case GUNPOWDER -> "Poudre à Canon";
                case SHULKER_SHELL -> "Coquille";
                default -> throw new IllegalArgumentException("Unexpected value: " + material);
            };
		
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
