package fr.kevyn.farmland.directives.gameplay.evenement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.directives.gameplay.menus.MenuBag;
import fr.kevyn.farmland.directives.gameplay.menus.MenuVenteJetons;
import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.GameMenuHashMap;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;

/**
 * /bag : clic sur un Jeton = ouvre le menu de quantité (MenuVenteJetons) ;
 * dans ce menu, clic sur 10 / 25 / 50 / 100 % = vend cette part du stock au marché
 * (le prix baisse pendant la vente, le joueur touche le prix moyen).
 * Identifie le metier par SLOT (pas par Material, qui n'est plus qu'une icone visuelle).
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

		if (gamemenu.getTypemenu() != TypeMenu.BAG && gamemenu.getTypemenu() != TypeMenu.BAG_VENTE) return;

		// Ne jamais interpréter un clic dans l'inventaire du joueur comme un clic du menu.
		int rawSlot = event.getRawSlot();
		if (rawSlot < 0 || rawSlot >= event.getView().getTopInventory().getSize()) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) return;

		if (gamemenu.getTypemenu() == TypeMenu.BAG) {
			String metier = metierDuSlot(rawSlot);
			if (metier == null) return; // clic sur la deco
			int stock = getQuantiteJeton(ps, metier);
			if (stock <= 0) {
				player.sendMessage("§cTu n'as rien à vendre !");
				return;
			}
			ouvrirPlusTard(player, MenuVenteJetons.createmenu(metier, stock));
			return;
		}

		// ===== Menu de quantité =====
		if (!(gamemenu instanceof MenuVenteJetons menuVente)) return;
		String metier = menuVente.getMetier();

		if (rawSlot == MenuVenteJetons.SLOT_RETOUR) {
			ouvrirPlusTard(player, MenuBag.createmenu(ps));
			return;
		}

		int pourcentage = MenuVenteJetons.pourcentageDuSlot(rawSlot);
		if (pourcentage < 0) return;

		int stock = getQuantiteJeton(ps, metier);
		int quantite = MarketCalc.quantitePourcentage(stock, pourcentage);
		if (quantite <= 0) {
			player.sendMessage("§cTu n'as rien à vendre !");
			return;
		}

		// Offre et demande : le prix baisse pendant la vente, le joueur touche le prix moyen.
		MarketCalc.Vente vente = MarketCalc.vendre(metier, MarketHolder.get(), quantite);
		// le solde est un int : on ne le fait jamais deborder
		int gain = (int) Math.min(vente.gain(), (long) Integer.MAX_VALUE - Math.max(0, ps.getMoney()));

		setQuantiteJeton(ps, metier, stock - quantite);
		ps.setMoney(ps.getMoney() + gain);

		String nom = MarketCalc.nomAffiche(metier);
		JournalEvenements.enregistrer("vente_jetons", player.getName(), quantite + "x " + metier + " (" + pourcentage + "%) +" + gain + " $FB, prix "
				+ PRIX_FORMAT.format(vente.prixAvant()) + " -> " + PRIX_FORMAT.format(vente.prixApres()) + ", solde " + ps.getMoney() + " $FB");

		player.sendMessage("§aVendu : " + quantite + "x Jeton " + nom + " pour " + gain + " $FB §7(prix moyen " + PRIX_FORMAT.format(vente.prixMoyen()) + " $FB/unité)");
		player.sendMessage("§7Le prix du jeton " + nom + " passe de §f" + PRIX_FORMAT.format(vente.prixAvant()) + " $FB §7à §e" + PRIX_FORMAT.format(vente.prixApres()) + " $FB");
		player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.4f); // son de vente

		// Reste du stock : on rouvre le menu à jour (nouveaux prix), sinon retour au sac.
		int reste = stock - quantite;
		ouvrirPlusTard(player, reste > 0 ? MenuVenteJetons.createmenu(metier, reste) : MenuBag.createmenu(ps));
	}

	// On n'ouvre pas un inventaire pendant le clic sur un autre : tick suivant.
	private void ouvrirPlusTard(Player player, Inventory inventaire) {
		Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MenuListenerFarm.class), () -> {
			if (player.isOnline()) player.openInventory(inventaire);
		});
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
