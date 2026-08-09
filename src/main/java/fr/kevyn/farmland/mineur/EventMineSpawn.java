package fr.kevyn.farmland.mineur;

import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.Plot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

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
		if (!isMineur(block.getType())) return; // pas un minerai genere par notre Cobblegenerator

		PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
		if (ps == null) {
			return;
		}

		event.setDropItems(false); // jamais de vrai drop au sol, tout passe par le /bag
		int niveauPioche = Math.max(1, ps.getCobblestonegeneratorlevel());
		int jeton = fr.kevyn.farmland.menufarm.MultiplicateurUtil.tirerMultiplicateur(niveauPioche);
		fr.kevyn.farmland.menufarm.RecompenseUtil.donnerRecompenseMineur(player, ps, 10);
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
        int niveauBorne = Math.max(0, Math.min(10, level));
        float bonus = niveauBorne / 10.0f;

        float chanceDiamantReel = chancetodiamond + (bonus * 0.05f);
        float chanceOrReel = chancetogold + (bonus * 0.05f);
        float chanceFerReel = chancetoiron + (bonus * 0.05f);
        float chanceCharbonReel = chancetocoal + (bonus * 0.05f);

        float tirage = random.nextFloat();

        if (tirage < chanceDiamantReel) {
            return Material.DIAMOND;
        }
        if (tirage < chanceDiamantReel + chanceOrReel) {
            return Material.GOLD_INGOT;
        }
        if (tirage < chanceDiamantReel + chanceOrReel + chanceFerReel) {
            return Material.IRON_INGOT;
        }
        if (tirage < chanceDiamantReel + chanceOrReel + chanceFerReel + chanceCharbonReel) {
            return Material.COAL;
        }

        return null;
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

}
