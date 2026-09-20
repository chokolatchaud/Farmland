package fr.kevyn.farmland.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.ChatListener;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.boathub.BoatRaceHologram;
import fr.kevyn.farmland.boathub.RaceAdminCommands;
import fr.kevyn.farmland.game.GameCommands;
import fr.kevyn.farmland.game.HubCommand;
import fr.kevyn.farmland.market.BuyCommands;
import fr.kevyn.farmland.market.MarketAdminCommands;
import fr.kevyn.farmland.market.MarketHolograms;
import fr.kevyn.farmland.market.MarketCommand;
import fr.kevyn.farmland.menufarm.BagCommands;
import fr.kevyn.farmland.menufarm.ClassementAdminCommands;
import fr.kevyn.farmland.menufarm.ClassementCommand;
import fr.kevyn.farmland.menufarm.LeaderboardHolograms;
import fr.kevyn.farmland.menufarm.MenuListenerFarm;
import fr.kevyn.farmland.playerserver.PlayerAdminCommands;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.PlayerSave;
import fr.kevyn.farmland.scoreboard.CreativePlotScoreboard;
import fr.kevyn.farmland.tpa.TpaCommand;
import fr.kevyn.farmland.vote.VoteCommand;
import fr.kevyn.farmland.vote.VoteListener;

/**
 * Enregistre les commandes, listeners et tâches du gameplay général.
 */
public final class GameModule {

    private static final long TAB_UPDATE_TICKS = 200L;
    private static final long SCOREBOARD_UPDATE_TICKS = 40L;
    private static final long BALANCE_PUSH_TICKS = 20L * 60;
    private static final long LEADERBOARD_UPDATE_TICKS = 20L * 60;
    private static final long MARKET_HOLOGRAM_UPDATE_TICKS = 20L * 10;
    private static final long BOAT_HOLOGRAM_UPDATE_TICKS = 20L * 60;
    private static final long AUTOSAVE_TICKS = 20L * 60 * 5;

    private final FarmlandMain plugin;

    public GameModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        try {
            registerCommands();
            registerMenus();
            registerMarket();
            registerVote();
            registerChatAndScoreboard();
            registerWebApiBalancePush();
            registerBoatRace();
            registerAutosave();

            plugin.getLogger().info("[Game] Module Game activé");
            messagediscord.sendmessage("Module Game bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module Game !");
            messagediscord.sendmessage("Module Game erreur: " + e, "statut");
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        GameCommands gameCommands = new GameCommands();
        plugin.getCommand("pay").setExecutor(gameCommands);
        plugin.getCommand("money").setExecutor(gameCommands);
        plugin.getCommand("msgf").setExecutor(gameCommands);
        plugin.getCommand("r").setExecutor(gameCommands);
        plugin.getCommand("reportmsg").setExecutor(gameCommands);

        plugin.getCommand("buy").setExecutor(new BuyCommands(plugin));

        HubCommand hubCommand = new HubCommand(plugin);
        plugin.getCommand("hub").setExecutor(hubCommand);
        plugin.getCommand("joinboat").setExecutor(hubCommand);

        plugin.getCommand("vote").setExecutor(new VoteCommand(plugin));
        plugin.getCommand("psadmin").setExecutor(new PlayerAdminCommands(plugin));
        plugin.getCommand("plotadmin").setExecutor(new fr.kevyn.plot.PlotAdminCommands(plugin));
        plugin.getCommand("bag").setExecutor(new BagCommands());
        plugin.getCommand("classement").setExecutor(new ClassementCommand());

        plugin.getCommand("marketadmin").setExecutor(new MarketAdminCommands(plugin));
        plugin.getCommand("market").setExecutor(new MarketCommand());
        plugin.getCommand("classementadmin").setExecutor(new ClassementAdminCommands(plugin));

        TpaCommand tpaCommand = new TpaCommand(plugin);
        plugin.getCommand("tpa").setExecutor(tpaCommand);
        plugin.getCommand("tpahere").setExecutor(tpaCommand);
        plugin.getCommand("tpaccept").setExecutor(tpaCommand);
        plugin.getCommand("tpdeny").setExecutor(tpaCommand);
    }

    private void registerMenus() {
        plugin.getServer().getPluginManager().registerEvents(new MenuListenerFarm(), plugin);
        LeaderboardHolograms.load(plugin);
    }

    private void registerMarket() {
        MarketHolograms.load(plugin);
        MarketHolograms.updateAll(plugin);

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> MarketHolograms.updateAll(plugin),
                MARKET_HOLOGRAM_UPDATE_TICKS,
                MARKET_HOLOGRAM_UPDATE_TICKS
        );
    }

    private void registerVote() {
        boolean votifierPresent =
                Bukkit.getPluginManager().getPlugin("Votifier") != null
                || Bukkit.getPluginManager().getPlugin("NuVotifier") != null;

        if (votifierPresent) {
            plugin.getServer().getPluginManager().registerEvents(new VoteListener(plugin), plugin);
            plugin.getLogger().info("[Vote] Module NuVotifier activé — WorldEdit 30min par vote");
        } else {
            plugin.getLogger().warning(
                    "[Vote] NuVotifier non trouvé — les votes ne donneront pas de récompense"
            );
        }
    }

    private void registerChatAndScoreboard() {
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ChatListener.updateTab(player);
                    }
                },
                0L,
                TAB_UPDATE_TICKS
        );

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        CreativePlotScoreboard.setscoreboardplot(player);
                    }
                },
                0L,
                SCOREBOARD_UPDATE_TICKS
        );

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> LeaderboardHolograms.updateAll(plugin),
                120L,
                LEADERBOARD_UPDATE_TICKS
        );
    }

    private void registerWebApiBalancePush() {
        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    if (!plugin.getConfig().getBoolean("webapi.enabled", false)
                            || plugin.getWebApi() == null) {
                        return;
                    }

                    for (PlayerServer playerServer
                            : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
                        if (playerServer == null) {
                            continue;
                        }

                        plugin.getWebApi().pushPlayerBalance(
                                playerServer.getName(),
                                playerServer.getMoney(),
                                playerServer.getBlocposetotal(),
                                playerServer.getCobblestonegeneratorlevel(),
                                playerServer.getHoueLevel(),
                                playerServer.getCanneLevel(),
                                playerServer.getHacheLevel(),
                                playerServer.getEpeeLevel()
                        );
                    }
                },
                100L,
                BALANCE_PUSH_TICKS
        );
    }

    private void registerBoatRace() {
        try {
            plugin.getCommand("raceadmin").setExecutor(new RaceAdminCommands(plugin));
            BoatRaceHologram.load(plugin);

            Bukkit.getScheduler().runTaskTimer(
                    plugin,
                    () -> BoatRaceHologram.update(plugin),
                    100L,
                    BOAT_HOLOGRAM_UPDATE_TICKS
            );
        } catch (Exception e) {
            plugin.getLogger().severe(
                    "[BoatRace] Erreur au chargement du module course de bateaux : "
                            + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    private void registerAutosave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                () -> {
                    try {
                        PlayerSave.saveAllPlayerServerFile(plugin);
                        plugin.getLogger().info("[Autosave] Joueurs sauvegardés");
                    } catch (Exception e) {
                        plugin.getLogger().severe(
                                "[Autosave] Erreur lors de la sauvegarde des joueurs : "
                                        + e.getMessage()
                        );
                        e.printStackTrace();
                    }
                },
                AUTOSAVE_TICKS,
                AUTOSAVE_TICKS
        );
    }
}
