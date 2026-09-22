package fr.kevyn.farmland.modules.coeur;

import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.infrastructure.PlaceholderApiplayerserver;

/**
 * Enregistre PlaceholderAPI lorsque le plugin est disponible.
 */
public final class PlaceholderModule {

    private final FarmlandMain plugin;

    public PlaceholderModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().warning(
                    "PlaceholderAPI non trouvé ! Les placeholders joueurs ne fonctionneront pas."
            );
            return;
        }

        try {
            new PlaceholderApiplayerserver().register();
            plugin.getLogger().info("PlayerPlaceholderAPI registered successfully!");
            messagediscord.sendmessage("Module PlaceholderAPI bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().warning(
                    "Impossible de charger PlaceholderAPI : " + e.getMessage()
            );
        }
    }
}
