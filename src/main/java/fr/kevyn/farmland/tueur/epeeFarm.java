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

	public static ItemStack create() {
		ItemStack item = Outils.create(Material.NETHERITE_SWORD, "§cépée du Tueur");
		appliquerDegats(item, 5.0); // niveau 0 = 1 degat, comme l'epee (armes liees)
		return item;
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
