package fr.kevyn.farmland.agriculteur;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Quand un joueur pose un spawner achete (tague via SpawnerItem), on
 * desactive completement la logique de spawn VANILLA (maxNearbyEntities=0,
 * jamais declenchee), on transfere le tag de type de mob sur le BLOC lui-meme,
 * et on enregistre la position pour que SpawnerTickTask sache ou faire
 * apparaitre les mobs inertes.
 *
 * ⚠️ Cette liste en memoire ne survit PAS a un redemarrage serveur - un
 * spawner deja pose lors d'une session precedente ne sera plus connu de
 * SpawnerTickTask apres un restart, tant qu'un vrai scan au demarrage n'est
 * pas ajoute (meme piege que PlotHashmap plus tot ce soir, a garder en tete).
 */
public class SpawnerPlaceListener implements Listener {

	private static final Map<Location, EntityType> spawnersConnus = new HashMap<>();

	@EventHandler
	public void onPlaceSpawner(BlockPlaceEvent event) {
		ItemStack itemUtilise = event.getItemInHand();
		EntityType typeMob = SpawnerItem.getTypeMob(itemUtilise);
		if (typeMob == null) return; // pas un de nos spawners tagues

		Block block = event.getBlock();
		if (!(block.getState() instanceof CreatureSpawner spawner)) return;

		spawner.getPersistentDataContainer().set(SpawnerItem.CLE_TYPE_MOB, PersistentDataType.STRING, typeMob.name());
		spawner.setMaxNearbyEntities(0); // neutralise totalement le spawn vanilla automatique
		spawner.update();

		spawnersConnus.put(block.getLocation(), typeMob);

		event.getPlayer().sendMessage("§aSpawner posé ! Les mobs apparaîtront progressivement.");
	}

	public static Map<Location, EntityType> getSpawnersConnus() {
		return spawnersConnus;
	}
}
