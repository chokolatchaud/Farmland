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
	
	public static Inventory createmenuplotUpgrade(String name, int page, PlayerServer playerserver) {

        Inventory inv = Bukkit.createInventory(null, 54);
        GameMenu plotupgrade = new GameMenu(inv, TypeMenu.PLOTUPGRADE);
        
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);

        int rankPlayer = playerserver.getUpgrade();
        int rankPlugin = 1;
        int moneyCost = 5;

        List<Integer> slots = Arrays.asList(
                1,2,3,4,5,6,7,
                19,20,21,22,23,24,25,
                37,38,39,40,41,42,43
        );

        for (int i = 21; i <= rankPlayer; i += 21) {
            moneyCost += 15;
            rankPlugin += 21;
        }

        for (int slot : slots) {
            if (slot == 19 || slot == 37) {
                moneyCost += 5;
            }

            if (rankPlayer >= rankPlugin) {
                // UPGRADE ACHETÉ - Icône verte avec check
            	GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_BOUGHT.create(), "Déjà acheté", slot, inv);
                rankPlugin++;
            } else {
                // UPGRADE DISPONIBLE - Icône rouge avec cadenas
            	GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_LOCKED.create(), "Coût : " + moneyCost, slot, inv);
                
            }
        }

        return inv;
    }
	
	

}
