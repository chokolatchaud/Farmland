package fr.kevyn.farmland.Farming;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.menufarm.Outils;

public class HoueFarmeur {


    public static ItemStack create() {
    	ItemStack houe = Outils.create(Material.NETHERITE_HOE, "§aHoue du Farmeur");
    	return houe;
    }

}


