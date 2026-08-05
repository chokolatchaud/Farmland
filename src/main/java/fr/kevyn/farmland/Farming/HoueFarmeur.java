package fr.kevyn.farmland.Farming;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.menufarm.Outils;

public class HoueFarmeur {
private static final NamespacedKey CLE_GRAINE_SELECTIONNEE = new NamespacedKey("farmland", "graine_selectionnee");
    

    public static ItemStack create() {
    	ItemStack houe = Outils.create(Material.NETHERITE_HOE, "§aHoue du Farmeur");
    	return houe;
    }

    public static void setGraineSelectionnee(ItemStack houe, Material graine) {
        ItemMeta meta = houe.getItemMeta();
        meta.getPersistentDataContainer().set(CLE_GRAINE_SELECTIONNEE, PersistentDataType.STRING, graine.name());
        houe.setItemMeta(meta);
    }

    public static Material getGraineSelectionnee(ItemStack houe) {
        if (houe == null || !houe.hasItemMeta()) return null;
        String nom = houe.getItemMeta().getPersistentDataContainer().get(CLE_GRAINE_SELECTIONNEE, PersistentDataType.STRING);
        return nom != null ? Material.valueOf(nom) : null;
    }
}


