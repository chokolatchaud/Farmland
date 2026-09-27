package fr.kevyn.farmland.directives.metiers;

import fr.kevyn.farmland.directives.infrastructure.JobType;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.GameMenuHashMap;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MetiersMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        GameMenu gameMenu = getGameMenu(event);
        if (gameMenu == null) {
            return;
        }

        event.setCancelled(true);
        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) {
            player.sendMessage("\u00A7cImpossible de trouver tes donnees joueur.");
            return;
        }

        if (gameMenu.getTypemenu() == TypeMenu.METIERS) {
            handleMetiersClick(player, ps, clickedItem);
            return;
        }

        if (gameMenu.getTypemenu() == TypeMenu.METIERS_LEVELS) {
            handleLevelsClick(player, ps, event.getSlot(), event.getView().getTopInventory());
        }
    }

    private GameMenu getGameMenu(InventoryClickEvent event) {
        for (GameMenu gameMenu : GameMenuHashMap.getInstance().getMenulist()) {
            if (event.getView().getTopInventory().equals(gameMenu.getInventory())) {
                return gameMenu;
            }
        }
        return null;
    }

    private void handleMetiersClick(Player player, PlayerServer ps, ItemStack clickedItem) {
        JobType job = switch (clickedItem.getType()) {
            case DIAMOND_HOE -> JobType.FARMEUR;
            case DIAMOND_AXE -> JobType.AGRICULTEUR;
            case DIAMOND_PICKAXE -> JobType.MINEUR;
            case FISHING_ROD -> JobType.PECHEUR;
            case DIAMOND_SWORD -> JobType.TUEUR;
            default -> null;
        };

        if (job != null) {
            MetiersLevelsMenu.open(player, ps, job, 0);
        }
    }

    private void handleLevelsClick(Player player, PlayerServer ps, int slot, org.bukkit.inventory.Inventory inventory) {
        MetiersLevelsMenu.LevelMenuState state = MetiersLevelsMenu.getState(inventory);
        if (state == null) {
            return;
        }

        if (slot == MetiersLevelsMenu.getPreviousSlot()) {
            MetiersLevelsMenu.open(player, ps, state.job(), state.page() - 1);
        } else if (slot == MetiersLevelsMenu.getBackSlot()) {
            MetiersMenu.open(player);
        } else if (slot == MetiersLevelsMenu.getNextSlot()) {
            MetiersLevelsMenu.open(player, ps, state.job(), state.page() + 1);
        }
    }
}
