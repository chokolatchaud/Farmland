package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Outils {


		    public static ItemStack create(Material material,String name) {
		        ItemStack item = new ItemStack(material);
		        ItemMeta meta = item.getItemMeta();
		        meta.setDisplayName(name);
		        meta.getPersistentDataContainer().set(CleOutils(item), PersistentDataType.BYTE, (byte) 1);
		        item.setItemMeta(meta);
		        return item;
		        
		    }

		    public static boolean isOutils(ItemStack item) {
		        if (item == null || !item.hasItemMeta()) return false;
		        return item.getItemMeta().getPersistentDataContainer().has(CleOutils(item), PersistentDataType.BYTE);
		    }
		    
		    
		    public static NamespacedKey CleOutils(ItemStack item) {
		        Material material = item.getType();
		        
		        switch (material) {
				case NETHERITE_HOE: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "houe_farmeur");
					return CLE_TAG;
				}
				case NETHERITE_SWORD: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "epee_tueur");
					return CLE_TAG;
					
				}
				case NETHERITE_PICKAXE: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "pioche_mineur");
					return CLE_TAG;
					
				}
				case NETHERITE_AXE: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "hache_agriculteur");
					return CLE_TAG;
				}
				case FISHING_ROD: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "Canneapeche_pecheur");
					return CLE_TAG;
				}

				default:
					throw new IllegalArgumentException("Unexpected value: " + material);
				}
		    }

		    
		    
		    


	
}
