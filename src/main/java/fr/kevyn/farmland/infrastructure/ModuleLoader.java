package fr.kevyn.farmland.infrastructure;

import org.bukkit.Bukkit;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.modules.ModerationModule;
import fr.kevyn.farmland.modules.MetierModule;
import fr.kevyn.farmland.modules.PlotModule;
import fr.kevyn.farmland.modules.SaveModule;
import fr.kevyn.farmland.modules.SecureWorldEditModule;
import fr.kevyn.farmland.modules.WebApiModule;

/**
 * Centralise le chargement des modules du plugin.
 */
public final class ModuleLoader {

    private static final long LOAD_DELAY_TICKS = 20L;

    private ModuleLoader() {
    }

    public static void load(FarmlandMain plugin) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            new ModerationModule(plugin).register();
            new PlotModule(plugin).register();
            new SaveModule(plugin).register();
            new SecureWorldEditModule(plugin).register();
            new WebApiModule(plugin).register();
            new MetierModule(plugin).register();
        }, LOAD_DELAY_TICKS);
    }
}
