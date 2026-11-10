package fr.kevyn.farmland.modules.coeur;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Module du journal des événements (plugins/Farmland/evenements.log).
 * Réglages dans config.yml, section "journal".
 */
public class JournalModule implements Listener {

    private final FarmlandMain plugin;

    public JournalModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        JournalEvenements.demarrer(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /** Écrit les cumuls du joueur (ex. bonus de blocs) quand il se déconnecte. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        JournalEvenements.finJoueur(event.getPlayer().getName());
    }
}
