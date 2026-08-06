package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class CreateMenuMetier {
	
	public static Inventory createmenuMetier(String Name,int Slot,TypeMenu typemenu,ArrayList<Material> listmaterial,PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, Slot, Name);
        GameMenu menu = new GameMenu(inv, typemenu);
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);
        for(Material material : listmaterial) {
        	ItemStack itemset = new ItemStack(material);
        	String aftername = getAfterName(typemenu, playerserver, material);
        	String name = (NameFarmItem.ReturnNameofMaterial(material, typemenu) + aftername);  
        	int slot = SlotPriceFarmItem.ReturnSlotofMaterial(material,typemenu);
        	GameMenu.set_oneitem_menu(itemset, name, slot, inv);
        }
        return inv;
    }
	
	public static String getAfterName(TypeMenu typemenu,PlayerServer playerserver,Material material) {
		if(TypeMenu.SHOP == typemenu) {
			 String aftername = " " + SlotPriceFarmItem.priceformaterial(material) + " FB" ;
			 return aftername;
        	
			
		}else if(TypeMenu.BAG == typemenu) {
			int numberofitem = playerserver.getRessource(material);
			int prixNormal = fr.kevyn.farmland.market.MarketCalc.getPrixDeBase(material);
			int prixMarche = fr.kevyn.farmland.market.MarketCalc.getPrixActuel(material, fr.kevyn.farmland.market.MarketHolder.get());
			String aftername = " x " + numberofitem + " §7(§f" + prixNormal + "$ §7→ §e" + prixMarche + "$§7)";
			return aftername;

		}else if(TypeMenu.SEEDS == typemenu || TypeMenu.PASSIF_MOB == typemenu || TypeMenu.AGGRESSIVE_MOB == typemenu) {
			int numberofitem = playerserver.getRessource(material);
        	String aftername = " x " + numberofitem;  
        	return aftername;
        	
		}
		
		return "erreur";
		

		
	}
	
	

}
