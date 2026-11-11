package fr.kevyn.farmland.doonees.menus;

import java.util.Arrays;
import java.util.List;


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

	// Numéro de la page affichée, pour les menus à plusieurs pages (ex. visite des plots).
	int page = 1;

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
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

	/** Comme set_oneitem_menu, avec une description (lore) sous le nom */
	public static void set_oneitem_menu(ItemStack item, String name, List<String> lore, int slot, Inventory inv) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(slot, item);
	}


}
