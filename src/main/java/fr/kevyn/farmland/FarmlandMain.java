package fr.kevyn.farmland;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.JoinAndleaveEvent;
import fr.kevyn.farmland.api.WebApiClient;
import fr.kevyn.farmland.save.PlayerSave;
import org.bukkit.plugin.java.JavaPlugin;


public class FarmlandMain extends JavaPlugin {

    private WebApiClient webApi;

    public WebApiClient getWebApi() { return webApi; }

    // appelé par moduleWebApi dans MicroPluginManager
    public void initWebApi(String baseUrl, String apiKey) {
        this.webApi = new WebApiClient(this, baseUrl, apiKey);
    }

    @Override
    public void onEnable() {
        System.out.println("----- Plugin activé -----");
        saveDefaultConfig();
        messagediscord.init(this);
        getServer().getPluginManager().registerEvents(new JoinAndleaveEvent(this), this);

        try {
            PlayerSave.verifyallPlayerSaves(this);
            PlayerSave.LoadPlayerserverFile(this);
        } catch (Exception e) {
            getLogger().severe("ERREUR CRITIQUE lors du chargement des fichiers joueurs !");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
        } catch (Exception e) {
            getLogger().severe("ERREUR CRITIQUE lors du chargement des regions !");
            e.printStackTrace();
        }

        try {
        } catch (Exception e) {
            getLogger().severe("ERREUR CRITIQUE lors du chargement des structures !");
            e.printStackTrace();
        }

        messagediscord.sendmessage("Le plugin vient de s'allumer", "status");
        MicroPluginManager.loadModules(this);
    }

    @Override
    public void onDisable() {
        try {
            PlayerSave.verifyallPlayerSaves(this);
            PlayerSave.saveAllPlayerServerFile(this);
            System.out.println("----- Plugin désactivé -----");
            messagediscord.sendmessage("Le plugin vient de s'éteindre", "status");
        } catch (Exception e) {
            getLogger().severe("Erreur lors de la sauvegarde finale !");
            e.printStackTrace();
        }
    }
}
