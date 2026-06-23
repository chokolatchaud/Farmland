package fr.kevyn.farmland;
import org.bukkit.plugin.java.JavaPlugin;
import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.JoinAndleaveEvent;
import fr.kevyn.farmland.api.WebApiClient;
import fr.kevyn.farmland.market.MarketCalc;
import fr.kevyn.farmland.save.Filesave;
import fr.kevyn.farmland.save.RegionSave;

public class FarmlandMain extends JavaPlugin {

    private WebApiClient webApi;

    public WebApiClient getWebApi() { return webApi; }

    @Override
    public void onEnable() {
    	//recuperation du projet par l'id 2
    	
        System.out.println("----- Plugin activé -----");
        messagediscord.init(this);
        getServer().getPluginManager().registerEvents(new JoinAndleaveEvent(this), this);
        
        try {
            Filesave.LoadPlayerserverFile(this);
            
        } catch (Exception e) {
            getLogger().severe("ERREUR CRITIQUE lors du chargement des fichiers joueurs !");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        try {
            RegionSave.loadAllRegions(this);
            MarketCalc.Calcforcoef(this);
        } catch (Exception e) {
            getLogger().severe("ERREUR CRITIQUE lors du chargement des regions !");
            e.printStackTrace();
        }

        try {
        } catch (Exception e) {
            getLogger().severe("ERREUR CRITIQUE lors du chargement des structures !");
            e.printStackTrace();
        }

        // WebAPI vers farm-land.fr
        saveDefaultConfig();
        if (getConfig().getBoolean("webapi.enabled", false)) {
            String base = getConfig().getString("webapi.base_url", "");
            String key  = getConfig().getString("webapi.api_key", "");
            webApi = new WebApiClient(this, base, key);
            long ticks = getConfig().getLong("webapi.push_interval_seconds", 30L) * 20L;
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                webApi.pushServerStatus(
                    getServer().getOnlinePlayers().size(),
                    getServer().getMaxPlayers(),
                    getServer().getBukkitVersion()
                );
            }, 20L, ticks);
            getLogger().info("[WebAPI] connecté à " + base);
        }

        messagediscord.sendmessage("Le plugin vient de s'allumer", "status");
        MicroPluginManager.loadModules(this);
    }

    @Override
    public void onDisable() {
        try {
            Filesave.SavePlayerserverFile(this);
            RegionSave.saveAllRegions(this);
            System.out.println("----- Plugin désactivé -----");
            messagediscord.sendmessage("Le plugin vient de s'éteindre", "status");
        } catch (Exception e) {
            getLogger().severe("Erreur lors de la sauvegarde finale !");
            e.printStackTrace();
        }
    }
}