package fr.kevyn.farmland.tueur;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.agriculteur.SpawnerTickTask;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Tue un mob HOSTILE (issu d'un de nos spawners) avec la Hache taguee ->
 * ajoute la ressource correspondante au /bag. Meme principe que
 * KillEventAgriculteur, juste pour les mobs hostiles + la Hache.
 */
public class KillEventTueur implements Listener {

	@EventHandler
	public void onKill(EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		if (!(mob instanceof Monster)) return;

		if (!mob.getPersistentDataContainer().has(SpawnerTickTask.CLE_MOB_SPAWNER, PersistentDataType.BYTE)) return;

		Player tueur = mob.getKiller();
		if (tueur == null) return;

		ItemStack arme = tueur.getInventory().getItemInMainHand();
		if (!HacheFarm.isHacheTueur(arme)) return;

		Material ressource = mobVersRessource(mob.getType());
		if (ressource == null) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(tueur.getUniqueId());
		if (ps == null) return;

		event.getDrops().clear();
		event.setDroppedExp(0);

		ps.addRessource(ressource, 1);
		tueur.sendMessage("§a+1 " + ressource.name() + " ajouté à ton /bag !");
	}

	private Material mobVersRessource(EntityType type) {
		return switch (type) {
			case ZOMBIE -> Material.ROTTEN_FLESH;
			case SKELETON -> Material.BONE;
			case CREEPER -> Material.GUNPOWDER;
			default -> null;
		};
	}
}
