package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.market.MarketCalc;
import fr.kevyn.farmland.market.MarketHolder;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.GameMenuHashMap;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Clic sur un Jeton dans le /bag = vend tout le stock au prix actuel du marche.
 */
public class MenuListenerFarm implements Listener {

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

		ItemStack clicked = event.getCurrentItem();
		if (clicked == null || clicked.getType() == Material.AIR
				|| clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) return;

		Material jeton = clicked.getType();
		if (MarketCalc.getMetierDeRessource(jeton) == null) return; // pas un vrai jeton connu

		int quantite = ps.getRessource(jeton);
		if (quantite <= 0) {
			player.sendMessage("§cTu n'as rien à vendre !");
			return;
		}

		int prixUnitaire = MarketCalc.getPrixActuel(jeton, MarketHolder.get());
		int total = prixUnitaire * quantite;

		ps.RemoveRessource(jeton, quantite);
		ps.setMoney(ps.getMoney() + total);

		for (int i = 0; i < quantite; i++) {
			MarketCalc.enregistrerVente(jeton);
		}

		player.sendMessage("§aVendu : " + quantite + "x " + jeton.name() + " pour " + total + " $FB !");
		player.closeInventory();
	}
}
