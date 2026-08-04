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

import fr.kevyn.farmland.menufarm.MenuFarm;
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

	    if (!HoueFarmeur.isHoueFarmeur(mainPrincipale)) return;

	    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
	    	player.openInventory(MenuFarm.createmenuSeeds(ps));
	    	return;
	    }
	    
	    

	    Block blocClique = event.getClickedBlock();
	    if (blocClique.getType() == Material.FARMLAND) {
	    	Material seedplanteur = HoueFarmeur.getGraineSelectionnee(mainPrincipale);
	        if (seedplanteur == null) {
	            player.sendMessage("§cSélectionne d'abord une graine (clic droit dans le vide) !");
	            return;
	        }

	        if (ps.getRessource(seedplanteur) <= 0) {
	            player.sendMessage("§cTu n'as plus de graines de ce type ! Achete-en via le menu.");
	            return;
	        }
	        
	        Material blocAPoser = itemVersBlocCulture(seedplanteur);
	        if (blocAPoser == null) return;
	        Block emplacementPlantation = blocClique.getRelative(BlockFace.UP);
	        if (emplacementPlantation.getType() != Material.AIR) return;
	        
	        emplacementPlantation.setType(blocAPoser);
	        ps.RemoveRessource(seedplanteur, 1);
	        
	        event.setCancelled(true);
	    	
	    	
	    	
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
