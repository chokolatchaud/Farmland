package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Epee de l'Agriculteur (mobs passifs). Degats alignes sur ArmesUtil,
 * niveau 0 par defaut au moment de la creation (le niveau reel du joueur
 * est applique separement via mettreAJourDegats()).
 *
 * ⚠️ La ligne AttributeModifier utilise l'API moderne des attributs Paper.
 * Non verifiee par compilation ici - si Eclipse signale une erreur sur
 * Attribute.GENERIC_ATTACK_DAMAGE, tape "Attribute." et laisse
 * l'autocompletion te montrer le nom exact dans ta version (pourrait etre
 * juste "Attribute.ATTACK_DAMAGE" sans le prefixe GENERIC_ selon la version).
 */
public class EpeeFarm {

	private static final NamespacedKey CLE_TAG = new NamespacedKey("farmland", "epee_agriculteur");
	private static final NamespacedKey CLE_MODIFIER = new NamespacedKey("farmland", "epee_degats");

	public static ItemStack create() {
		ItemStack item = new ItemStack(Material.WOODEN_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§eÉpée de l'Agriculteur");
		meta.getPersistentDataContainer().set(CLE_TAG, PersistentDataType.BYTE, (byte) 1);
		item.setItemMeta(meta);

		appliquerDegats(item, 1.0); // niveau 0 = 1 degat, comme demande
		return item;
	}

	public static boolean isEpeeAgriculteur(ItemStack item) {
		if (item == null || !item.hasItemMeta()) return false;
		return item.getItemMeta().getPersistentDataContainer().has(CLE_TAG, PersistentDataType.BYTE);
	}

	/** Remplace le degat vanilla du bois par notre propre valeur, quel que soit le niveau */
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
