package fr.kevyn.farmland.mineur;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFormEvent;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.Plot;

public class EventMineSpawn {

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
            ps.addRessource(resultat, 1);
            event.getNewState().setType(convertirEnBlocMinerai(resultat));

        }
    }

    private Material tirerResultat(int level) {
        float tirage = random.nextFloat(); // un nombre entre 0.0 (inclus) et 1.0 (exclu)

        // on empile les tranches : diamant d'abord (le plus rare), puis or, fer, charbon
        if (tirage < chancetodiamond) {
            return Material.DIAMOND;
        }
        if (tirage < chancetodiamond + chancetogold) {
            return Material.GOLD_INGOT;
        }
        if (tirage < chancetodiamond + chancetogold + chancetoiron) {
            return Material.IRON_INGOT;
        }
        if (tirage < chancetodiamond + chancetogold + chancetoiron + chancetocoal) {
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
}