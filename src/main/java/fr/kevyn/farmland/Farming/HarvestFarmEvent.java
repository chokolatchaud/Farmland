package fr.kevyn.farmland.Farming;

import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

		// verifie que la culture est bien arrivee a MATURITE (age maximal)
		if (ageable.getAge() < ageable.getMaximumAge()) return;

	

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) {
			return;
		}
		event.setDropItems(false); 

		int niveauHoue = Math.max(1, ps.getHoueLevel());
		int jeton = fr.kevyn.farmland.menufarm.MultiplicateurUtil.tirerMultiplicateur(niveauHoue);
		fr.kevyn.farmland.menufarm.RecompenseUtil.donnerRecompenseFarmeur(player, ps, 10);

	}
	
	
	
	
	
	@EventHandler
	public void onClicHoue(PlayerInteractEvent event) {
	    if (event.getHand() != EquipmentSlot.HAND) return; // eviter le double declenchement

	    Player player = event.getPlayer();
	    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
	    ItemStack mainPrincipale = player.getInventory().getItemInMainHand();

	    if (!Outils.isOutilsAttendu(mainPrincipale, Material.NETHERITE_HOE)) return;


	   
	    	
	    	
	    	
	    }

	    }
	    
	
	

