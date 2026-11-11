package fr.kevyn.farmland.directives.plot.interfaces;

import java.util.ArrayList;
import java.util.List;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.kevyn.farmland.directives.gameplay.objet.CustomItemType;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;

public class MenuPlotVisit {

	// viewer : le joueur qui ouvre le menu (son propre plot privé reste visitable).
	public static Inventory createmenuplotvisit(String name, int page, Player viewer) {
        if (page < 1) page = 1;

        String titre = MessageColor.DARK_GREEN.apply(name) + MessageColor.DARK_GRAY.apply(" | Page " + page);
        Inventory inv = Bukkit.createInventory(null, 54, titre);
        GameMenu plotvisit = new GameMenu(inv, TypeMenu.PLOTVISIT);
        plotvisit.setPage(page);

        int playersPerPage = 45; // slots 0 à 44
        int startIndex = (page - 1) * playersPerPage;
        int slot = 0;

        List<PlayerServer> allPlayers = PlayerserverHashMap.getInstance().getHashMapPlayer().values().stream()
                .filter(ps -> ps.getPlotdata() != null)
                .toList();

        // Dernière ligne (slots 45 à 53) : vitre décorative, flèches et numéro de page par-dessus
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, creerVitreDecorative());
        }
        inv.setItem(49, creerNumeroPage(page));

        if (startIndex >= allPlayers.size()) return inv;

        int endIndex = Math.min(startIndex + playersPerPage, allPlayers.size());
        List<PlayerServer> playersToShow = allPlayers.subList(startIndex, endIndex);

        for (PlayerServer ps : playersToShow) {
            inv.setItem(slot++, creerTetePlot(ps, viewer));
        }

        // NAVIGATION - Page précédente
        if (page > 1) {
            ItemStack prev = CustomItemType.ARROW_PREV.create();
            ItemMeta prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(MessageColor.YELLOW.apply("« Page précédente"));
            }
            prev.setItemMeta(prevMeta);
            inv.setItem(45, prev);
        }

        // NAVIGATION - Page suivante
        if (endIndex < allPlayers.size()) {
            ItemStack next = CustomItemType.ARROW_NEXT.create();
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(MessageColor.YELLOW.apply("Page suivante »"));
            }
            next.setItemMeta(nextMeta);
            inv.setItem(53, next);
        }

        return inv;
    }

    // Tête du propriétaire : nom coloré et description. Un plot privé est affiché en gris.
    private static ItemStack creerTetePlot(PlayerServer ps, Player viewer) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return head;

        // Le clic sur la tête retrouve le plot grâce à ce propriétaire : ne pas l'enlever.
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(ps.getUuid()));

        boolean prive = ps.getPlotdata().getPrivateplot();
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(MessageColor.GRAY.apply("Propriétaire : ") + MessageColor.WHITE.apply(ps.getName()));
        lore.add(MessageColor.GRAY.apply("Bordure : ") + MessageColor.AQUA.apply(ps.getPlotdata().getWorldborder() + " blocs"));

        boolean proprietaire = viewer != null && viewer.getUniqueId().equals(ps.getUuid());

        if (prive && proprietaire) {
            meta.setDisplayName(MessageColor.GOLD.apply("Ton plot (privé)"));
            lore.add(MessageColor.GRAY.apply("Accès : ") + MessageColor.RED.apply("Privé"));
            lore.add("");
            lore.add(MessageColor.YELLOW.apply("Clique pour visiter"));
        } else if (prive) {
            meta.setDisplayName(MessageColor.DARK_GRAY.apply("Plot de " + ps.getName()));
            lore.add(MessageColor.GRAY.apply("Accès : ") + MessageColor.RED.apply("Privé"));
            lore.add("");
            lore.add(MessageColor.DARK_GRAY.apply("Ce plot ne peut pas être visité"));
        } else {
            meta.setDisplayName(MessageColor.GREEN.apply("Plot de " + ps.getName()));
            lore.add(MessageColor.GRAY.apply("Accès : ") + MessageColor.GREEN.apply("Public"));
            lore.add("");
            lore.add(MessageColor.YELLOW.apply("Clique pour visiter"));
        }

        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }

    private static ItemStack creerVitreDecorative() {
        ItemStack vitre = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = vitre.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            vitre.setItemMeta(meta);
        }
        return vitre;
    }

    private static ItemStack creerNumeroPage(int page) {
        ItemStack papier = new ItemStack(Material.PAPER);
        ItemMeta meta = papier.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageColor.GOLD.apply("Page " + page));
            papier.setItemMeta(meta);
        }
        return papier;
    }

}
