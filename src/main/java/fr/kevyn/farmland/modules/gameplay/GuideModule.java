package fr.kevyn.farmland.modules.gameplay;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.gameplay.guide.GuideAdminCommand;
import fr.kevyn.farmland.directives.gameplay.guide.GuideCommand;
import fr.kevyn.farmland.directives.gameplay.guide.GuideHolograms;
import fr.kevyn.farmland.directives.gameplay.guide.GuideLivre;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Guide des joueurs : menu personnalisé (/guide ou clic droit sur l'objet Guide),
 * hologrammes au hub (/guideadmin), et à la première connexion :
 * l'objet Guide + un court message dans le chat.
 */
public final class GuideModule implements Listener {

    private static final long DELAI_PREMIERE_CONNEXION = 60L; // 3 s : après le message de bienvenue
    private static final long MISE_A_JOUR_HOLOGRAMMES = 20L * 10;

    private final FarmlandMain plugin;

    public GuideModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getCommand("guide").setExecutor(new GuideCommand(plugin));
        plugin.getCommand("guideadmin").setExecutor(new GuideAdminCommand(plugin));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(
                new fr.kevyn.farmland.directives.gameplay.guide.GuideMenuListener(plugin), plugin);

        GuideHolograms.load(plugin);
        GuideHolograms.updateAll();
        Bukkit.getScheduler().runTaskTimer(plugin, GuideHolograms::updateAll,
                MISE_A_JOUR_HOLOGRAMMES, MISE_A_JOUR_HOLOGRAMMES);

        plugin.getLogger().info("[Guide] Module Guide activé");
    }

    @EventHandler
    public void onPremiereConnexion(PlayerJoinEvent event) {
        Player joueur = event.getPlayer();
        if (joueur.hasPlayedBefore()) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!joueur.isOnline()) return;
            GuideCommand.envoyerGuide(joueur);
            GuideLivre.donnerSiAbsent(joueur);
        }, DELAI_PREMIERE_CONNEXION);
    }
}
