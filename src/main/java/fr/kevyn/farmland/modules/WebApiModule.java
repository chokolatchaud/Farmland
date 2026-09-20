package fr.kevyn.farmland.modules;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;

/**
 * Initialise et planifie les mises à jour de la WebAPI.
 */
public final class WebApiModule {

    private final FarmlandMain plugin;

    public WebApiModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        if (!plugin.getConfig().getBoolean("webapi.enabled", false)) {
            plugin.getLogger().info("[WebAPI] Module désactivé");
            return;
        }

        try {
            String baseUrl = plugin.getConfig().getString("webapi.base_url", "");
            String apiKey = plugin.getConfig().getString("webapi.api_key", "");
            long intervalTicks =
                    plugin.getConfig().getLong("webapi.push_interval_seconds", 30L) * 20L;

            plugin.initWebApi(baseUrl, apiKey);
            plugin.getWebApi().pushVoteSites(
                    plugin.getConfig().getStringList("vote.sites"),
                    plugin.getConfig().getString("vote.reward", "World Edit 1 heure")
            );

            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                    () -> plugin.getWebApi().pushServerStatus(
                            plugin.getServer().getOnlinePlayers().size(),
                            plugin.getServer().getMaxPlayers(),
                            plugin.getServer().getBukkitVersion()
                    ),
                    20L,
                    intervalTicks);

            plugin.getLogger().info("[WebAPI] Module chargé → " + baseUrl);
            messagediscord.sendmessage("Module WebAPI bien lancé → " + baseUrl, "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("[WebAPI] Erreur lors du chargement du module WebAPI !");
            messagediscord.sendmessage("Module WebAPI erreur: " + e, "statut");
            e.printStackTrace();
        }
    }
}
