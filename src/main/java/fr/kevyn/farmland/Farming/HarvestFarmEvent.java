package fr.kevyn.farmland.Farming;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menufarm.MenuFarm;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;



public class HarvestFarmEvent implements Listener {
	Material[] seedlist = {Material.WHEAT_SEEDS,Material.CARROT,Material.POTATO,Material.PUMPKIN_SEEDS};

	@EventHandler
	public void onHarvest(BlockBreakEvent event) {
		if (event.isCancelled()) {
			System.out.println("event annulé");
			
			return; // deja refuse par un autre handler (protection de plot, etc.)
		}

		Player player = event.getPlayer();
		ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
		if (!HoueFarmeur.isHoueFarmeur(mainPrincipale)) {
			System.out.println("la houe pas dans la bonne main");
			return;
		}

		Block block = event.getBlock();
		if (!(block.getBlockData() instanceof Ageable ageable)) {
			System.out.println("ageable null");
			return;
		}

		// verifie que la culture est bien arrivee a MATURITE (age maximal)
		if (ageable.getAge() < ageable.getMaximumAge()) return;

		Material ressource = blocCultureVersRessource(block.getType());
		if (ressource == null) return;


		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) {
			System.out.println("player server null");
			return;
		}

		event.setDropItems(false); // jamais de vrai drop au sol, tout passe par le /bag

		ps.addRessource(ressource, 1);
		player.sendMessage("§a+1 " + ressource.name() + " ajouté à ton /bag !");
	}
	
	
	
	@EventHandler
	public void onPlacePlant(BlockPlaceEvent event) {
		Material type = event.getBlock().getType();

		if (isCulture(type)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cSeul le Planteur peut faire pousser ces cultures !");
		}
	}
	
	@EventHandler
	public void onClicHoue(PlayerInteractEvent event) {
	    if (event.getHand() != EquipmentSlot.HAND) return; // eviter le double declenchement

	    Player player = event.getPlayer();
	    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
	    ItemStack mainPrincipale = player.getInventory().getItemInMainHand();

	    if (!HoueFarmeur.isHoueFarmeur(mainPrincipale)) return;
	    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
	        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
	            player.openInventory(MenuFarm.createmenuSeeds(ps));
	        }
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
	        case CARROT -> Material.CARROTS;
	        case POTATO -> Material.POTATOES;
	        case PUMPKIN_STEM -> Material.PUMPKIN_STEM;
	        default -> null;
	    };
	}
	
	
	
	
	

	private boolean isCulture(Material type) {
		return switch (type) {
			case WHEAT, CARROTS, POTATOES, PUMPKIN -> true;
			default -> false;
		};
	}

	private Material blocCultureVersRessource(Material blocCulture) {
		return switch (blocCulture) {
			case WHEAT -> Material.WHEAT;
			case CARROTS -> Material.CARROTS;
			case POTATOES -> Material.POTATOES;
			case PUMPKIN_STEM-> Material.PUMPKIN;
			default -> null;
		};
	}
}
