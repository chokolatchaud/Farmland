package fr.kevyn.farmland.modules;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.Savecomands;

/**
 * Commandes et sauvegarde périodique des joueurs.
 */
public final class SaveModule {


    private final FarmlandMain plugin;

    public SaveModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        try {
            Savecomands saveCommands = new Savecomands(plugin);
            plugin.getCommand("playerserver").setExecutor(saveCommands);
            plugin.getCommand("saveplayer").setExecutor(saveCommands);

            messagediscord.sendmessage("Module SaveCommand bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module SaveCommand !");
            messagediscord.sendmessage("Module SaveCommand erreur: " + e, "statut");
            e.printStackTrace();
        }
    }

}
