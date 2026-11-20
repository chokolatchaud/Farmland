package fr.kevyn.farmland.modules.gameplay;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.gameplay.missions.Missions;
import fr.kevyn.farmland.directives.gameplay.missions.MissionsCalc;
import fr.kevyn.farmland.directives.gameplay.missions.ProgressionJour;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Missions du jour : 3 missions par joueur et par jour, visibles dans le guide
 * (/guide missions). Rappel à la connexion tant qu'elles ne sont pas toutes faites.
 */
public final class MissionsModule implements Listener {

    private static final long SAUVEGARDE_TICKS = 20L * 120; // 2 minutes
    private static final long DELAI_RAPPEL = 20L * 5;

    private final FarmlandMain plugin;

    public MissionsModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Missions.init(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, Missions::sauvegarderSiModifie, SAUVEGARDE_TICKS, SAUVEGARDE_TICKS);
        plugin.getLogger().info("[Missions] Module Missions du jour activé (jusqu'à "
                + Missions.getRecompenses().totalMaximum() + " $FB par jour)");
    }

    @EventHandler
    public void onConnexion(PlayerJoinEvent event) {
        Player joueur = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!joueur.isOnline()) return;
            ProgressionJour p = Missions.progressionDuJour(joueur.getUniqueId());
            int faites = p.nombreFaites();
            if (faites >= MissionsCalc.NB_MISSIONS) return;
            joueur.sendMessage(Component.text("✦ Missions du jour : ", NamedTextColor.YELLOW)
                    .append(Component.text(faites + "/" + MissionsCalc.NB_MISSIONS + " faites", NamedTextColor.WHITE))
                    .append(Component.text(" — clique ici pour les voir", NamedTextColor.GRAY))
                    .clickEvent(ClickEvent.runCommand("/guide missions"))
                    .hoverEvent(HoverEvent.showText(Component.text("Ouvrir les missions du jour", NamedTextColor.YELLOW))));
        }, DELAI_RAPPEL);
    }
}
