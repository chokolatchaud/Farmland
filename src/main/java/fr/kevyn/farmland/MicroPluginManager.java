package fr.kevyn.farmland;

import fr.kevyn.farmland.infrastructure.ModuleLoader;

/**
 * Façade de compatibilité pour les anciens appels au gestionnaire de modules.
 *
 * Le chargement normal passe désormais directement par {@link ModuleLoader}.
 * Cette classe est conservée pour les intégrations externes qui utiliseraient
 * encore les anciennes méthodes de chargement.
 */
@Deprecated
public final class MicroPluginManager {

    private MicroPluginManager() {
    }

    /**
     * Redirige l'ancien point d'entrée vers le chargeur de modules actuel.
     */
    public static void loadModules(FarmlandMain plugin) {
        ModuleLoader.load(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement du module de modération.
     */
    public static void moduleModeration(FarmlandMain plugin) {
        ModuleLoaderFacade.loadModeration(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement du module de plots.
     */
    public static void modulePlot(FarmlandMain plugin) {
        ModuleLoaderFacade.loadPlot(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement du module WorldEdit sécurisé.
     */
    public static void moduleSecureWorldEdit(FarmlandMain plugin) {
        ModuleLoaderFacade.loadSecureWorldEdit(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement du module de sauvegarde.
     */
    public static void moduleSaveCommand(FarmlandMain plugin) {
        ModuleLoaderFacade.loadSave(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement de PlaceholderAPI.
     */
    public static void modulePlaceholderAPI(FarmlandMain plugin) {
        ModuleLoaderFacade.loadPlaceholder(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement de la WebAPI.
     */
    public static void moduleWebApi(FarmlandMain plugin) {
        ModuleLoaderFacade.loadWebApi(plugin);
    }

    /**
     * Compatibilité avec l'ancien chargement du module métiers.
     */
    public static void modulemetier(FarmlandMain plugin) {
        ModuleLoaderFacade.loadMetier(plugin);
    }

    /**
     * Petites délégations internes pour éviter de dupliquer les imports
     * des modules historiques dans cette façade.
     */
    private static final class ModuleLoaderFacade {

        private ModuleLoaderFacade() {
        }

        private static void loadModeration(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.ModerationModule(plugin).register();
        }

        private static void loadPlot(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.PlotModule(plugin).register();
        }

        private static void loadSecureWorldEdit(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.SecureWorldEditModule(plugin).register();
        }

        private static void loadSave(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.SaveModule(plugin).register();
        }

        private static void loadPlaceholder(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.PlaceholderModule(plugin).register();
        }

        private static void loadWebApi(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.WebApiModule(plugin).register();
        }

        private static void loadMetier(FarmlandMain plugin) {
            new fr.kevyn.farmland.modules.MetierModule(plugin).register();
        }
    }
}
