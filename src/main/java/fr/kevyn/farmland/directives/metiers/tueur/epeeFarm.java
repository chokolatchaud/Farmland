package fr.kevyn.farmland.directives.metiers.tueur;

import fr.kevyn.farmland.directives.metiers.agriculteur.ArmesUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;


public class epeeFarm {

	private static final NamespacedKey CLE_MODIFIER = new NamespacedKey("farmland", "epee_degats");


	public static ItemStack create() {
		ItemStack item = Outils.create(Material.NETHERITE_SWORD, "§cépée du Tueur");
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
