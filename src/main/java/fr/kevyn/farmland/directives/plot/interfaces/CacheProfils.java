package fr.kevyn.farmland.directives.plot.interfaces;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Garde en mémoire le profil (avec le skin) des joueurs, pour les têtes du menu /plot visit.
 * Sans ce cache, chaque ouverture du menu redemandait les skins à Mojang.
 * - À la connexion, le profil complet est déjà connu du serveur : il est gardé sans demande à Mojang.
 * - Sinon, une seule demande en arrière-plan, gardée 6 h ; après un échec, pas de nouvelle demande avant 30 min.
 */
public final class CacheProfils implements Listener {

    static final long DUREE_VIE_MS = 6L * 60 * 60 * 1000;
    static final long ATTENTE_APRES_ECHEC_MS = 30L * 60 * 1000;

    private record Entree(PlayerProfile profil, long date) {}

    private static final Map<UUID, Entree> profils = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> echecs = new ConcurrentHashMap<>();
    private static final Set<UUID> enCours = ConcurrentHashMap.newKeySet();
    private static JavaPlugin plugin;

    public static void demarrer(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        for (Player p : Bukkit.getOnlinePlayers()) {
            memoriser(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        memoriser(event.getPlayer());
    }

    private static void memoriser(Player player) {
        PlayerProfile profil = player.getPlayerProfile();
        if (profil.hasTextures()) {
            profils.put(player.getUniqueId(), new Entree(profil, System.currentTimeMillis()));
            echecs.remove(player.getUniqueId());
        }
    }

    /**
     * Profil avec skin s'il est connu, sinon null (une demande part alors en arrière-plan si elle est permise).
     */
    public static PlayerProfile profilAvecSkin(UUID uuid, String nom) {
        if (uuid == null) return null;
        long maintenant = System.currentTimeMillis();

        Entree entree = profils.get(uuid);
        if (entree != null && maintenant - entree.date() < DUREE_VIE_MS) {
            return entree.profil();
        }

        Player enLigne = Bukkit.getPlayer(uuid);
        if (enLigne != null) {
            memoriser(enLigne);
            Entree fraiche = profils.get(uuid);
            if (fraiche != null) return fraiche.profil();
        }

        demanderEnArrierePlan(uuid, nom, maintenant);
        // Un profil périmé reste meilleur que rien en attendant la nouvelle demande
        return entree != null ? entree.profil() : null;
    }

    private static void demanderEnArrierePlan(UUID uuid, String nom, long maintenant) {
        if (plugin == null || !plugin.isEnabled()) return;
        if (!peutDemander(echecs.get(uuid), maintenant)) return;
        if (!enCours.add(uuid)) return; // une demande est déjà en cours pour ce joueur

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PlayerProfile profil = Bukkit.createProfile(uuid, nom);
                if (profil.complete(true) && profil.hasTextures()) {
                    profils.put(uuid, new Entree(profil, System.currentTimeMillis()));
                    echecs.remove(uuid);
                } else {
                    echecs.put(uuid, System.currentTimeMillis());
                }
            } catch (Exception e) {
                echecs.put(uuid, System.currentTimeMillis());
            } finally {
                enCours.remove(uuid);
            }
        });
    }

    /** Vrai s'il n'y a pas eu d'échec, ou si le dernier date d'au moins 30 min. */
    static boolean peutDemander(Long dernierEchec, long maintenant) {
        return dernierEchec == null || maintenant - dernierEchec >= ATTENTE_APRES_ECHEC_MS;
    }
}
