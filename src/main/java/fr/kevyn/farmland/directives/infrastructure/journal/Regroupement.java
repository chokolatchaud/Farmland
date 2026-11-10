package fr.kevyn.farmland.directives.infrastructure.journal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Additionne les événements fréquents (par type et par joueur) pour écrire
 * une seule ligne, par exemple "+75 $FB (450 blocs)" au lieu de 3 lignes.
 * Thread-safe : appelé depuis le jeu, vidé depuis la tâche d'écriture.
 */
public final class Regroupement {

    /** Une ligne regroupée, prête à être formatée. */
    public record Ligne(String type, String joueur, String valeur) {
    }

    private static final class Cumul {
        long montant;
        long quantite;
        String unite;
    }

    private final Map<String, Cumul> cumuls = new LinkedHashMap<>();

    public synchronized void ajouter(String type, String joueur, int montant, int quantite, String unite) {
        String cle = cle(type, joueur);
        Cumul c = cumuls.computeIfAbsent(cle, k -> new Cumul());
        c.montant += montant;
        c.quantite += quantite;
        c.unite = unite;
    }

    /** Toutes les lignes cumulées, puis remise à zéro. */
    public synchronized List<Ligne> vider() {
        List<Ligne> lignes = new ArrayList<>();
        for (Map.Entry<String, Cumul> e : cumuls.entrySet()) {
            lignes.add(versLigne(e.getKey(), e.getValue()));
        }
        cumuls.clear();
        return lignes;
    }

    /** Les lignes d'un seul joueur (à sa déconnexion), puis retrait de ses cumuls. */
    public synchronized List<Ligne> viderJoueur(String joueur) {
        List<Ligne> lignes = new ArrayList<>();
        String suffixe = "\u0000" + (joueur == null ? "" : joueur);
        Iterator<Map.Entry<String, Cumul>> it = cumuls.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Cumul> e = it.next();
            if (e.getKey().endsWith(suffixe)) {
                lignes.add(versLigne(e.getKey(), e.getValue()));
                it.remove();
            }
        }
        return lignes;
    }

    private static String cle(String type, String joueur) {
        return (type == null ? "" : type) + "\u0000" + (joueur == null ? "" : joueur);
    }

    private static Ligne versLigne(String cle, Cumul c) {
        int sep = cle.indexOf('\u0000');
        String type = cle.substring(0, sep);
        String joueur = cle.substring(sep + 1);
        String signe = c.montant >= 0 ? "+" : "";
        String valeur = signe + c.montant + " $FB (" + c.quantite + " " + (c.unite == null ? "" : c.unite) + ")";
        return new Ligne(type, joueur, valeur.replace(" )", ")"));
    }
}
