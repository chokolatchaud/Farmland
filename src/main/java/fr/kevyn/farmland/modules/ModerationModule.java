package fr.kevyn.farmland.modules;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.LuckpermGrade;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.moderation.ModerationCommands;

/**
 * Enregistre les commandes et listeners de modération.
 */
public final class ModerationModule {

    private final FarmlandMain plugin;

    public ModerationModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        try {
            ModerationCommands moderationCommands = new ModerationCommands(plugin);

            plugin.getCommand("banf").setExecutor(moderationCommands);
            plugin.getCommand("kickf").setExecutor(moderationCommands);
            plugin.getCommand("warnf").setExecutor(moderationCommands);
            plugin.getCommand("unbanf").setExecutor(moderationCommands);

            plugin.getServer().getPluginManager().registerEvents(new LuckpermGrade(), plugin);

            messagediscord.sendmessage("Module Modération bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module Modération !");
            messagediscord.sendmessage("Module Modération erreur: " + e, "statut");
            e.printStackTrace();
        }
    }
}
