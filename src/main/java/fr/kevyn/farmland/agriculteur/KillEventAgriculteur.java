package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menufarm.AllMenuMetier;
import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.menufarm.SlotPriceFarmItem;
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

		Player tueur = mob.getKiller();
		if (tueur == null) return;

		ItemStack arme = tueur.getInventory().getItemInMainHand();
		if (!Outils.isOutils(arme)) return;

		Material ressource = SlotPriceFarmItem.mobVersRessource(mob.getType());
		if (ressource == null) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(tueur.getUniqueId());
		if (ps == null) return;

		event.getDrops().clear(); // jamais de vrai drop au sol, tout passe par le /bag
		event.setDroppedExp(0);

		ps.addRessource(ressource, 1);
		tueur.sendMessage("§a+1 " + ressource.name() + " ajouté à ton /bag !");
	}
	
	
	@EventHandler
	public void onClicHache(PlayerInteractEvent event) {
	    if (event.getHand() != EquipmentSlot.HAND) return; // eviter le double declenchement

	    Player player = event.getPlayer();
	    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
	    ItemStack mainPrincipale = player.getInventory().getItemInMainHand();

	    if (!Outils.isOutils(mainPrincipale)) return;
	    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            player.openInventory(AllMenuMetier.createmenuAnimal(ps));
        }
	    
	}
	
	


}
