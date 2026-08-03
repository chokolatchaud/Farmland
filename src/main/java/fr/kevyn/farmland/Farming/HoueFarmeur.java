package fr.kevyn.farmland.Farming;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HoueFarmeur {

    private static final NamespacedKey CLE_TAG = new NamespacedKey("farmland", "outil_farmeur");

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
}