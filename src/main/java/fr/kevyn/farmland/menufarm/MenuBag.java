package fr.kevyn.farmland.menufarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.market.MarketCalc;
import fr.kevyn.farmland.market.MarketHolder;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;

/**
 * Le /bag - affiche les 5 Jetons de metier (fini les 20 ressources
 * individuelles). Clic sur un jeton = vend tout le stock au prix actuel du
 * marche (voir MenuListenerFarm pour la logique de vente).
 */
public class MenuBag {

	public static Inventory createmenu(PlayerServer ps) {
		Inventory inv = Bukkit.createInventory(null, 27, "§6Sac");
		new GameMenu(inv, TypeMenu.BAG);
		GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);

		placerJeton(inv, 10, RecompenseUtil.JETON_MINEUR, "§7Jeton Mineur", ps);
		placerJeton(inv, 12, RecompenseUtil.JETON_FARMEUR, "§aJeton Farmeur", ps);
		placerJeton(inv, 13, RecompenseUtil.JETON_PECHEUR, "§bJeton Pêcheur", ps);
		placerJeton(inv, 14, RecompenseUtil.JETON_AGRICULTEUR, "§eJeton Agriculteur", ps);
		placerJeton(inv, 16, RecompenseUtil.JETON_TUEUR, "§cJeton Tueur", ps);

		return inv;
	}

	private static void placerJeton(Inventory inv, int slot, Material material, String nom, PlayerServer ps) {
		int quantite = ps.getRessource(material);
		int prixNormal = MarketCalc.getPrixDeBase(material);
		int prixMarche = MarketCalc.getPrixActuel(material, MarketHolder.get());

		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nom + " §7x " + quantite + " §7(§f" + prixNormal + "$ §7→ §e" + prixMarche + "$§7)");
		item.setItemMeta(meta);

		inv.setItem(slot, item);
	}
}
