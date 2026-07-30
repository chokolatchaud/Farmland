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

    // Prix de base des upgrades dans l'ordre, pour UN palier (21 upgrades)
    private static final int[] UPGRADE_COSTS = {50, 50, 50, 50, 50, 50, 50, 65, 65, 65, 65, 65, 65, 65, 100, 100, 100, 100, 100, 100, 100};

    /** Nombre d'upgrades affiches par palier (le systeme est infini au-dela) */
    private static final int UPGRADES_PER_PAGE = UPGRADE_COSTS.length; // 21

    /** Le systeme est infini : conserve pour compatibilite mais n'est plus une limite dure */
    public static int getMaxUpgrades() {
        return UPGRADES_PER_PAGE;
    }

    /**
     * Cout reel (cote serveur) de l'upgrade au rang absolu donne.
     * Palier 0 (rangs 0-20) = prix de base. Palier 1 (rangs 21-41) = prix +20.
     * Palier 2 (rangs 42-62) = prix +40. Et ainsi de suite, a l'infini.
     */
    public static int getCost(int rank) {
        if (rank < 0) return -1;
        int page = rank / UPGRADES_PER_PAGE;
        int posInPage = rank % UPGRADES_PER_PAGE;
        return UPGRADE_COSTS[posInPage] + page * 20;
    }
	
	public static Inventory createmenuplotUpgrade(String name, int page, PlayerServer playerserver) {

        Inventory inv = Bukkit.createInventory(null, 54);
        GameMenu plotupgrade = new GameMenu(inv, TypeMenu.PLOTUPGRADE);
        
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);

        int rankPlayer = playerserver.getUpgrade();
        int currentPage = rankPlayer / UPGRADES_PER_PAGE;
        int rankPlayerInPage = rankPlayer % UPGRADES_PER_PAGE;

        List<Integer> slots = Arrays.asList(
                1,2,3,4,5,6,7,
                19,20,21,22,23,24,25,
                37,38,39,40,41,42,43
        );

        int posInPage = 0;
        for (int slot : slots) {
            int absoluteRank = currentPage * UPGRADES_PER_PAGE + posInPage;
            int cost = getCost(absoluteRank);

            if (rankPlayerInPage > posInPage) {
                GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_BOUGHT.create(), "Déjà acheté", slot, inv);
            } else if (rankPlayerInPage == posInPage) {
                // Prochain upgrade : le seul achetable
                GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_LOCKED.create(), "Coût : " + cost + " $FB (+5 bordure) — Palier " + (currentPage + 1), slot, inv);
            } else {
                GameMenu.set_oneitem_menu(CustomItemType.UPGRADE_LOCKED.create(), "Verrouillé — " + cost + " $FB", slot, inv);
            }
            posInPage++;
        }

        return inv;
    }
	
	

}
