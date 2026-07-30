package fr.kevyn.farmland.cosmetics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Liste des cosmetiques achetables via /buy cosmetic.
 * Base : CARVED_PUMPKIN reskinne par custom model data (resource pack).
 *
 * ⚠️ EXEMPLE A REMPLACER : l'entree ci-dessous a un customModelData=1 et un
 * prix de demonstration. Duplique la ligne pour chacun de tes vrais chapeaux,
 * en mettant le VRAI customModelData de ton resource pack et le prix voulu.
 */
public class CosmeticShop {

    public static class Cosmetic {
        public final int id;
        public final String name;
        public final int price;
        public final int customModelData;

        public Cosmetic(int id, String name, int price, int customModelData) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.customModelData = customModelData;
        }

        public ItemStack createItem() {
            ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6" + name);
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
            return item;
        }
    }

    // ⚠️ REMPLACE CET EXEMPLE PAR TES VRAIS CHAPEAUX (id unique, nom, prix, customModelData)
    public static final List<Cosmetic> COSMETICS = new ArrayList<>(List.of(
            new Cosmetic(1, "Citrouille Dorée", 500, 1)
            // new Cosmetic(2, "Ton 2eme chapeau", 750, 2),
            // new Cosmetic(3, "Ton 3eme chapeau", 1000, 3),
    ));

    public static Cosmetic getById(int id) {
        for (Cosmetic c : COSMETICS) {
            if (c.id == id) return c;
        }
        return null;
    }
}
