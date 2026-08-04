package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Tue un mob PASSIF (issu d'un de nos spawners, jamais un vrai mob vanilla
 * sauvage) avec l'Epee taguee -> ajoute la ressource correspondante au /bag.
 */
public class KillEventAgriculteur implements Listener {

	@EventHandler
	public void onKill(EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		if (!(mob instanceof Animals)) return;

		// verifie que ce mob vient bien d'un de NOS spawners (pas un mob sauvage)
		if (!mob.getPersistentDataContainer().has(SpawnerTickTask.CLE_MOB_SPAWNER, PersistentDataType.BYTE)) return;

		Player tueur = mob.getKiller();
		if (tueur == null) return;

		ItemStack arme = tueur.getInventory().getItemInMainHand();
		if (!EpeeFarm.isEpeeAgriculteur(arme)) return;

		Material ressource = mobVersRessource(mob.getType());
		if (ressource == null) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(tueur.getUniqueId());
		if (ps == null) return;

		event.getDrops().clear(); // jamais de vrai drop au sol, tout passe par le /bag
		event.setDroppedExp(0);

		ps.addRessource(ressource, 1);
		tueur.sendMessage("§a+1 " + ressource.name() + " ajouté à ton /bag !");
	}

	private Material mobVersRessource(EntityType type) {
		return switch (type) {
			case PIG -> Material.PORKCHOP;
			case COW -> Material.BEEF;
			case CHICKEN -> Material.CHICKEN;
			case SHEEP -> Material.MUTTON;
			default -> null;
		};
	}
}
