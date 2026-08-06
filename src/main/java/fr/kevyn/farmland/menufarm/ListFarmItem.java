package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

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
	public static Material mobVersRessource(EntityType type) {
		return switch (type) {
			case ZOMBIE -> Material.ROTTEN_FLESH;
			case SKELETON -> Material.BONE;
			case CREEPER -> Material.GUNPOWDER;
			case SHULKER -> Material.SHULKER_SHELL;
			default -> null;
		};
	}
	
	public static ArrayList<Material> getlistSeeditem() {
		ArrayList<Material> farmingSeed = new ArrayList<Material>();
		farmingSeed.add(Material.WHEAT_SEEDS);
		farmingSeed.add(Material.CARROT);
		farmingSeed.add(Material.POTATO);
		farmingSeed.add(Material.PUMPKIN_SEEDS);
		return farmingSeed;

	}
	public static ArrayList<Material> getlistshop() {
		ArrayList<Material> listmaterialshop = new ArrayList<Material>();
		//Farming
		listmaterialshop.add(Material.WHEAT_SEEDS);
		listmaterialshop.add(Material.CARROT);
		listmaterialshop.add(Material.POTATO);
		listmaterialshop.add(Material.PUMPKIN_SEEDS);
		listmaterialshop.add(Material.PIG_SPAWN_EGG);
		listmaterialshop.add(Material.COW_SPAWN_EGG);
		listmaterialshop.add(Material.CHICKEN_SPAWN_EGG);
		listmaterialshop.add(Material.SHEEP_SPAWN_EGG);
		listmaterialshop.add(Material.ZOMBIE_SPAWN_EGG);
		listmaterialshop.add(Material.SKELETON_SPAWN_EGG);
		listmaterialshop.add(Material.CREEPER_SPAWN_EGG);
		listmaterialshop.add(Material.SHULKER_SPAWN_EGG);
		return listmaterialshop;
	}
	
	
	public static ArrayList<Material> listfichingitem() {
		ArrayList<Material> farmingFishing= new ArrayList<Material>();
		farmingFishing.add(Material.COD);
		farmingFishing.add(Material.SALMON);
		farmingFishing.add(Material.TROPICAL_FISH);
		farmingFishing.add(Material.PUFFERFISH);
		return farmingFishing;

	}
	
	public static ArrayList<Material> listAgriculteuritem() {
		ArrayList<Material> farmingAgriculteur= new ArrayList<Material>();
		farmingAgriculteur.add(Material.PIG_SPAWN_EGG);
		farmingAgriculteur.add(Material.COW_SPAWN_EGG);
		farmingAgriculteur.add(Material.CHICKEN_SPAWN_EGG);
		farmingAgriculteur.add(Material.SHEEP_SPAWN_EGG);
		return farmingAgriculteur;

	}
	
	public static ArrayList<Material> listTueuritem() {
		ArrayList<Material> farmingTueur= new ArrayList<Material>();
		farmingTueur.add(Material.ZOMBIE_SPAWN_EGG);
		farmingTueur.add(Material.SKELETON_SPAWN_EGG);
		farmingTueur.add(Material.CREEPER_SPAWN_EGG);
		farmingTueur.add(Material.SHULKER_SPAWN_EGG);
		return farmingTueur;

	}
	
	public static boolean isSeedMaterial(Material material) {
		return switch (material) {
			case WHEAT_SEEDS, CARROT, POTATO, PUMPKIN_SEEDS -> true;
			default -> false;
		};
	}

	public static Material itemVersBlocCulture(Material itemGraine) {
	    return switch (itemGraine) {
	        case WHEAT_SEEDS -> Material.WHEAT;
	        case CARROT -> Material.CARROTS;
	        case POTATO -> Material.POTATOES;
	        case PUMPKIN_SEEDS -> Material.PUMPKIN_STEM;
	        default -> null;
	    };
	}
	
	public static EntityType EggVersMobs(Material itemGraine) {
	    return switch (itemGraine) {
	        case PIG_SPAWN_EGG -> EntityType.PIG;
	        case COW_SPAWN_EGG -> EntityType.COW;
	        case SHEEP_SPAWN_EGG -> EntityType.SHEEP;
	        case CHICKEN_SPAWN_EGG -> EntityType.CHICKEN;
	        case ZOMBIE_SPAWN_EGG -> EntityType.ZOMBIE;
	        case CREEPER_SPAWN_EGG -> EntityType.CREEPER;
	        case SKELETON_SPAWN_EGG -> EntityType.SKELETON;
	        case SHULKER_SPAWN_EGG -> EntityType.SHULKER;
	        default -> null;
	    };
	}

	public static boolean isCulture(Material type) {
		return switch (type) {
			case WHEAT, CARROTS, POTATOES, PUMPKIN_STEM -> true;
			default -> false;
		};
	}

	public static Material blocCultureVersRessource(Material blocCulture) {
		return switch (blocCulture) {
			case WHEAT -> Material.WHEAT;
			case CARROTS -> Material.CARROT;
			case POTATOES -> Material.POTATO;
			case PUMPKIN_STEM-> Material.PUMPKIN;
			default -> null;
		};
	}
	
	
}
	

	