package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.menufarm.Outils;


public class HacheFarm {
	private static final NamespacedKey CLE_MODIFIER = new NamespacedKey("farmland", "hache_degats");
	

	public static ItemStack createHache() {
	    ItemStack item = Outils.create(Material.NETHERITE_AXE, "§eHache de l'Agriculteur");
	    appliquerDegats(item, ArmesUtil.calculerDegats(0));
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
