package fr.kevyn.farmland.EventBuild;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.market.BuyCommands;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.PlayerSave;
import fr.kevyn.plot.Plot;
import fr.kevyn.plot.PlotData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

/**
 * Gère l'entrée et la sortie des joueurs ainsi que l'initialisation de leur plot.
 */
public final class JoinAndleaveEvent implements Listener {

    private static final long PLOT_CREATION_DELAY = 40L;
    private static final long WORLD_RELOAD_DELAY = 20L;
    private static final long WORLD_BORDER_UPDATE_DELAY = WORLD_RELOAD_DELAY;
    private static final long WORLD_EDIT_RESTORE_DELAY = 5L;
    private final FarmlandMain plugin;

    public JoinAndleaveEvent(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        PlayerServer playerServer = PlayerserverHashMap.getInstance()
                .getplayerHaspMaps(event.getPlayer().getUniqueId());

        // ✅ Vérif ban
        if (playerServer != null && playerServer.getBan()) {
            event.getPlayer().kickPlayer("&cVous êtes banni définitivement !" +
                    "\nRaison : " + playerServer.getRaison());
            return;
        }

        if (playerServer == null) {
            // ===== NOUVEAU JOUEUR =====
            event.getPlayer().sendMessage(MessageColor.YELLOW.apply("Bienvenue Sur Farmland"));
            String messageBienvenue = MessageColor.LIGHT_PURPLE.apply("&eBienvenue à " + event.getPlayer().getName() + " Sur FarmLand !!!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(messageBienvenue);
            }

            PlotData plotData = new PlotData(
                event.getPlayer().getUniqueId().toString(),
                new ArrayList<>(), new ArrayList<>(),
                event.getPlayer().getUniqueId().toString(),
                50, 0, "minecraftActive", "day", "weatherclear"
            );
            
            PlayerServer newPlayerServer = new PlayerServer(
                event.getPlayer().getUniqueId(), event.getPlayer().getName(),
                false, false, "", 0, plotData, 0, "joueur", 0
            );

            if (newPlayerServer.getUuid() == null || newPlayerServer.getName() == null) {
                event.getPlayer().kickPlayer("Erreur de Sécurité, veuillez tenter une reconnexion.");
                plugin.getLogger().warning("Player " + event.getPlayer().getUniqueId() + " kick : SavePlayer incomplet.");
                messagediscord.sendmessage("Joueur kick pour cause newplayer... == null @everyone", "statut");
                return;
            }
            PlayerSave.saveOnePlayerServerFile(plugin, newPlayerServer);
            messagediscord.sendmessage("Nouveau joueur " + newPlayerServer.getName() + " a rejoint", "statut");

            ChatListener.updateTab(event.getPlayer());

            // ✅ Création du plot uniquement
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                new Plot(event.getPlayer().getUniqueId(), plugin);
            }, PLOT_CREATION_DELAY);

        } else {
            // ===== JOUEUR EXISTANT =====
            if (!event.getPlayer().getUniqueId().toString().equalsIgnoreCase(playerServer.getUuid().toString())) {
                event.getPlayer().kickPlayer("Erreur 23 Rapprocher vous d'un modérateur");
                messagediscord.sendmessage(event.getPlayer().getName() + " Erreur 23", "statut");
                return;
            }

            String playerName = event.getPlayer().getName();
            if (playerServer.getName() == null
                    || !playerServer.getName().equalsIgnoreCase(playerName)) {
                playerServer.setName(playerName);
                PlayerSave.saveOnePlayerServerFile(plugin, playerServer);
            }

            if (!playerServer.getLastjoin()) {
                playerServer.setLastjoin(true);
            }

            event.getPlayer().sendMessage(MessageColor.GRAY.apply("Données bien synchronisées"));

            messagediscord.sendmessage("[" + playerServer.getGrade() + "]: " + playerServer.getName() + " est revenu", "statut");
            LuckpermGrade.updateGrade(event.getPlayer());
            ChatListener.updateTab(event.getPlayer());

            // Restaurer les permissions WorldEdit si encore actif
            if (playerServer.isWeActive()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    BuyCommands.restoreAttachment(event.getPlayer(), playerServer, plugin);
                }, WORLD_EDIT_RESTORE_DELAY);
            }

            // ✅ Création du plot + application de la bordure après chargement
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                new Plot(event.getPlayer().getUniqueId(), plugin);
                // Appliquer la bordure après que le monde soit chargé
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(event.getPlayer().getUniqueId());
                    if (ps != null && ps.getPlotdata() != null) {
                        String worldName = ps.getPlotdata().getNameWorld();
                        org.bukkit.World plotWorld = Bukkit.getWorld(worldName);
                        if (plotWorld != null) {
                            plotWorld.getWorldBorder().setSize(ps.getPlotdata().getWorldborder());
                        }
                    }
                }, WORLD_BORDER_UPDATE_DELAY);
            }, PLOT_CREATION_DELAY);
        }

        // Pousse le statut du serveur vers farm-land.fr.
        if (plugin.getWebApi() != null) {
            plugin.getWebApi().pushServerStatus(
                plugin.getServer().getOnlinePlayers().size(),
                plugin.getServer().getMaxPlayers(),
                plugin.getServer().getBukkitVersion()
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerServer playerServer = PlayerserverHashMap.getInstance()
                .getplayerHaspMaps(event.getPlayer().getUniqueId());
        if (playerServer != null) {
            playerServer.getPlotdata().setAllplotadd(new ArrayList<String>());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                PlayerSave.saveOnePlayerServerFile(plugin, playerServer);
            });
        }
        // Nettoyer l'attachment WorldEdit à la déconnexion
        BuyCommands.removeAttachment(event.getPlayer().getUniqueId());
        
        // pousse le statut du serveur vers farm-land.fr
        if (plugin.getWebApi() != null) {
            plugin.getWebApi().pushServerStatus(
                plugin.getServer().getOnlinePlayers().size() - 1,
                plugin.getServer().getMaxPlayers(),
                plugin.getServer().getBukkitVersion()
            );
        }
        
        event.setQuitMessage("");
    }
}