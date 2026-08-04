package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.Farming.HoueFarmeur;
import fr.kevyn.farmland.menu.GameMenu;
import fr.kevyn.farmland.menu.GameMenuHashMap;
import fr.kevyn.farmland.menu.TypeMenu;

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
        
        if(gamemenu.getTypemenu() == TypeMenu.SEEDS) {
        	if(ListFarmItem.getlistSeeditem().contains(clicked.getType())) {
        		HoueFarmeur.setGraineSelectionnee(iteminhand, clicked.getType());
	
        	}
        }
        
        
	}
	
	

}
