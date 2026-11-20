package fr.kevyn.farmland.modules.gameplay;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.cosmetic.titres.Titres;
import fr.kevyn.farmland.directives.plot.une.PlotUne;
import org.bukkit.Bukkit;

/**
 * Boutique : titres du chat (titres.json) et enchères du plot à la une (plot_une.json).
 */
public final class BoutiqueModule {

    private static final long VERIF_SEMAINE_TICKS = 20L * 60 * 10; // 10 minutes

    private final FarmlandMain plugin;

    public BoutiqueModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Titres.init(plugin);
        PlotUne.init(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, PlotUne::verifierSemaine, VERIF_SEMAINE_TICKS, VERIF_SEMAINE_TICKS);
        plugin.getLogger().info("[Boutique] Module Boutique activé (titres, plot à la une)");
    }
}
