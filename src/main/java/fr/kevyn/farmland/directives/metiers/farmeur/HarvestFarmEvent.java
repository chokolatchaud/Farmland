package fr.kevyn.farmland.directives.metiers.farmeur;

import fr.kevyn.farmland.directives.gameplay.recompense.MultiplicateurUtil;
import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;


public class HarvestFarmEvent implements Listener {

	@EventHandler
	public void onHarvest(BlockBreakEvent event) {
		if (event.isCancelled()) {

			
			return; // deja refuse par un autre handler (protection de plot, etc.)
		}
		event.setDropItems(false);
		Player player = event.getPlayer();
		ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
		if (!Outils.isOutilsAttendu(mainPrincipale, Material.NETHERITE_HOE)) {
			return;
		}

		Block block = event.getBlock();
		if (!(block.getBlockData() instanceof Ageable ageable)) {
			return;
		}


		if (ageable.getAge() < ageable.getMaximumAge()) return;

	

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) {
			return;
		}


		Block blockCentre = event.getBlock();

		BlockFace facing = player.getFacing();

		Block coteGauche;
		Block coteDroit;

		switch (facing) {

			case NORTH -> {
				coteGauche = blockCentre.getRelative(BlockFace.WEST);
				coteDroit = blockCentre.getRelative(BlockFace.EAST);
			}

			case SOUTH -> {
				coteGauche = blockCentre.getRelative(BlockFace.EAST);
				coteDroit = blockCentre.getRelative(BlockFace.WEST);
			}

			case EAST -> {
				coteGauche = blockCentre.getRelative(BlockFace.NORTH);
				coteDroit = blockCentre.getRelative(BlockFace.SOUTH);
			}

			case WEST -> {
				coteGauche = blockCentre.getRelative(BlockFace.SOUTH);
				coteDroit = blockCentre.getRelative(BlockFace.NORTH);
			}

			default -> {
				return;
			}
		}

		int rewardLevel = Outils.getRewardLevel(mainPrincipale);

		if (rewardLevel >= 100) {

			event.setCancelled(true);


			// Les 3 cultures d'un coup : une seule annonce (XP et jetons cumulés)
			int recoltes = 0;
			int jetonsTotal = 0;
			for (Block culture : new Block[] {blockCentre, coteGauche, coteDroit}) {
				int jetons = harvestCulture(culture, player, ps, mainPrincipale);
				if (jetons >= 0) {
					recoltes++;
					jetonsTotal += jetons;
				}
			}
			if (recoltes > 0) {
				ps.addJetonFarmeur(jetonsTotal);
				RecompenseUtil.donnerRecompenseFarmeur(player, ps, 10 * recoltes, jetonsTotal);
			}

			return;
		}


		int niveauHoue = Math.max(1, ps.getHoueLevel());
		int jeton = MultiplicateurUtil.tirerMultiplicateur(niveauHoue);
		ps.addJetonFarmeur(jeton);
		RecompenseUtil.donnerRecompenseFarmeur(player, ps, 10, jeton);

	}

	/** Récolte et replante une culture mûre ; renvoie les jetons tirés, ou -1 si rien n'est récolté. */
	private int harvestCulture(
			Block block,
			Player player,
			PlayerServer ps,
			ItemStack houe
	) {

		if (!(block.getBlockData() instanceof Ageable ageable)) {
			return -1;
		}

		if (ageable.getAge() < ageable.getMaximumAge()) {
			return -1;
		}


		for (ItemStack drop : block.getDrops(houe, player)) {
			player.getInventory().addItem(drop);
		}

		// Jetons tirés avec le niveau actuel (comme la houe normale) ;
		// XP et annonce faites une seule fois par onHarvest pour les 3 cultures.
		int niveauHoue = Math.max(1, ps.getHoueLevel());
		int jetons = MultiplicateurUtil.tirerMultiplicateur(niveauHoue);

		ageable.setAge(0);
		block.setBlockData(ageable);
		return jetons;
	}
}
