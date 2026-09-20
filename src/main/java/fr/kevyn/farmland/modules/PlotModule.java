package fr.kevyn.farmland.modules;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.chat.ChatCalcListener;
import fr.kevyn.farmland.EventBuild.EventBuildAndUse;
import fr.kevyn.farmland.EventBuild.Plotinventory;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.boathub.BoatRaceListener;
import fr.kevyn.farmland.boathub.DailyBoatReward;
import fr.kevyn.farmland.chat.AnnouncementBroadcaster;
import fr.kevyn.plot.Plotcommands;

/**
 * Enregistre les éléments liés aux plots et aux événements généraux de gameplay.
 */
public final class PlotModule {

    private static final long MINUTE_TICKS = 20L * 60;

    private final FarmlandMain plugin;

    public PlotModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        try {
            plugin.getServer().getPluginManager().registerEvents(new EventBuildAndUse(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new Plotinventory(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new BoatRaceListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new ChatCalcListener(), plugin);

            plugin.getServer().getScheduler().runTaskTimer(plugin,
                    () -> DailyBoatReward.checkAndRewardIfNewDay(plugin),
                    100L,
                    MINUTE_TICKS);

            plugin.getServer().getScheduler().runTaskTimer(plugin,
                    () -> ChatCalcListener.lancerNouveauCalcul(plugin),
                    20L * 60 * 5,
                    20L * 60 * 5);

            plugin.getServer().getScheduler().runTaskTimer(plugin,
                    () -> AnnouncementBroadcaster.broadcastRandom(plugin),
                    20L * 60 * 10,
                    20L * 60 * 10);

            plugin.getCommand("plot").setExecutor(new Plotcommands(plugin));

            messagediscord.sendmessage("Module Plot bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module Plot !");
            messagediscord.sendmessage("Module Plot erreur: " + e, "statut");
            e.printStackTrace();
        }
    }
}
