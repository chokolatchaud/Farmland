package fr.kevyn.farmland.menu;

import java.util.Arrays;


import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



public class GameMenu {
	Inventory inventory;
	TypeMenu typemenu;
	
	public GameMenu(Inventory inventory, TypeMenu typemenu) {
		this.inventory = inventory;
		this.typemenu = typemenu;
		GameMenuHashMap.getInstance().AddMenulist(this);
		// TODO Auto-generated constructor stub
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	public TypeMenu getTypemenu() {
		return typemenu;
	}
	
	
	public static void fillmenu(Material material, Inventory inv) {
		ItemStack glass = new ItemStack(material);
        ItemStack[] fill = new ItemStack[inv.getSize()];
        Arrays.fill(fill, glass);
        inv.setContents(fill);
	}
	
	public static void set_oneitem_menu(ItemStack item, String name,int slot,Inventory inv) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        inv.setItem(slot, item);
	}
	

}
