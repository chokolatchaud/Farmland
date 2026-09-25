package fr.kevyn.farmland.infrastructure;

import org.bukkit.Bukkit;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.modules.gameplay.GameModule;
import fr.kevyn.farmland.modules.moderation.ModerationModule;
import fr.kevyn.farmland.modules.gameplay.MetierModule;
import fr.kevyn.farmland.modules.monde.PlotModule;
import fr.kevyn.farmland.modules.coeur.PlaceholderModule;
import fr.kevyn.farmland.modules.coeur.SaveModule;
import fr.kevyn.farmland.modules.monde.SecureWorldEditModule;
import fr.kevyn.farmland.modules.coeur.WebApiModule;

/**
 * Centralise le chargement des modules du plugin.
 */
public final class ModuleLoader {

    /**
     * Les modules sont chargés une seconde après l'activation de base.
     * Les données joueurs sont déjà disponibles à ce moment-là.
     */
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
