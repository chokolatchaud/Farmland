package fr.kevyn.farmland.directives.metiers.pecheur;

import fr.kevyn.farmland.directives.gameplay.recompense.MultiplicateurUtil;
import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

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
			int jeton = MultiplicateurUtil.tirerMultiplicateur(niveauCanne);
			RecompenseUtil.donnerRecompensePecheur(player, ps, 10);

			event.setCancelled(true);

        }
		
		

	}

}
