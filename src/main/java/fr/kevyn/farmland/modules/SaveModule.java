package fr.kevyn.farmland.modules;

import java.io.IOException;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.Savecomands;
import fr.kevyn.farmland.save.PlayerSave;

/**
 * Commandes et sauvegarde périodique des joueurs.
 */
public final class SaveModule {

    private static final long SAVE_INTERVAL_TICKS = 6000L;

    private final FarmlandMain plugin;

    public SaveModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        try {
            Savecomands saveCommands = new Savecomands(plugin);
            plugin.getCommand("playerserver").setExecutor(saveCommands);
            plugin.getCommand("saveplayer").setExecutor(saveCommands);

            Bukkit.getScheduler().runTaskTimer(plugin, this::saveOnlinePlayers, SAVE_INTERVAL_TICKS, SAVE_INTERVAL_TICKS);

            messagediscord.sendmessage("Module SaveCommand bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module SaveCommand !");
            messagediscord.sendmessage("Module SaveCommand erreur: " + e, "statut");
            e.printStackTrace();
        }
    }

    private void saveOnlinePlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.isEmpty()) {
            plugin.getLogger().info("Sauvegarde non faite, aucun joueur connecté");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PlayerSave.saveAllPlayerServerFile(plugin);
            } catch (IOException e) {
                plugin.getLogger().severe("Erreur lors de la sauvegarde automatique des joueurs : " + e.getMessage());
                e.printStackTrace();
            }
        });

        plugin.getLogger().info("Sauvegarde lancée pour " + players.size() + " joueurs");
    }
}
