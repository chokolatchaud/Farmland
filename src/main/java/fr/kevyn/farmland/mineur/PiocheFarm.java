package fr.kevyn.farmland.mineur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PiocheFarm {


	    private static final NamespacedKey CLE_TAG = new NamespacedKey("farmland", "pioche_farmeur");

	    public static ItemStack create() {
	        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
	        ItemMeta meta = item.getItemMeta();
	        meta.setDisplayName("§aPioche");
	        meta.getPersistentDataContainer().set(CLE_TAG, PersistentDataType.BYTE, (byte) 2);
	        item.setItemMeta(meta);
	        return item;
	    }
	    public static boolean isPiochemine(ItemStack item) {
	        if (item == null || !item.hasItemMeta()) return false;
	        return item.getItemMeta().getPersistentDataContainer().has(CLE_TAG, PersistentDataType.BYTE);
	    }
	    
	    
	    

	    
	}



