package fr.kevyn.farmland.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.kevyn.farmland.game.CustomItemType;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class MenuPlotVisit {
	
	public static Inventory createmenuplotvisit(String name, int page) {
        if (page < 1) page = 1;

        Inventory inv = Bukkit.createInventory(null, 54, name + " | Page " + page);
        GameMenu plotvisit = new GameMenu(inv, TypeMenu.PLOTVISIT);

        int playersPerPage = 45; // slots 0 à 44
        int startIndex = (page - 1) * playersPerPage;
        int slot = 0;

        List<PlayerServer> allPlayers = PlayerserverHashMap.getInstance().getHashMapPlayer().values().stream()
                .filter(ps -> ps.getPlotdata() != null)
                .toList();

        if (startIndex >= allPlayers.size()) return inv;

        int endIndex = Math.min(startIndex + playersPerPage, allPlayers.size());
        List<PlayerServer> playersToShow = allPlayers.subList(startIndex, endIndex);

        for (PlayerServer ps : playersToShow) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(ps.getUuid()));
                meta.setDisplayName(ChatColor.GREEN + ps.getName());
                head.setItemMeta(meta);
            }

            inv.setItem(slot++, head);
        }

        // NAVIGATION - Page précédente
        if (page > 1) {
            ItemStack prev = CustomItemType.ARROW_PREV.create();
            ItemMeta prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.GOLD + "Page précédente");
            }
            prev.setItemMeta(prevMeta);
            inv.setItem(45, prev);
        }

        // NAVIGATION - Page suivante
        if (endIndex < allPlayers.size()) {
            ItemStack next = CustomItemType.ARROW_NEXT.create();
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.GOLD + "Page suivante");
            }
            next.setItemMeta(nextMeta);
            inv.setItem(53, next);
        }

        return inv;
    }

}
