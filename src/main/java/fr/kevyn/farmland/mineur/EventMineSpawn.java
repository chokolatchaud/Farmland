package fr.kevyn.farmland.mineur;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.Farming.HoueFarmeur;
import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.Plot;

public class EventMineSpawn implements Listener {
	private static final Random random = new Random();
	// niveau 1 (a dupliquer/ajuster pour niveau 2, 3... plus tard)
    float chancetocoal = 0.20f;
    float chancetoiron = 0.10f;
    float chancetogold = 0.05f;
    float chancetodiamond = 0.025f;
    // le reste (0.825f dans ton cas) = rien de special, reste du cobble normal

    @EventHandler
    public void onCobbleForme(BlockFormEvent event) {
        if (event.getNewState().getType() != Material.COBBLESTONE) return;


        World monde = event.getBlock().getWorld();
        Plot plot = Plot.Worldtoplot(monde);
        if (plot == null) return;

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(plot.getUuid());
        if (ps == null) return;

        int levelcobble = ps.getCobblestonegeneratorlevel();

        Material resultat = tirerResultat(levelcobble);
        if (resultat != null) {
            event.getNewState().setType(convertirEnBlocMinerai(resultat));
        }
    }
    
    @EventHandler
	public void onMine(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return; // deja refuse par un autre handler (protection de plot, etc.)
		}

		Player player = event.getPlayer();
		ItemStack mainPrincipale = player.getInventory().getItemInMainHand();
		if (!Outils.isOutilsAttendu(mainPrincipale, Material.NETHERITE_PICKAXE)) {
			return;
		}

		Block block = event.getBlock();
		Material ressource = convertirMineraiEnBloc(block.getType());
		if (ressource == null) return;


		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) {
			return;
		}

		event.setDropItems(false); // jamais de vrai drop au sol, tout passe par le /bag
		int numberdone = 1;
		if(ps.getCobblestonegeneratorlevel() >= 10) {
			numberdone = 2;
		}
		if(ps.getCobblestonegeneratorlevel() >= 20) {
			numberdone = 3;
		}
		ps.addRessource(ressource, numberdone);
		player.sendMessage("§a+" + numberdone +" " + ressource.name() + " ajouté à ton /bag !");
	}
    @EventHandler
    public void onMinePlace(BlockPlaceEvent event) {
		Material type = event.getBlock().getType();

		if (isMineur(type)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cSeul un cobblegenerator peut generer ce minerai!");
		}
	}
    
    private boolean isMineur(Material type) {
		return switch (type) {
			case IRON_ORE, GOLD_ORE, DIAMOND_ORE, COAL_ORE -> true;
			default -> false;
		};
	}
    

    private Material tirerResultat(int level) {
        float tirage = random.nextFloat(); // un nombre entre 0.0 (inclus) et 1.0 (exclu)

        // on empile les tranches : diamant d'abord (le plus rare), puis or, fer, charbon
        if (tirage < chancetodiamond + level /10) {
            return Material.DIAMOND;
        }
        if (tirage < chancetodiamond + chancetogold /5) {
            return Material.GOLD_INGOT;
        }
        if (tirage < chancetodiamond + chancetogold + chancetoiron + level /3) {
            return Material.IRON_INGOT;
        }
        if (tirage < chancetodiamond + chancetogold + chancetoiron + chancetocoal + level /2) {
            return Material.COAL;
        }

        return null; // tombe dans les 82.5% restants : rien de special, reste du cobble
    }
    private Material convertirEnBlocMinerai(Material item) {
        return switch (item) {
            case COAL -> Material.COAL_ORE;
            case IRON_INGOT -> Material.IRON_ORE;
            case GOLD_INGOT -> Material.GOLD_ORE;
            case DIAMOND -> Material.DIAMOND_ORE;
            default -> Material.COBBLESTONE;
        };
    }
    
    private Material convertirMineraiEnBloc(Material item) {
        return switch (item) {
            case COAL_ORE -> Material.COAL;
            case IRON_ORE -> Material.IRON_INGOT;
            case GOLD_ORE -> Material.GOLD_INGOT;
            case DIAMOND_ORE -> Material.DIAMOND;
            default -> null;
        };
    }

}
