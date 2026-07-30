package fr.kevyn.farmland.cosmetics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Liste des cosmetiques achetables via /buy cosmetic.
 * Base : Material.PAPER + item_model (namespace "custom:...") genere par
 * ton resource pack, meme systeme que CustomItemType.java.
 *
 * Les 6 modeles ci-dessous existent DEJA dans ton resource pack (genere le
 * 30/07) : cap, couleurschapeau, glasses, lunettesnoires, fleurs, sprout.
 * ⚠️ Ajuste juste les NOMS AFFICHES et les PRIX selon tes envies.
 */
public class CosmeticShop {

    public static class Cosmetic {
        public final int id;
        public final String name;
        public final int price;
        public final String itemModel; // ex: "custom:cap"

        public Cosmetic(int id, String name, int price, String itemModel) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.itemModel = itemModel;
        }

        public ItemStack createItem() {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6" + name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            // meme technique que CustomItemType.create() : item_model (1.21.4+)
            // avec repli PersistentDataContainer si l'API n'est pas disponible
            try {
                meta.setItemModel(NamespacedKey.fromString(itemModel));
            } catch (Exception e) {
                NamespacedKey key = new NamespacedKey("farmland", "item_model");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemModel);
            }

            item.setItemMeta(meta);
            return item;
        }
    }

    // ⚠️ Modifie les noms/prix comme tu veux, les item_model correspondent
    // deja a tes vrais fichiers du resource pack genere
    public static final List<Cosmetic> COSMETICS = new ArrayList<>(List.of(
            new Cosmetic(1, "Casquette",        300,  "custom:cap"),
            new Cosmetic(2, "Chapeau de paille",    400,  "custom:couleurschapeau"),
            new Cosmetic(3, "Lunettes",          350,  "custom:glasses"),
            new Cosmetic(4, "Lunettes Noires",   500,  "custom:lunettesnoires"),
            new Cosmetic(5, "Couronne de Fleurs",500,  "custom:fleurs"),
            new Cosmetic(6, "Pousse",            250,  "custom:sprout"),
            new Cosmetic(9, "A venir",            0,  "custom:nul")
    ));

    public static Cosmetic getById(int id) {
        for (Cosmetic c : COSMETICS) {
            if (c.id == id) return c;
        }
        return null;
    }
}
