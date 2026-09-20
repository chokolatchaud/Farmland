package fr.kevyn.farmland;

import fr.kevyn.farmland.infrastructure.ModuleLoader;

public final class MicroPluginManager {

    private MicroPluginManager() {
    }

    public static void loadModules(FarmlandMain plugin) {
        ModuleLoader.load(plugin);
    }

    /**
     * Compatibilité avec les anciens appels.
     */
    public static void moduleModeration(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.ModerationModule(plugin).register();
    }

    public static void modulePlot(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.PlotModule(plugin).register();
    }

    public static void moduleSecureWorldEdit(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.SecureWorldEditModule(plugin).register();
    }

    public static void moduleSaveCommand(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.SaveModule(plugin).register();
    }

    public static void modulePlaceholderAPI(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.PlaceholderModule(plugin).register();
    }

    public static void moduleWebApi(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.WebApiModule(plugin).register();
    }

    public static void modulemetier(FarmlandMain plugin) {
        new fr.kevyn.farmland.modules.MetierModule(plugin).register();
    }
}
