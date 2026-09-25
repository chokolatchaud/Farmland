package fr.kevyn.farmland.infrastructure;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.modules.coeur.PlaceholderModule;
import fr.kevyn.farmland.modules.coeur.SaveModule;
import fr.kevyn.farmland.modules.coeur.WebApiModule;
import fr.kevyn.farmland.modules.gameplay.MetierModule;
import fr.kevyn.farmland.modules.moderation.ModerationModule;
import fr.kevyn.farmland.modules.monde.PlotModule;
import fr.kevyn.farmland.modules.monde.SecureWorldEditModule;

@Deprecated
public final class MicroPluginManager {

    private MicroPluginManager() {
    }

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
            new ModerationModule(plugin).register();
        }

        private static void loadPlot(FarmlandMain plugin) {
            new PlotModule(plugin).register();
        }

        private static void loadSecureWorldEdit(FarmlandMain plugin) {
            new SecureWorldEditModule(plugin).register();
        }

        private static void loadSave(FarmlandMain plugin) {
            new SaveModule(plugin).register();
        }

        private static void loadPlaceholder(FarmlandMain plugin) {
            new PlaceholderModule(plugin).register();
        }

        private static void loadWebApi(FarmlandMain plugin) {
            new WebApiModule(plugin).register();
        }

        private static void loadMetier(FarmlandMain plugin) {
            new MetierModule(plugin).register();
        }
    }
}
