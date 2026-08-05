package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


public class HacheFarm {

	private static final NamespacedKey CLE_TAG = new NamespacedKey("farmland", "hache_agriculteur");
	private static final NamespacedKey CLE_MODIFIER = new NamespacedKey("farmland", "hache_degats");

	public static ItemStack create() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§eHache de l'Agriculteur");
		meta.getPersistentDataContainer().set(CLE_TAG, PersistentDataType.BYTE, (byte) 1);
		item.setItemMeta(meta);

		appliquerDegats(item, 2.0); 
		return item;
	}

	public static boolean isHacheAgriculteur(ItemStack item) {
		if (item == null || !item.hasItemMeta()) return false;
		return item.getItemMeta().getPersistentDataContainer().has(CLE_TAG, PersistentDataType.BYTE);
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
