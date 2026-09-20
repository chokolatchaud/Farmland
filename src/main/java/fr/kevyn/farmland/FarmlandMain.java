package fr.kevyn.farmland;

import org.bukkit.plugin.java.JavaPlugin;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.JoinAndleaveEvent;
import fr.kevyn.farmland.api.WebApiClient;
import fr.kevyn.farmland.infrastructure.ModuleLoader;
import fr.kevyn.farmland.save.PlayerSave;

public final class FarmlandMain extends JavaPlugin {

    private WebApiClient webApi;

    /**
     * Retourne le client WebAPI, lorsqu'il a été initialisé.
     */
    public WebApiClient getWebApi() {
        return webApi;
    }

    public void initWebApi(String baseUrl, String apiKey) {
        this.webApi = new WebApiClient(this, baseUrl, apiKey);
    }

    /**
     * Initialise le plugin dans un ordre déterministe :
     * configuration, services de base, données joueurs, puis modules.
     */
    @Override
    public void onEnable() {
        getLogger().info("----- Plugin activé -----");

        saveDefaultConfig();
        messagediscord.init(this);

        getServer().getPluginManager().registerEvents(new JoinAndleaveEvent(this), this);

        if (!loadPlayerData()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        messagediscord.sendmessage("Le plugin vient de s'allumer", "status");
        ModuleLoader.load(this);
    }

    /**
     * Charge les données joueurs avant tout module dépendant de PlayerServer.
     */
    private boolean loadPlayerData() {
        try {
            PlayerSave.verifyallPlayerSaves(this);
            PlayerSave.LoadPlayerserverFile(this);
            return true;
        } catch (Exception e) {
            getLogger().severe(
                    "ERREUR CRITIQUE lors du chargement des fichiers joueurs !"
            );
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDisable() {
        try {
            PlayerSave.verifyallPlayerSaves(this);
            PlayerSave.saveAllPlayerServerFile(this);

            getLogger().info("----- Plugin désactivé -----");
            messagediscord.sendmessage("Le plugin vient de s'éteindre", "status");
        } catch (Exception e) {
            getLogger().severe("Erreur lors de la sauvegarde finale !");
            e.printStackTrace();
        }
    }
}
