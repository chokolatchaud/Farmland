package fr.kevyn.farmland.menufarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class MenuFarm {
	
	public static Inventory createmenuSeeds(PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 9, "§6Planteur");
        GameMenu menu = new GameMenu(inv, TypeMenu.SEEDS);
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);
        for(Material material : ListFarmItem.getlistSeeditem()) {
    
        	ItemStack itemset = new ItemStack(material);
        	int numberofitem = playerserver.getRessource(material);
        	String name = (ListFarmItem.ReturnNameofMaterial(material, TypeMenu.SEEDS) + " x " + numberofitem);  
        	int slot = ListFarmItem.ReturnSlotofMaterial(material,TypeMenu.SEEDS);
        	GameMenu.set_oneitem_menu(itemset, name, slot, inv);
        }
        return inv;
        

        


    }

}
