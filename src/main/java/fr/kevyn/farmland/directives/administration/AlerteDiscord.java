package fr.kevyn.farmland.directives.administration;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Évite de remplir la console avec le même problème Discord.
 * Un WARN la première fois, puis au plus un rappel par heure tant que le problème
 * se reproduit (avec le nombre d'occurrences masquées). Si les envois
 * réussissent, plus aucun échec n'arrive ici, donc plus aucun rappel.
 */
public final class AlerteDiscord {

    static final long INTERVALLE_RAPPEL_MS = 60L * 60 * 1000;

    private static final Map<String, Long> derniereAlerte = new HashMap<>();
    private static final Map<String, Integer> masquees = new HashMap<>();

    private AlerteDiscord() {
    }

    public static void signaler(JavaPlugin plugin, String cle, String message) {
        String texte = aEcrire(cle, message, System.currentTimeMillis());
        if (texte != null && plugin != null) {
            plugin.getLogger().warning(texte);
        }
    }

    /** Texte à écrire maintenant, ou null s'il faut se taire. Séparé pour les tests. */
    static synchronized String aEcrire(String cle, String message, long maintenant) {
        Long derniere = derniereAlerte.get(cle);
        if (derniere != null && maintenant - derniere < INTERVALLE_RAPPEL_MS) {
            masquees.merge(cle, 1, Integer::sum);
            return null;
        }
        int nb = masquees.getOrDefault(cle, 0);
        derniereAlerte.put(cle, maintenant);
        masquees.remove(cle);
        if (derniere == null) return message;
        return message + " (rappel : " + nb + " autre(s) occurrence(s) depuis la dernière alerte)";
    }

    static synchronized void reinitialiser() {
        derniereAlerte.clear();
        masquees.clear();
    }
}
