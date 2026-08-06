package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.menufarm.Outils;


public class HacheFarm {
	private static final NamespacedKey CLE_MODIFIER = new NamespacedKey("farmland", "hache_degats");
	private static final NamespacedKey CLE_OEUF_SELECTIONNEE = new NamespacedKey("farmland", "oeuf_selectionnee");

	public static ItemStack createHache() {
	    ItemStack item = Outils.create(Material.NETHERITE_AXE, "§eHache de l'Agriculteur");
	    appliquerDegats(item, ArmesUtil.calculerDegats(0));
	    return item;
	}
	
	public static void setOeufSelection(ItemStack hache, Material oeuf) {
        ItemMeta meta = hache.getItemMeta();
        meta.getPersistentDataContainer().set(CLE_OEUF_SELECTIONNEE, PersistentDataType.STRING, oeuf.name());
        hache.setItemMeta(meta);
    }

    public static Material getOeufSelection(ItemStack houe) {
        if (houe == null || !houe.hasItemMeta()) return null;
        String nom = houe.getItemMeta().getPersistentDataContainer().get(CLE_OEUF_SELECTIONNEE, PersistentDataType.STRING);
        return nom != null ? Material.valueOf(nom) : null;
    }


	public static void appliquerDegats(ItemStack item, double degats) {
		ItemMeta meta = item.getItemMeta();

		meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE);
		AttributeModifier modifier = new AttributeModifier(
			CLE_MODIFIER, degats, AttributeModifier.Operation.ADD_NUMBER
		);
		meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);

		item.setItemMeta(meta);
	}
}
