package fr.kevyn.farmland.modules.monde;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.plot.jaime.PlotJaime;

/**
 * « J'aime » sur les plots : /plot like, classement dans /plot visit
 * et hologramme « Top plots » (/classementadmin holo set plots).
 */
public final class PlotJaimeModule {

    private final FarmlandMain plugin;

    public PlotJaimeModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        PlotJaime.init(plugin);
        plugin.getLogger().info("[J'aime] Module J'aime des plots activé");
    }
}
