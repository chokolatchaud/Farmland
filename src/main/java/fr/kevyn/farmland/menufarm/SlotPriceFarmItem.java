package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.kevyn.farmland.menu.TypeMenu;

public class SlotPriceFarmItem {
	
	public static Material mobVersRessource(EntityType type) {
		return switch (type) {
			case PIG -> Material.PORKCHOP;
			case COW -> Material.BEEF;
			case CHICKEN -> Material.CHICKEN;
			case SHEEP -> Material.MUTTON;
			default -> null;
		};
	}
	
	public static int priceformaterial(Material material) {
		
		switch (material) {
		
		case WHEAT_SEEDS: return 1;
		case CARROT: return 3;
		case POTATO: return 5;
		case PUMPKIN_SEEDS: return 10;
		case PIG_SPAWN_EGG: return 50;
		case COW_SPAWN_EGG: return 30;
        case CHICKEN_SPAWN_EGG: return 10;
        case SHEEP_SPAWN_EGG: return 20;
        case ZOMBIE_SPAWN_EGG: return 30;
		case SKELETON_SPAWN_EGG: return 20;
        case CREEPER_SPAWN_EGG: return 10;
        case SHULKER_SPAWN_EGG: return 100;
		

		default:
			throw new IllegalArgumentException("Unexpected value: " + material);
		
		}
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
		if(menu == TypeMenu.SHOP) {
			switch (material) {
			case WHEAT_SEEDS: return 0;
			case CARROT: return 1;
			case POTATO: return 2;
			case PUMPKIN_SEEDS: return 7;
			case PIG_SPAWN_EGG: return 3;
			case COW_SPAWN_EGG: return 4;
			case CHICKEN_SPAWN_EGG: return 5;
			case SHEEP_SPAWN_EGG: return 6;
			case ZOMBIE_SPAWN_EGG: return 8;
			case SKELETON_SPAWN_EGG: return 9;
			case CREEPER_SPAWN_EGG: return 10;
			case SHULKER_SPAWN_EGG: return 11;
			
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
			
			
}
		if(menu == TypeMenu.PASSIF_MOB) {
			switch (material) {
			case PIG_SPAWN_EGG: return 1;
			case COW_SPAWN_EGG: return 2;
			case CHICKEN_SPAWN_EGG: return 3;
			case SHEEP_SPAWN_EGG: return 4;
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
	
}
		if(menu == TypeMenu.AGGRESSIVE_MOB) {
			switch (material) {
			case ZOMBIE_SPAWN_EGG: return 1;
			case SKELETON_SPAWN_EGG: return 2;
			case CREEPER_SPAWN_EGG: return 3;
			case SHULKER_SPAWN_EGG: return 4;
			default:
				throw new IllegalArgumentException("Unexpected value: " + material);
			
		}
	
}
		return null;
	}

}
