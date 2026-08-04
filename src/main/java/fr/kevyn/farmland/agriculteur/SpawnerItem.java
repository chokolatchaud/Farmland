package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


public class SpawnerItem {

	public static final NamespacedKey CLE_TYPE_MOB = new NamespacedKey("farmland", "spawner_type_mob");

	public static ItemStack create(EntityType typeMob, String nomAffiche) {
		ItemStack item = new ItemStack(Material.SPAWNER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Spawner - " + nomAffiche);
		meta.getPersistentDataContainer().set(CLE_TYPE_MOB, PersistentDataType.STRING, typeMob.name());
		item.setItemMeta(meta);
		return item;
	}

	public static EntityType getTypeMob(ItemStack item) {
		if (item == null || !item.hasItemMeta()) return null;
		String nom = item.getItemMeta().getPersistentDataContainer().get(CLE_TYPE_MOB, PersistentDataType.STRING);
		return nom != null ? EntityType.valueOf(nom) : null;
	}
}
