package fr.kevyn.farmland;

import fr.kevyn.farmland.directives.administration.messagediscord;
import java.util.logging.Level;
import fr.kevyn.farmland.directives.gameplay.evenement.JoinAndleaveEvent;
import fr.kevyn.farmland.infrastructure.ModuleLoader;
import fr.kevyn.farmland.persistance.joueurs.PlayerSave;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmlandMain extends JavaPlugin {







    @Override
    public void onEnable() {
        getLogger().info("----- Plugin activé -----");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new JoinAndleaveEvent(this), this);
        messagediscord.init(this);
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
            getLogger().log(Level.SEVERE,
                    "ERREUR CRITIQUE lors du chargement des fichiers joueurs !", e);
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
            getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde finale !", e);
        }
    }
}
