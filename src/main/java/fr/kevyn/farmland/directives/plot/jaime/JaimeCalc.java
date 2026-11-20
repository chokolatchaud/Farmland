package fr.kevyn.farmland.directives.plot.jaime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Règles des « j'aime » sur les plots, sans Bukkit (testées par JUnit) :
 * un j'aime par plot et par jour pour chaque joueur, jamais sur son propre plot.
 */
public final class JaimeCalc {

    private JaimeCalc() {
    }

    /** J'aime reçus par un plot : total depuis toujours et votants du jour (votant → date). */
    public static class Donnees {
        int total;
        Map<String, String> votes = new HashMap<>();

        public int getTotal() { return total; }
    }

    public enum Reponse { OK, PROPRE_PLOT, DEJA_AUJOURDHUI }

    /** Ajoute le j'aime s'il est permis ; ne change rien sinon. */
    public static Reponse aimer(Donnees plot, UUID votant, UUID proprietaire, LocalDate jour) {
        if (votant.equals(proprietaire)) return Reponse.PROPRE_PLOT;
        if (plot.votes == null) plot.votes = new HashMap<>();
        String date = jour.toString();
        if (date.equals(plot.votes.get(votant.toString()))) return Reponse.DEJA_AUJOURDHUI;
        plot.votes.put(votant.toString(), date);
        plot.total = (int) Math.min(Integer.MAX_VALUE, (long) plot.total + 1);
        return Reponse.OK;
    }

    /** Oublie les votes des jours passés (seuls ceux du jour servent à bloquer un 2e j'aime). */
    public static void oublierVotesAnciens(Donnees plot, LocalDate jour) {
        if (plot.votes == null) {
            plot.votes = new HashMap<>();
            return;
        }
        String date = jour.toString();
        plot.votes.values().removeIf(d -> !date.equals(d));
    }

    /** Propriétaires classés par nombre de j'aime (du plus aimé au moins aimé), plots sans j'aime exclus. */
    public static List<UUID> classement(Map<UUID, Donnees> plots) {
        List<UUID> liste = new ArrayList<>();
        for (Map.Entry<UUID, Donnees> e : plots.entrySet()) {
            if (e.getValue() != null && e.getValue().total > 0) liste.add(e.getKey());
        }
        liste.sort(Comparator.comparingInt((UUID id) -> plots.get(id).total).reversed().thenComparing(UUID::toString));
        return liste;
    }

    /** « 1 j'aime », « 12 j'aime » (invariable). */
    public static String texte(int total) {
        return "❤ " + total + " j'aime";
    }
}
