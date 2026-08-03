package fr.kevyn.farmland.menufarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.cosmetics.CosmeticShop;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class MenuBag {

	
	public static Inventory createmenu(PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Sac");
        GameMenu menu = new GameMenu(inv, TypeMenu.BAG);
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);
        for(Material material : ListFarmItem.getlistfarmitem()) {
    
        	ItemStack itemset = new ItemStack(material);
        	int numberofitem = playerserver.getRessource(material);
        	String name = (ListFarmItem.ReturnNameofMaterial(material) + " x " + numberofitem);  
        	int slot = ListFarmItem.ReturnSlotofMaterial(material);
        	GameMenu.set_oneitem_menu(itemset, name, slot, inv);
        }
        return inv;
        

        


    }

}
