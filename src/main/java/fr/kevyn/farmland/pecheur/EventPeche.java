package fr.kevyn.farmland.pecheur;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class EventPeche implements Listener {
	Random random = new Random();
	
	@EventHandler
	public void onfish(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			Player player = event.getPlayer();
			PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
			ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
			if (!Outils.isOutilsAttendu(mainPrincipale, Material.FISHING_ROD)) {
				player.sendMessage("Veuillez Pechez grace a la canne a péche /peche");
				event.setCancelled(true);
				return;
				
			}
			int niveauCanne = Math.max(1, ps.getCanneLevel());
			
			event.setCancelled(true);

        }
		
		

	}

}
