package fr.kevyn.farmland.pecheur;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menufarm.ListFarmItem;
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
			if (!Outils.isOutils(mainPrincipale)) {
				player.sendMessage("Veuillez Pechez grace a la canne a péche /peche");
				event.setCancelled(true);
				return;
				
			}
			ArrayList<Material> listfishreward = ListFarmItem.listfichingitem();
			int index = random.nextInt(listfishreward.size());
			Material fish = listfishreward.get(index);
			ps.addRessource(fish, 1);
			player.sendMessage("Vous avez pecher un " + fish.name() + "x 1");
			event.setCancelled(true);

        }
		
		

	}

}
