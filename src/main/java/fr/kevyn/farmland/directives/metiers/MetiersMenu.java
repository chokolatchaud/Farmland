package fr.kevyn.farmland.directives.metiers;

import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MetiersMenu {

    public static void open(Player player, PlayerServer ps) {
        player.openInventory(createMetiersMenu(ps));
    }

    public static Inventory createMetiersMenu(PlayerServer ps) {
        Inventory inv = Bukkit.createInventory(null, 36, "\u00A78Tes metiers");
        new GameMenu(inv, TypeMenu.METIERS);
        GameMenu.fillmenu(Material.LIGHT_BLUE_STAINED_GLASS_PANE, inv);

        inv.setItem(
                11,
                createJobItem(
                        Material.DIAMOND_HOE,
                        "§aFarmeur",
                        ps.getHoueLevel(),
                        ps.getXp("Farmeur"),
                        ps.getJetonFarmeur()
                )
        );

        inv.setItem(
                13,
                createJobItem(
                        Material.DIAMOND_AXE,
                        "§aAgriculteur",
                        ps.getHacheLevel(),
                        ps.getXp("Agriculteur"),
                        ps.getJetonAgriculteur()
                )
        );

        inv.setItem(
                15,
                createJobItem(
                        Material.DIAMOND_PICKAXE,
                        "§aMineur",
                        ps.getCobblestonegeneratorlevel(),
                        ps.getXp("Mineur"),
                        ps.getJetonMineur()
                )
        );

        inv.setItem(
                21,
                createJobItem(
                        Material.FISHING_ROD,
                        "§aPecheur",
                        ps.getCanneLevel(),
                        ps.getXp("Pecheur"),
                        ps.getJetonPecheur()
                )
        );

        inv.setItem(
                23,
                createJobItem(
                        Material.DIAMOND_SWORD,
                        "§aTueur",
                        ps.getEpeeLevel(),
                        ps.getXp("Tueur"),
                        ps.getJetonTueur()
                )
        );

        return inv;
    }

    private static ItemStack createJobItem(
            Material material,
            String name,
            int level,
            int xp,
            int tokens
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        int xpNeeded = RecompenseUtil.getXpNeeded(level);

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("§7Niveau : §e" + level);
        lore.add("§7XP : §b" + xp + " §7/ §b" + xpNeeded);
        lore.add("§7Jetons : §6" + tokens);
        lore.add("");
        lore.add("§eClique pour voir les niveaux");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }


}
