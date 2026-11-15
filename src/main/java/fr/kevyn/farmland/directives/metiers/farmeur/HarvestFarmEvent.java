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
		event.setDropItems(false);

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

		if (rewardLevel == 100) {

			event.setCancelled(true);

			harvestCulture(blockCentre, player, ps, mainPrincipale);
			harvestCulture(coteGauche, player, ps, mainPrincipale);
			harvestCulture(coteDroit, player, ps, mainPrincipale);

			return;
		}


		int niveauHoue = Math.max(1, ps.getHoueLevel());
		int jeton = MultiplicateurUtil.tirerMultiplicateur(niveauHoue);
		ps.addJetonFarmeur(jeton);
		RecompenseUtil.donnerRecompenseFarmeur(player, ps, 10);

	}

	private void harvestCulture(
			Block block,
			Player player,
			PlayerServer ps,
			ItemStack houe
	) {

		if (!(block.getBlockData() instanceof Ageable ageable)) {
			return;
		}

		if (ageable.getAge() < ageable.getMaximumAge()) {
			return;
		}


		for (ItemStack drop : block.getDrops(houe, player)) {
			player.getInventory().addItem(drop);
		}


		RecompenseUtil.donnerRecompenseFarmeur(
				player,
				ps,
				10
		);

		int niveauHoue =
				Math.max(1, ps.getHoueLevel());

		int jetons =
				MultiplicateurUtil.tirerMultiplicateur(
						niveauHoue
				);

		ps.addJetonFarmeur(jetons);

		ageable.setAge(0);
		block.setBlockData(ageable);
	}
}
	

