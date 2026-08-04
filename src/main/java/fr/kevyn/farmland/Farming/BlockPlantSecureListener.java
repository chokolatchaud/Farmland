package fr.kevyn.farmland.Farming;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Interdit a tout joueur de poser manuellement une culture (blé, carotte,
 * patate, tige de citrouille). SEUL le Planteur (via HoueFarmEvent, qui pose
 * le bloc directement en code sans passer par ce BlockPlaceEvent) peut le
 * faire. Empeche un joueur d'en placer via son inventaire creatif ou un
 * item ramasse ailleurs.
 */
public class BlockPlantSecureListener implements Listener {

	@EventHandler
	public void onPlacePlant(BlockPlaceEvent event) {
		Material type = event.getBlock().getType();

		if (isCulture(type)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cSeul le Planteur peut faire pousser des cultures !");
		}
	}

	private boolean isCulture(Material type) {
		return switch (type) {
			case WHEAT, CARROT, POTATO, PUMPKIN_STEM -> true;
			default -> false;
		};
	}
}
