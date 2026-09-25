package fr.kevyn.farmland.modules.monde;

import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.directives.plot.evenement.EventBuildAndUse;
import fr.kevyn.farmland.directives.gameplay.chat.ChatCalcListener;
import fr.kevyn.farmland.directives.plot.evenement.Plotinventory;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.bateau.course.BoatRaceListener;
import fr.kevyn.farmland.directives.bateau.recompense.DailyBoatReward;
import fr.kevyn.farmland.directives.gameplay.chat.AnnouncementBroadcaster;
import fr.kevyn.farmland.directives.plot.commands.Plotcommands;

/**
 * Enregistre les éléments liés aux plots et aux événements généraux de gameplay.
 */
public final class PlotModule {

    private static final long MINUTE_TICKS = 20L * 60;
    private static final long CHAT_CALC_TICKS = 20L * 60 * 5;
    private static final long ANNOUNCEMENT_TICKS = 20L * 60 * 10;
    private static final long BOAT_REWARD_INITIAL_DELAY = 100L;

    private final FarmlandMain plugin;

    public PlotModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        try {
            registerListeners();
            registerTasks();
            registerCommands();

            messagediscord.sendmessage("Module Plot bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module Plot !");
            messagediscord.sendmessage("Module Plot erreur: " + e, "statut");
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new EventBuildAndUse(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Plotinventory(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BoatRaceListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChatCalcListener(), plugin);
    }

    private void registerTasks() {
        plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> DailyBoatReward.checkAndRewardIfNewDay(plugin),
                BOAT_REWARD_INITIAL_DELAY,
                MINUTE_TICKS);

        plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> ChatCalcListener.lancerNouveauCalcul(plugin),
                CHAT_CALC_TICKS,
                CHAT_CALC_TICKS);

        plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> AnnouncementBroadcaster.broadcastRandom(plugin),
                ANNOUNCEMENT_TICKS,
                ANNOUNCEMENT_TICKS);
    }

    private void registerCommands() {
        plugin.getCommand("plot").setExecutor(new Plotcommands(plugin));
    }
}
