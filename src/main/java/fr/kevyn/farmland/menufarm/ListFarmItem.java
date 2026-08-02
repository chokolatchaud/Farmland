package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;

import org.bukkit.Material;

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

}
