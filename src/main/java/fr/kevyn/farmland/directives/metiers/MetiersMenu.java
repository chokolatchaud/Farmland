package fr.kevyn.farmland.directives.metiers;

import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MetiersMenu {

    public static void open(Player player) {
        player.openInventory(createMetiersMenu());
    }

    public static Inventory createMetiersMenu() {
        Inventory inv = Bukkit.createInventory(null, 36, "\u00A78Tes metiers");
        new GameMenu(inv, TypeMenu.METIERS);
        GameMenu.fillmenu(Material.LIGHT_BLUE_STAINED_GLASS_PANE, inv);

        GameMenu.set_oneitem_menu(new ItemStack(Material.DIAMOND_HOE), "\u00A7aFarmeur", 11, inv);
        GameMenu.set_oneitem_menu(new ItemStack(Material.DIAMOND_AXE), "\u00A7aAgriculteur", 13, inv);
        GameMenu.set_oneitem_menu(new ItemStack(Material.DIAMOND_PICKAXE), "\u00A7aMineur", 15, inv);
        GameMenu.set_oneitem_menu(new ItemStack(Material.FISHING_ROD), "\u00A7aPecheur", 21, inv);
        GameMenu.set_oneitem_menu(new ItemStack(Material.DIAMOND_SWORD), "\u00A7aTueur", 23, inv);

        return inv;
    }
}
