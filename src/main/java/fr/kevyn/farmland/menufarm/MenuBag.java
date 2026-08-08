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
 * Le /bag - affiche les 5 Jetons de metier. Chaque Jeton vit dans son
 * propre champ dedie sur PlayerServer (pas un inventaire generique) - le
 * Material ici ne sert QUE d'icone visuelle pour l'affichage.
 */
public class MenuBag {

	public static Inventory createmenu(PlayerServer ps) {
		Inventory inv = Bukkit.createInventory(null, 27, "§6Sac");
		new GameMenu(inv, TypeMenu.BAG);
		GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);

		placerJeton(inv, 10, Material.IRON_NUGGET, MarketCalc.MINEUR, "§7Jeton Mineur", ps.getJetonMineur());
		placerJeton(inv, 12, Material.WHEAT, MarketCalc.FARMEUR, "§aJeton Farmeur", ps.getJetonFarmeur());
		placerJeton(inv, 13, Material.PRISMARINE_SHARD, MarketCalc.PECHEUR, "§bJeton Pêcheur", ps.getJetonPecheur());
		placerJeton(inv, 14, Material.LEATHER, MarketCalc.AGRICULTEUR, "§eJeton Agriculteur", ps.getJetonAgriculteur());
		placerJeton(inv, 16, Material.GUNPOWDER, MarketCalc.TUEUR, "§cJeton Tueur", ps.getJetonTueur());

		return inv;
	}

	private static void placerJeton(Inventory inv, int slot, Material icone, String metier, String nom, int quantite) {
		int prixNormal = MarketCalc.getPrixDeBase(metier);
		int prixMarche = MarketCalc.getPrixActuel(metier, MarketHolder.get());

		ItemStack item = new ItemStack(icone);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nom + " §7x " + quantite + " §7(§f" + prixNormal + "$ §7→ §e" + prixMarche + "$§7)");
		item.setItemMeta(meta);

		inv.setItem(slot, item);
	}
}
