package fr.kevyn.farmland.Farming;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class HoueFarmEvent implements Listener {
	Material[] seedlist = {Material.WHEAT_SEEDS,Material.CARROT,Material.POTATO,Material.PUMPKIN_SEEDS};
	
	
	
	
	@EventHandler
	public void onClicHoue(PlayerInteractEvent event) {
	    if (event.getHand() != EquipmentSlot.HAND) return; // eviter le double declenchement

	    Player player = event.getPlayer();
	    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
	    ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
	    ItemStack mainSecondaire = player.getInventory().getItemInOffHand();

	    if (!HoueFarmeur.isHoueFarmeur(mainPrincipale)) return;

	    // pas de vrai clic sur un bloc = rien a faire
	    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
	    	player.openInventory(MenuFarm.createmenuSeeds(ps));
	    }

	    Block blocClique = event.getClickedBlock();
	    if (blocClique.getType() == Material.FARMLAND) return; // faut de la terre labouree
	    
	    for(Material seedaccept : seedlist) {
	    	if((seed == seedaccept)) {
	    		isseed = true;
	    	}
	    
	    	
	    }
	   

	    	
	    }
	    
	
	private Material itemVersBlocCulture(Material itemGraine) {
	    return switch (itemGraine) {
	        case WHEAT_SEEDS -> Material.WHEAT;
	        case CARROT -> Material.CARROT;
	        case POTATO -> Material.POTATO;
	        case PUMPKIN_SEEDS -> Material.PUMPKIN_STEM;
	        default -> null;
	    };
	}
	
	

}
