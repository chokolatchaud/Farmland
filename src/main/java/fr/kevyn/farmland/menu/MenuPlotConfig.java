package fr.kevyn.farmland.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.game.CustomItemType;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class MenuPlotConfig {
	
	public static Inventory createmenuplotconfig(String name, PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 27);
        GameMenu plotconfig = new GameMenu(inv, TypeMenu.PLOTCONFIG);
        GameMenu.fillmenu(Material.GREEN_STAINED_GLASS_PANE, inv);
        
        // CLOCK - Jour/Nuit
        GameMenu.set_oneitem_menu(CustomItemType.CLOCK_DAYNIGHT.create(), "Météo", 10, inv);
 
        // LAPIS - Pluie
        GameMenu.set_oneitem_menu(CustomItemType.RAIN_TOGGLE.create(), "Pluie", 19, inv);
        
        
        // CYAN WOOL - Temps figé
        GameMenu.set_oneitem_menu(CustomItemType.TIME_FREEZE.create(), "Temps qui passe", 1, inv);
     
        // WATER BUCKET - Eau/Lave
        String namewaterlava = "";
        if(playerserver.getPlotdata().getwaterlava()) {
        	namewaterlava = "Eau/Lave désactivé";
        }else {
        	namewaterlava = "Eau/Lave activé";
        	
        }
        GameMenu.set_oneitem_menu(CustomItemType.WATERLAVASELECTION.create(), namewaterlava, 12, inv);
        

        // DOOR - Privé/Public
        String namePrivatepublic = "Public/Privée";
        if(playerserver.getPlotdata().getPrivateplot()) {
        	namewaterlava = "Plot privé";
        }else {
        	namewaterlava = "Plot public";
        	
        }
        GameMenu.set_oneitem_menu(CustomItemType.DOOR_PRIVACY.create(), namePrivatepublic, 14, inv);
        GameMenu.set_oneitem_menu(CustomItemType.SUGGEST.create(), "A venir", 16, inv);
        return inv;
    }

}
