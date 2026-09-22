package fr.kevyn.farmland.directives.gameplay.evenement;

import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.GameMenuHashMap;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;

/**
 * Clic sur un Jeton dans le /bag = vend tout le stock au prix actuel du
 * marche. Identifie le metier par SLOT (pas par Material, qui n'est
 * plus qu'une icone visuelle) - meme disposition que MenuBag.createmenu.
 */
public class MenuListenerFarm implements Listener {

	private static final DecimalFormat PRIX_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.FRANCE));

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;

		GameMenu gamemenu = null;
		for (GameMenu g : GameMenuHashMap.getInstance().getMenulist()) {
			if (event.getInventory().equals(g.getInventory())) { gamemenu = g; break; }
		}
		if (gamemenu == null) return;
		event.setCancelled(true);

		if (gamemenu.getTypemenu() != TypeMenu.BAG) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) return;

		String metier = metierDuSlot(event.getSlot());
		if (metier == null) return; // clic sur la deco ou un slot vide

		int quantite = getQuantiteJeton(ps, metier);
		if (quantite <= 0) {
			player.sendMessage("§cTu n'as rien à vendre !");
			return;
		}

		double prixUnitaire = MarketCalc.getPrixActuel(metier, MarketHolder.get());
		double totalDecimal = prixUnitaire * quantite;
		int gain = (int) Math.round(totalDecimal);

		setQuantiteJeton(ps, metier, 0);
		ps.setMoney(ps.getMoney() + gain);

		for (int i = 0; i < quantite; i++) {
			MarketCalc.enregistrerVente(metier);
		}

		player.sendMessage("§aVendu : " + quantite + "x Jeton " + metier + " pour " + PRIX_FORMAT.format(prixUnitaire) + " $FB/unité, soit " + gain + " $FB !");
		player.closeInventory();
	}

	private String metierDuSlot(int slot) {
		return switch (slot) {
			case 10 -> MarketCalc.MINEUR;
			case 12 -> MarketCalc.FARMEUR;
			case 13 -> MarketCalc.PECHEUR;
			case 14 -> MarketCalc.AGRICULTEUR;
			case 16 -> MarketCalc.TUEUR;
			default -> null;
		};
	}

	private int getQuantiteJeton(PlayerServer ps, String metier) {
		return switch (metier) {
			case MarketCalc.MINEUR -> ps.getJetonMineur();
			case MarketCalc.FARMEUR -> ps.getJetonFarmeur();
			case MarketCalc.PECHEUR -> ps.getJetonPecheur();
			case MarketCalc.AGRICULTEUR -> ps.getJetonAgriculteur();
			case MarketCalc.TUEUR -> ps.getJetonTueur();
			default -> 0;
		};
	}

	private void setQuantiteJeton(PlayerServer ps, String metier, int valeur) {
		switch (metier) {
			case MarketCalc.MINEUR -> ps.setJetonMineur(valeur);
			case MarketCalc.FARMEUR -> ps.setJetonFarmeur(valeur);
			case MarketCalc.PECHEUR -> ps.setJetonPecheur(valeur);
			case MarketCalc.AGRICULTEUR -> ps.setJetonAgriculteur(valeur);
			case MarketCalc.TUEUR -> ps.setJetonTueur(valeur);
		}
	}
}
