package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Tue un mob PASSIF (issu d'un de nos spawners, jamais un vrai mob vanilla
 * sauvage) avec l'Epee taguee -> ajoute la ressource correspondante au /bag.
 */
public class KillEventAgriculteur implements Listener {

	@EventHandler
	public void onKill(@NonNull EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		if (!(mob instanceof Animals)) return;

		Player tueur = mob.getKiller();
		if (tueur == null) return;

		ItemStack arme = tueur.getInventory().getItemInMainHand();
		if (!Outils.isOutilsAttendu(arme, Material.NETHERITE_AXE)) return;
		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(tueur.getUniqueId());
		if (ps == null) return;
		

		

		

		event.getDrops().clear(); // jamais de vrai drop au sol, tout passe par le /bag
		event.setDroppedExp(0);

		fr.kevyn.farmland.menufarm.RecompenseUtil.donnerRecompenseAgriculteur(tueur, ps, 10, 1);

	}
	
	


}
