package fr.kevyn.farmland.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.game.CustomItemType;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class MenuPlotUpgrade {

    // Prix des upgrades dans l'ordre
    private static final int[] UPGRADE_COSTS = {10, 10, 10, 10, 10, 10, 10, 25, 25, 25, 25, 25, 25, 25, 60, 60, 60, 60, 60, 60, 60};
	
	public static Inventory createmenuplotUpgrade(String name, int page, PlayerServer playerserver) {

        Inventory inv = Bukkit.createInventory(null, 54);
        GameMenu plotupgrade = new GameMenu(inv, TypeMenu.PLOTUPGRADE);
        
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);

        int rankPlayer = playerserver.getUpgrade();
        int rankPlugin = 0;

        List<Integer> slots = Arrays.asList(
                1,2,3,4,5,6,7,
                19,20,21,22,23,24,25,
                37,38,39,40,41,42,43
        );

        for (int slot : slots) {
            int cost = rankPlugin < UPGRADE_COSTS.length ? UPGRADE_COSTS[rankPlugin] : 120;

            if (rankPlayer > rankPlugin) {
                GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_BOUGHT.create(), "Déjà acheté", slot, inv);
            } else {
                GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_LOCKED.create(), "Coût : " + cost, slot, inv);
            }
            rankPlugin++;
        }

        return inv;
    }
	
	

}
