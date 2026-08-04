package fr.kevyn.farmland.Farming;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HoueFarmeur {

    private static final NamespacedKey CLE_TAG = new NamespacedKey("farmland", "houe_farmeur");
    private static final NamespacedKey CLE_GRAINE_SELECTIONNEE = new NamespacedKey("farmland", "graine_selectionnee");

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aHoue du Farmeur");
        meta.getPersistentDataContainer().set(CLE_TAG, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isHoueFarmeur(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(CLE_TAG, PersistentDataType.BYTE);
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
