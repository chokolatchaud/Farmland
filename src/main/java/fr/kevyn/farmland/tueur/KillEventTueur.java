package fr.kevyn.farmland.tueur;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class KillEventTueur implements Listener {
	
	

	@EventHandler
	public void onKill(EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		if (!(mob instanceof Monster) && !(mob instanceof org.bukkit.entity.Enemy)) return;
		Player tueur = mob.getKiller();
		if (tueur == null) return;
		ItemStack arme = tueur.getInventory().getItemInMainHand();
		if (!Outils.isOutilsAttendu(arme, Material.NETHERITE_SWORD)) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(tueur.getUniqueId());
		if (ps == null) return;
		event.getDrops().clear();
		event.setDroppedExp(0);

	}
}
	
	

	

