package fr.kevyn.farmland.directives.metiers.tueur;

import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;

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

		RecompenseUtil.donnerRecompenseTueur(tueur, ps, 10);

	}
}
	
	

	

