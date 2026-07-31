package fr.kevyn.farmland.afk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;


public class AfkManager {

    private static final long AFK_THRESHOLD_MS = 20L * 60 * 1000; // 20 minutes

    private static final Map<UUID, Long> lastActivity = new HashMap<>();

    /** A appeler a chaque mouvement reel (X/Z), connexion, ou action du joueur */
    public static void updateActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /** Vrai si le joueur n'a montre aucune activite depuis plus de 20 minutes */
    public static boolean isAfk(Player player) {
        Long last = lastActivity.get(player.getUniqueId());
        if (last == null) return false; // pas encore de donnee = pas afk par defaut
        return (System.currentTimeMillis() - last) >= AFK_THRESHOLD_MS;
    }

    /** Nettoyage a la deconnexion (evite une fuite memoire sur le long terme) */
    public static void clear(Player player) {
        lastActivity.remove(player.getUniqueId());
    }
}
