package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class ShopMenu {
	
	public static Inventory createmenu(PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Boutique");
        GameMenu menu = new GameMenu(inv, TypeMenu.SHOP);
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);
        for(Material material : getlistshop()) {
    
        	ItemStack itemset = new ItemStack(material);
        	String name = (ListFarmItem.ReturnNameofMaterial(material, TypeMenu.SHOP) + " " + priceformaterial(material) + " FB" );  
        	int slot = ListFarmItem.ReturnSlotofMaterial(material,TypeMenu.SHOP);
        	GameMenu.set_oneitem_menu(itemset, name, slot, inv);
        }
        return inv;
        

        


    }


	
	
	
	
	
	public static ArrayList<Material> getlistshop() {
		ArrayList<Material> listmaterialshop = new ArrayList<Material>();
		//Farming
		listmaterialshop.add(Material.WHEAT_SEEDS);
		listmaterialshop.add(Material.CARROT);
		listmaterialshop.add(Material.POTATO);
		listmaterialshop.add(Material.PUMPKIN_SEEDS);
		return listmaterialshop;
	}
	
	public static int priceformaterial(Material material) {
		
		switch (material) {
		
		case WHEAT_SEEDS: return 1;
		case CARROT: return 3;
		case POTATO: return 5;
		case PUMPKIN_SEEDS: return 10;
		

		default:
			throw new IllegalArgumentException("Unexpected value: " + material);
		
	}
		
	}
	
	
	
	
	

}
