package fr.kevyn.farmland.tueur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.menufarm.Outils;


public class epeeFarm {

	private static final NamespacedKey CLE_MODIFIER = new NamespacedKey("farmland", "epee_degats");
	private static final NamespacedKey CLE_OEUF_SELECTIONNEE = new NamespacedKey("farmland", "oeuf_selectionneetueur");

	public static ItemStack create() {
		ItemStack item = Outils.create(Material.NETHERITE_SWORD, "§cépée du Tueur");
		appliquerDegats(item, 10.0); 
		return item;
	}
	
	public static void setOeufSelectionnee(ItemStack hache, Material oeuf) {
        ItemMeta meta = hache.getItemMeta();
        meta.getPersistentDataContainer().set(CLE_OEUF_SELECTIONNEE, PersistentDataType.STRING, oeuf.name());
        hache.setItemMeta(meta);
    }

    public static Material getOeufSelectionnee(ItemStack épée) {
        if (épée == null || !épée.hasItemMeta()) return null;
        String nom = épée.getItemMeta().getPersistentDataContainer().get(CLE_OEUF_SELECTIONNEE, PersistentDataType.STRING);
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
