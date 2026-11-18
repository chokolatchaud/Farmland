package fr.kevyn.farmland.directives.gameplay.menus;

import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;
import fr.kevyn.farmland.doonees.marche.Market;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Menu de quantité du /bag : vendre 10 %, 25 %, 50 % ou 100 % du stock d'un métier.
 * Chaque bouton affiche la quantité, le gain estimé et le prix après la vente
 * (le prix baisse pendant la vente : vendre en petites portions et attendre
 * entre deux ventes laisse le marché remonter).
 */
public class MenuVenteJetons extends GameMenu {

	/** Slots des boutons, dans l'ordre de MarketCalc.PORTIONS. */
	public static final int[] SLOTS_PORTIONS = {10, 12, 14, 16};
	public static final int SLOT_INFO = 4;
	public static final int SLOT_RETOUR = 22;

	private static final DecimalFormat PRIX_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.FRANCE));
	private static final Material[] ICONES_PORTIONS = {
		Material.GOLD_NUGGET, Material.GOLD_INGOT, Material.GOLD_BLOCK, Material.EMERALD_BLOCK
	};

	private final String metier;

	private MenuVenteJetons(Inventory inventory, String metier) {
		super(inventory, TypeMenu.BAG_VENTE);
		this.metier = metier;
	}

	public String getMetier() {
		return metier;
	}

	/** Pourcentage du bouton à ce slot, ou -1. */
	public static int pourcentageDuSlot(int slot) {
		for (int i = 0; i < SLOTS_PORTIONS.length; i++) {
			if (SLOTS_PORTIONS[i] == slot) return MarketCalc.PORTIONS[i];
		}
		return -1;
	}

	public static Inventory createmenu(String metier, int stock) {
		Inventory inv = Bukkit.createInventory(null, 27,
				fr.kevyn.farmland.directives.infrastructure.Glyphes.titreAvecFond(fr.kevyn.farmland.directives.infrastructure.Glyphes.FOND_VENTE, "Vendre : Jeton " + MarketCalc.nomAffiche(metier)));
		new MenuVenteJetons(inv, metier);
		// fond illustré : les cases vides restent transparentes (pas de vitres)

		Market market = MarketHolder.get();
		double prix = MarketCalc.getPrixActuel(metier, market);
		int coef = market.getCoefficient(metier);
		String couleur = coef >= 100 ? "§a" : coef >= 75 ? "§e" : "§c";

		GameMenu.set_oneitem_menu(MenuBag.jetonItem(metier),
			"§6Jeton " + MarketCalc.nomAffiche(metier) + " §7x " + stock,
			List.of(
				"§7Prix actuel : §f" + PRIX_FORMAT.format(prix) + " $FB " + couleur + "(" + coef + "%)",
				"§7Prix de base : §f" + MarketCalc.getPrixDeBase(metier) + " $FB",
				"",
				"§7Le prix baisse pendant la vente.",
				"§7Vends en petites portions et attends",
				"§7que le marché remonte pour gagner plus."
			), SLOT_INFO, inv);

		for (int i = 0; i < SLOTS_PORTIONS.length; i++) {
			int pourcentage = MarketCalc.PORTIONS[i];
			int quantite = MarketCalc.quantitePourcentage(stock, pourcentage);
			List<String> lore;
			if (quantite <= 0) {
				lore = List.of("§cTu n'as rien à vendre.");
			} else {
				MarketCalc.Vente estimation = MarketCalc.estimer(metier, market, quantite);
				lore = List.of(
					"§7Quantité : §f" + quantite + (quantite > 1 ? " jetons" : " jeton"),
					"§7Gain estimé : §a" + estimation.gain() + " $FB",
					"§7Prix moyen : §f" + PRIX_FORMAT.format(estimation.prixMoyen()) + " $FB",
					"§7Prix après la vente : §e" + PRIX_FORMAT.format(estimation.prixApres()) + " $FB",
					"",
					"§eClique pour vendre"
				);
			}
			GameMenu.set_oneitem_menu(new ItemStack(ICONES_PORTIONS[i]),
				"§6Vendre " + pourcentage + " %", lore, SLOTS_PORTIONS[i], inv);
		}

		GameMenu.set_oneitem_menu(new ItemStack(Material.ARROW), "§7← Retour au sac", SLOT_RETOUR, inv);
		return inv;
	}
}
