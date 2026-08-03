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

public class HoueFarmEvent implements Listener {
	Material[] seedlist = {Material.WHEAT_SEEDS,Material.CARROT,Material.POTATO,Material.PUMPKIN_SEEDS};
	
	
	
	
	@EventHandler
	public void onClicHoue(PlayerInteractEvent event) {
	    if (event.getHand() != EquipmentSlot.HAND) return; // eviter le double declenchement

	    Player player = event.getPlayer();
	    ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
	    ItemStack mainSecondaire = player.getInventory().getItemInOffHand();

	    if (!HoueFarmeur.isHoueFarmeur(mainPrincipale)) return;

	    // pas de vrai clic sur un bloc = rien a faire
	    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

	    Block blocClique = event.getClickedBlock();
	    if (blocClique.getType() != Material.FARMLAND) return; // faut de la terre labouree
	    
	    Material seed = mainSecondaire.getType();
	    Boolean isseed = false;
	    for(Material seedaccept : seedlist) {
	    	if((seed == seedaccept)) {
	    		isseed = true;
	    	}
	    
	    	
	    }
	    if (!isseed) {
	        player.sendMessage("§cTiens une graine dans ta main secondaire pour planter !");
	        return;
	    }else {
	    	 // plante la graine, un cran au-dessus du bloc clique (la terre labouree)
		    Block emplacementPlantation = blocClique.getRelative(BlockFace.UP);
		    emplacementPlantation.setType(seed);

		    // consomme 1 graine dans la main secondaire
		    mainSecondaire.setAmount(mainSecondaire.getAmount() - 1);

	    	
	    }
	    
	    event.setCancelled(true); // evite que Minecraft fasse sa propre action par-dessus
	}
	
	

}
