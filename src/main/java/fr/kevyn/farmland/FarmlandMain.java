package fr.kevyn.farmland;
import org.bukkit.plugin.java.JavaPlugin;
import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.JoinAndleaveEvent;
import fr.kevyn.farmland.market.MarketCalc;
import fr.kevyn.farmland.save.Filesave;
import fr.kevyn.farmland.save.RegionSave;

public class FarmlandMain extends JavaPlugin {
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