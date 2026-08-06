package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.Farming.HoueFarmeur;
import fr.kevyn.farmland.agriculteur.HacheFarm;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.GameMenuHashMap;
import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.tueur.epeeFarm;

public class MenuListenerFarm implements Listener {

	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        GameMenu gamemenu = null;
        for (GameMenu g : GameMenuHashMap.getInstance().getMenulist()) {
            if (event.getInventory().equals(g.getInventory())) { gamemenu = g; break; }
        }
        if (gamemenu == null) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemStack iteminhand = player.getInventory().getItemInMainHand();
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return;

        if (gamemenu.getTypemenu() == TypeMenu.BAG) {
        	int quantite = ps.getRessource(clicked.getType());
        	if (quantite <= 0) {
        		player.sendMessage("§cTu n'as rien à vendre pour cet item !");
        		return;
        	}

        	int prixUnitaire = fr.kevyn.farmland.market.MarketCalc.getPrixActuel(clicked.getType(), fr.kevyn.farmland.market.MarketHolder.get());
        	int total = prixUnitaire * quantite;

        	ps.RemoveRessource(clicked.getType(), quantite);
        	ps.setMoney(ps.getMoney() + total);

        	for (int i = 0; i < quantite; i++) {
        		fr.kevyn.farmland.market.MarketCalc.enregistrerVente(clicked.getType());
        	}

        	player.sendMessage("§aVendu : " + quantite + "x " + clicked.getType().name() + " pour " + total + " $FB !");
        }

        if (gamemenu.getTypemenu() == TypeMenu.SEEDS) {
        	if (!Outils.isOutilsAttendu(iteminhand, Material.NETHERITE_HOE)) {
        		player.sendMessage("§cTu dois tenir ta Houe en main principale !");
        		return;
        	}
        	if (ListFarmItem.getlistSeeditem().contains(clicked.getType())) {
        		HoueFarmeur.setGraineSelectionnee(iteminhand, clicked.getType());
        		player.sendMessage("§aGraine sélectionnée : " + clicked.getType().name());
        	}
        }

        if (gamemenu.getTypemenu() == TypeMenu.SHOP) {
        	if (ListFarmItem.getlistshop().contains(clicked.getType())) {
        		int prix = SlotPriceFarmItem.priceformaterial(clicked.getType());
        		if (ps.getMoney() < prix) {
        			player.sendMessage("§cTu n'as pas assez d'argent ! (" + prix + " $FB)");
        			return;
        		}

        		ps.setMoney(ps.getMoney() - prix);
        		ps.addRessource(clicked.getType(), 1);
        		player.sendMessage("§a+1 " + clicked.getType().name() + " acheté ! (-" + prix + " $FB)");
        	}
        }
        if (gamemenu.getTypemenu() == TypeMenu.PASSIF_MOB) {
        	if (!Outils.isOutilsAttendu(iteminhand, Material.NETHERITE_AXE)) {
        		player.sendMessage("§cTu dois tenir ta Hache en main principale !");
        		return;
        	}
        	if (ListFarmItem.listAgriculteuritem().contains(clicked.getType())) {
        		HacheFarm.setOeufSelection(iteminhand, clicked.getType());
        		player.sendMessage("§aOeuf sélectionnée : " + clicked.getType().name());
        	}
        }
        if (gamemenu.getTypemenu() == TypeMenu.AGGRESSIVE_MOB) {
        	if (!Outils.isOutilsAttendu(iteminhand, Material.NETHERITE_SWORD)) {
        		player.sendMessage("§cTu dois tenir ton épée en main principale !");
        		return;
        	}
        	if (ListFarmItem.listTueuritem().contains(clicked.getType())) {
        		epeeFarm.setOeufSelectionnee(iteminhand, clicked.getType());
        		player.sendMessage("§aOeuf sélectionnée : " + clicked.getType().name());
        	}
        }
	}

}
