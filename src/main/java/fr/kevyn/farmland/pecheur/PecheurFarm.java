package fr.kevyn.farmland.pecheur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.menufarm.Outils;

public class PecheurFarm {
	
	private static final NamespacedKey CLE_TAG = new NamespacedKey("farmland", "pecheur_farmeur");

    public static ItemStack create() {
    	ItemStack item = Outils.create(Material.FISHING_ROD, "§aCanne à Péche");
        return item;
    }

}
