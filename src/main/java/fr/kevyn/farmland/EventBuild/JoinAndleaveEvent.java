package fr.kevyn.farmland.EventBuild;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.Filesave;
import fr.kevyn.plot.Plot;
import fr.kevyn.plot.PlotData;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class JoinAndleaveEvent implements Listener {

    private static final long PLOT_CREATION_DELAY = 40L;
    private FarmlandMain plugin;

    public JoinAndleaveEvent(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(e.getPlayer().getUniqueId());

        // ✅ Vérif ban
        if (playerServer != null && playerServer.getBan()) {
            e.getPlayer().kickPlayer("&cVous êtes banni définitivement !" +
                    "\nRaison : " + playerServer.getRaison());
            return;
        }

        if (playerServer == null) {
            // ===== NOUVEAU JOUEUR =====
            e.getPlayer().sendMessage(MessageColor.YELLOW.apply("Bienvenue Sur Farmland"));
            String messageBienvenue = MessageColor.LIGHT_PURPLE.apply("&eBienvenue à " + e.getPlayer().getName() + " Sur FarmLand !!!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(messageBienvenue);
            }

            PlotData plotData = new PlotData(
                e.getPlayer().getUniqueId().toString(),
                new ArrayList<>(), new ArrayList<>(),
                e.getPlayer().getUniqueId().toString(),
                50, 0, "minecraftActive", "day", "weatherclear"
            );
            
            PlayerServer newPlayerServer = new PlayerServer(
                e.getPlayer().getUniqueId(), e.getPlayer().getName(),
                false, false, "", 0, plotData, 0, "joueur", 0
            );

            if (newPlayerServer.getUuid() == null || newPlayerServer.getName() == null) {
                e.getPlayer().kickPlayer("Erreur de Sécurité, veuillez tenter une reconnexion.");
                plugin.getLogger().warning("Player " + e.getPlayer().getUniqueId() + " kick : SavePlayer incomplet.");
                messagediscord.sendmessage("Joueur kick pour cause newplayer... == null @everyone", "statut");
                return;
            }
            Filesave.saveOnePlayerServerFile(plugin, newPlayerServer);
            messagediscord.sendmessage("Nouveau joueur " + newPlayerServer.getName() + " a rejoint", "statut");

            ChatListener.updateTab(e.getPlayer());

            // ✅ Création du plot uniquement
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                new Plot(e.getPlayer().getUniqueId(), plugin);
            }, PLOT_CREATION_DELAY);

        } else {
            // ===== JOUEUR EXISTANT =====
            if (!e.getPlayer().getUniqueId().toString().equalsIgnoreCase(playerServer.getUuid().toString())) {
                e.getPlayer().kickPlayer("Erreur 23 Rapprocher vous d'un modérateur");
                messagediscord.sendmessage(e.getPlayer().getName() + " Erreur 23", "statut");
                return;
            }

            if (!playerServer.getName().equalsIgnoreCase(e.getPlayer().getName())) {
                playerServer.setName(e.getPlayer().getName());
            }

            if (!playerServer.getLastjoin()) {
                playerServer.setLastjoin(true);
            }

            e.getPlayer().sendMessage(MessageColor.GRAY.apply("Données bien synchronisées"));

            // Gains des structures pendant l'absence (penalite hors ligne de 80% deja appliquee)
            if (playerServer.getMoneyoffline() > 0) {
                e.getPlayer().sendMessage(MessageColor.GOLD.apply("✦ Tes structures ont rapporté +" + playerServer.getMoneyoffline() + " $FB pendant ton absence ! §7(revenus hors ligne réduits de 80%)"));
                playerServer.setMoneyoffline(0);
            }
            messagediscord.sendmessage("[" + playerServer.getGrade() + "]: " + playerServer.getName() + " est revenu", "statut");
            LuckpermGrade.updateGrade(e.getPlayer());
            ChatListener.updateTab(e.getPlayer());

            // Restaurer les permissions WorldEdit si encore actif
            if (playerServer.isWeActive()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fr.kevyn.farmland.market.BuyCommands.restoreAttachment(e.getPlayer(), playerServer, plugin);
                }, 5L);
            }

            // ✅ Création du plot + application de la bordure après chargement
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                new Plot(e.getPlayer().getUniqueId(), plugin);
                // Appliquer la bordure après que le monde soit chargé
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(e.getPlayer().getUniqueId());
                    if (ps != null && ps.getPlotdata() != null) {
                        String worldName = ps.getPlotdata().getNameWorld();
                        org.bukkit.World plotWorld = Bukkit.getWorld(worldName);
                        if (plotWorld != null) {
                            plotWorld.getWorldBorder().setSize(ps.getPlotdata().getWorldborder());
                        }
                    }
                }, 20L);
            }, PLOT_CREATION_DELAY);
        }

        // pousse le statut du serveur vers farm-land.fr
        if (plugin.getWebApi() != null) {
            plugin.getWebApi().pushServerStatus(
                plugin.getServer().getOnlinePlayers().size(),
                plugin.getServer().getMaxPlayers(),
                plugin.getServer().getBukkitVersion()
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(e.getPlayer().getUniqueId());
        if (playerServer != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Filesave.saveOnePlayerServerFile(plugin, playerServer);
            });
        }
        // Nettoyer l'attachment WorldEdit à la déconnexion
        fr.kevyn.farmland.market.BuyCommands.removeAttachment(e.getPlayer().getUniqueId());

        playerServer.getPlotdata().setAllplotadd(new ArrayList<String>());
        
        // pousse le statut du serveur vers farm-land.fr
        if (plugin.getWebApi() != null) {
            plugin.getWebApi().pushServerStatus(
                plugin.getServer().getOnlinePlayers().size() - 1,
                plugin.getServer().getMaxPlayers(),
                plugin.getServer().getBukkitVersion()
            );
        }
        
        e.setQuitMessage("");
    }
}