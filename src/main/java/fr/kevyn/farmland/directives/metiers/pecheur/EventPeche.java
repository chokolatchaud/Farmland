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

	/**
	 * Une prise compte comme 7 actions : la pêche est ~7 fois plus lente que les
	 * autres métiers (mesuré sur le serveur de test : ~230 prises/h contre ~1 700 minerais/h).
	 */
	static final int MULTIPLICATEUR_PECHE = 7;
	static final int XP_PAR_ACTION = 10;

	@EventHandler
	public void onfish(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			Player player = event.getPlayer();
			PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
			if (ps == null) return;
			ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
			if (!Outils.isOutilsAttendu(mainPrincipale, Material.FISHING_ROD)) {
				player.sendMessage("§cPêche avec la canne du Pêcheur (/peche) pour gagner des jetons !");
				event.setCancelled(true);
				return;

			}
			int jeton = jetonsPeche(ps.getCanneLevel());
			int xp = XP_PAR_ACTION * MULTIPLICATEUR_PECHE;
			ps.addJetonPecheur(jeton);
			RecompenseUtil.donnerRecompensePecheur(player, ps, xp, jeton); // XP et jetons annoncés dans la barre d'action

			event.setCancelled(true);

        }



	}

	/** Jetons d'une prise : somme de MULTIPLICATEUR_PECHE tirages au niveau de la canne. */
	static int jetonsPeche(int niveauCanne) {
		int niveau = Math.max(1, niveauCanne);
		int total = 0;
		for (int i = 0; i < MULTIPLICATEUR_PECHE; i++) {
			total += MultiplicateurUtil.tirerMultiplicateur(niveau);
		}
		return total;
	}

}
