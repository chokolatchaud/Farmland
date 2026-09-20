package fr.kevyn.farmland.infrastructure;

import org.bukkit.Bukkit;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.modules.GameModule;
import fr.kevyn.farmland.modules.ModerationModule;
import fr.kevyn.farmland.modules.MetierModule;
import fr.kevyn.farmland.modules.PlotModule;
import fr.kevyn.farmland.modules.PlaceholderModule;
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
            loadModeration(plugin);
            loadPlot(plugin);
            loadPlaceholder(plugin);
            loadSave(plugin);
            loadGame(plugin);
            loadSecureWorldEdit(plugin);
            loadWebApi(plugin);
            loadMetier(plugin);
        }, LOAD_DELAY_TICKS);
    }

    private static void loadModeration(FarmlandMain plugin) {
        new ModerationModule(plugin).register();
    }

    private static void loadPlot(FarmlandMain plugin) {
        new PlotModule(plugin).register();
    }

    private static void loadPlaceholder(FarmlandMain plugin) {
        new PlaceholderModule(plugin).register();
    }

    private static void loadSave(FarmlandMain plugin) {
        new SaveModule(plugin).register();
    }

    private static void loadGame(FarmlandMain plugin) {
        new GameModule(plugin).register();
    }

    private static void loadSecureWorldEdit(FarmlandMain plugin) {
        new SecureWorldEditModule(plugin).register();
    }

    private static void loadWebApi(FarmlandMain plugin) {
        new WebApiModule(plugin).register();
    }

    private static void loadMetier(FarmlandMain plugin) {
        new MetierModule(plugin).register();
    }
}
