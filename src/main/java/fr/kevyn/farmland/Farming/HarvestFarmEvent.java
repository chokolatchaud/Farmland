package fr.kevyn.farmland.Farming;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Recolte d'une culture arrivee a maturite avec la Houe du Farmeur.
 * Ne recolte QUE si la position est bien passee par PositionsLegitimesFarm
 * (plantee via le Planteur, pas posee/collee par un joueur ou WorldEdit).
 * Une chance de ne rien obtenir existe (le bloc casse quand meme, mais rien
 * n'atterrit dans le /bag).
 */
public class HarvestFarmEvent implements Listener {

	private static final Random random = new Random();
	private static final float CHANCE_DE_RATER = 0.20f; // 20% de chance de ne rien obtenir

	@EventHandler
	public void onHarvest(BlockBreakEvent event) {
		if (event.isCancelled()) return; // deja refuse par un autre handler (protection de plot, etc.)

		Player player = event.getPlayer();
		ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
		if (!HoueFarmeur.isHoueFarmeur(mainPrincipale)) return;

		Block block = event.getBlock();
		if (!(block.getBlockData() instanceof Ageable ageable)) return;

		// verifie que la culture est bien arrivee a MATURITE (age maximal)
		if (ageable.getAge() < ageable.getMaximumAge()) return;

		Material ressource = blocCultureVersRessource(block.getType());
		if (ressource == null) return;

		// LE VRAI VERROU ANTI-TRICHE : cette culture est-elle bien nee d'une
		// vraie plantation via le Planteur ? Sinon (posee par un joueur normal
		// ou collee via WorldEdit deja a maturite), pas de recolte du tout.
		if (!PositionsLegitimesFarm.estLegitime(block.getLocation())) {
			player.sendMessage("§cCette culture n'a pas ete plantee legitimement !");
			event.setCancelled(true);
			return;
		}

		PositionsLegitimesFarm.retirer(block.getLocation());

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) return;

		event.setDropItems(false); // jamais de vrai drop au sol, tout passe par le /bag

		if (random.nextFloat() < CHANCE_DE_RATER) {
			player.sendMessage("§7Cette récolte n'a rien donné...");
			return;
		}

		ps.addRessource(ressource, 1);
		player.sendMessage("§a+1 " + ressource.name() + " ajouté à ton /bag !");
	}

	private Material blocCultureVersRessource(Material blocCulture) {
		return switch (blocCulture) {
			case WHEAT -> Material.WHEAT;
			case CARROT -> Material.CARROT;
			case POTATO -> Material.POTATO;
			case PUMPKIN_STEM -> Material.PUMPKIN;
			default -> null;
		};
	}
}
