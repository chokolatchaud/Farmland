package fr.kevyn.farmland.Farming;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;

/**
 * Empeche l'os a moudre de faire pousser instantanement les cultures.
 */
public class BlockFertilizeListener implements Listener {

    @EventHandler
    public void onFertilize(BlockFertilizeEvent event) {
        event.setCancelled(true);
    }
}
