package fr.kevyn.farmland.agriculteur;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


public class SpawnerTickTask {

	public static final NamespacedKey CLE_MOB_SPAWNER = new NamespacedKey("farmland", "mob_de_spawner");
	private static final int MAX_MOBS_PAR_SPAWNER = 5; // ajustable, voire lie a un futur niveau de spawner
	private static final double RAYON_VERIFICATION = 5.0;

	public static void demarrer(JavaPlugin plugin) {
		org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (Map.Entry<Location, EntityType> entry : SpawnerPlaceListener.getSpawnersConnus().entrySet()) {
				Location position = entry.getKey();
				EntityType typeMob = entry.getValue();

				if (position.getWorld() == null) continue;

				long compteActuel = position.getWorld()
					.getNearbyEntities(position, RAYON_VERIFICATION, RAYON_VERIFICATION, RAYON_VERIFICATION)
					.stream()
					.filter(e -> e instanceof LivingEntity)
					.filter(e -> e.getPersistentDataContainer().has(CLE_MOB_SPAWNER, PersistentDataType.BYTE))
					.count();

				if (compteActuel >= MAX_MOBS_PAR_SPAWNER) continue;

				LivingEntity mob = (LivingEntity) position.getWorld().spawnEntity(position.clone().add(0, 1, 0), typeMob);
				mob.setAI(false);
				mob.getPersistentDataContainer().set(CLE_MOB_SPAWNER, PersistentDataType.BYTE, (byte) 1);
			}
		}, 100L, 200L); // premiere execution apres 5s, puis toutes les 10s
	}
}
