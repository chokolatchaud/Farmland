package fr.kevyn.farmland.directives.metiers;

import fr.kevyn.farmland.directives.infrastructure.JobType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetiersLevelsMenu {

    private static final int MAX_LEVEL = 100;
    private static final int LEVELS_PER_PAGE = 45;
    private static final int PREVIOUS_SLOT = 45;
    private static final int BACK_SLOT = 49;
    private static final int NEXT_SLOT = 53;
    private static final Map<Inventory, LevelMenuState> STATES = new HashMap<>();

    public static void open(Player player, PlayerServer ps, JobType job, int page) {
        player.openInventory(createMetiersMenu(ps, job, page));
    }

    public static Inventory createMetiersMenu(PlayerServer ps, JobType job, int page) {
        int maxPage = getMaxPage();
        int safePage = Math.max(0, Math.min(page, maxPage));

        Inventory inv = Bukkit.createInventory(null, 54,
                "\u00A78" + getDisplayName(job) + " - niveaux " + (safePage + 1) + "/" + (maxPage + 1));
        new GameMenu(inv, TypeMenu.METIERS_LEVELS);
        GameMenu.fillmenu(Material.GRAY_STAINED_GLASS_PANE, inv);

        int playerLevel = getLevel(ps, job);
        int startLevel = safePage * LEVELS_PER_PAGE + 1;
        int endLevel = Math.min(startLevel + LEVELS_PER_PAGE - 1, MAX_LEVEL);

        for (int level = startLevel; level <= endLevel; level++) {
            inv.setItem(level - startLevel, createLevelItem(job, level, playerLevel));
        }

        if (safePage > 0) {
            inv.setItem(PREVIOUS_SLOT, createButton(Material.ARROW, "\u00A7ePage precedente"));
        }
        inv.setItem(BACK_SLOT, createButton(Material.BARRIER, "\u00A7cRetour aux metiers"));
        if (safePage < maxPage) {
            inv.setItem(NEXT_SLOT, createButton(Material.ARROW, "\u00A7ePage suivante"));
        }

        STATES.put(inv, new LevelMenuState(job, safePage));
        return inv;
    }

    public static LevelMenuState getState(Inventory inventory) {
        return STATES.get(inventory);
    }

    public static int getPreviousSlot() {
        return PREVIOUS_SLOT;
    }

    public static int getBackSlot() {
        return BACK_SLOT;
    }

    public static int getNextSlot() {
        return NEXT_SLOT;
    }

    private static ItemStack createLevelItem(JobType job, int level, int playerLevel) {
        Material material = getStatusMaterial(level, playerLevel);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getStatusColor(level, playerLevel) + "Niveau " + level);

        List<String> lore = new ArrayList<>();
        lore.add("\u00A77Metier : \u00A7f" + getDisplayName(job));
        lore.add("\u00A77Statut : " + getStatusLabel(level, playerLevel));
        lore.add("");
        lore.add("\u00A76Recompense :");
        lore.add("\u00A77- " + getRewardText(job, level));
        lore.add("\u00A77- Meilleur potentiel de jetons en farmant");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private static int getLevel(PlayerServer ps, JobType job) {
        return switch (job) {
            case MINEUR -> ps.getCobblestonegeneratorlevel();
            case FARMEUR -> ps.getHoueLevel();
            case PECHEUR -> ps.getCanneLevel();
            case AGRICULTEUR -> ps.getHacheLevel();
            case TUEUR -> ps.getEpeeLevel();
        };
    }

    private static Material getStatusMaterial(int level, int playerLevel) {
        if (level < playerLevel) {
            return Material.LIME_STAINED_GLASS_PANE;
        }
        if (level == playerLevel) {
            return Material.YELLOW_STAINED_GLASS_PANE;
        }
        return Material.RED_STAINED_GLASS_PANE;
    }

    private static String getStatusColor(int level, int playerLevel) {
        if (level < playerLevel) {
            return "\u00A7a";
        }
        if (level == playerLevel) {
            return "\u00A7e";
        }
        return "\u00A7c";
    }

    private static String getStatusLabel(int level, int playerLevel) {
        if (level < playerLevel) {
            return "\u00A7aAtteint";
        }
        if (level == playerLevel) {
            return "\u00A7eNiveau actuel";
        }
        return "\u00A7cNon atteint";
    }

    private static String getRewardText(JobType job, int level) {
        return switch (job) {
            case MINEUR -> "Generateur de cobblestone niveau " + level;
            case FARMEUR -> "Houe de farmeur niveau " + level;
            case PECHEUR -> "Canne a peche niveau " + level;
            case AGRICULTEUR -> "Hache d'agriculteur niveau " + level;
            case TUEUR -> "Epee de tueur niveau " + level;
        };
    }

    private static String getDisplayName(JobType job) {
        return switch (job) {
            case MINEUR -> "Mineur";
            case FARMEUR -> "Farmeur";
            case PECHEUR -> "Pecheur";
            case AGRICULTEUR -> "Agriculteur";
            case TUEUR -> "Tueur";
        };
    }

    private static int getMaxPage() {
        return (MAX_LEVEL - 1) / LEVELS_PER_PAGE;
    }

    public record LevelMenuState(JobType job, int page) {
    }
}
