package fr.kevyn.farmland.modules.coeur;

import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.administration.Savecomands;

/**
 * Enregistre les commandes liées aux sauvegardes des joueurs.
 *
 * La sauvegarde automatique est gérée par le module Game pour conserver
 * une seule planification périodique.
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
