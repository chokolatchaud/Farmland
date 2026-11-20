package fr.kevyn.farmland.directives.gameplay.missions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Calculs des missions du jour, sans Bukkit (testés par JUnit) :
 * tirage, avancement, récompenses.
 */
public final class MissionsCalc {

    public static final int NB_MISSIONS = 3;

    private MissionsCalc() {
    }

    public record Mission(TypeMission type, Difficulte difficulte, int objectif) {
        public String description() {
            return type.description(objectif);
        }
    }

    /** Montants versés (réglables dans config.yml, section missions). */
    public record Recompenses(int facile, int moyen, int difficile, int bonus) {
        public static final Recompenses PAR_DEFAUT = new Recompenses(100, 200, 350, 250);

        public int de(Difficulte d) {
            return switch (d) {
                case FACILE -> facile;
                case MOYEN -> moyen;
                case DIFFICILE -> difficile;
            };
        }

        public int totalMaximum() {
            return facile + moyen + difficile + bonus;
        }
    }

    /** Ce qu'un avancement vient de terminer : indices des missions finies et bonus. */
    public record Resultat(List<Integer> terminees, boolean bonus) {
        public boolean rien() {
            return terminees.isEmpty() && !bonus;
        }
    }

    /**
     * Les 3 missions d'un joueur pour un jour : 3 types différents, une facile,
     * une moyenne, une difficile. Toujours le même résultat pour le même joueur et le même jour.
     */
    public static List<Mission> tirer(UUID joueur, LocalDate jour) {
        long graine = joueur.getMostSignificantBits() ^ Long.rotateLeft(joueur.getLeastSignificantBits(), 17)
                ^ (jour.toEpochDay() * 0x9E3779B97F4A7C15L);
        List<TypeMission> types = new ArrayList<>(Arrays.asList(TypeMission.values()));
        Collections.shuffle(types, new Random(graine));
        Difficulte[] difficultes = Difficulte.values();
        List<Mission> missions = new ArrayList<>(NB_MISSIONS);
        for (int i = 0; i < NB_MISSIONS; i++) {
            TypeMission type = types.get(i);
            missions.add(new Mission(type, difficultes[i], type.objectif(difficultes[i])));
        }
        return List.copyOf(missions);
    }

    /** La progression du jour : celle donnée si elle est de ce jour, sinon une nouvelle (minuit passé). */
    public static ProgressionJour duJour(ProgressionJour progression, LocalDate jour) {
        String date = jour.toString();
        if (progression != null && progression.estValide() && date.equals(progression.date)) return progression;
        return new ProgressionJour(date);
    }

    /**
     * Ajoute des actions de ce type. La progression ne dépasse jamais l'objectif ;
     * une mission n'est terminée (et payée) qu'une fois, le bonus aussi.
     */
    public static Resultat avancer(ProgressionJour p, List<Mission> missions, TypeMission type, int quantite) {
        if (quantite <= 0) return new Resultat(List.of(), false);
        List<Integer> terminees = new ArrayList<>();
        for (int i = 0; i < missions.size(); i++) {
            Mission m = missions.get(i);
            if (m.type() != type || p.faites[i]) continue;
            p.progression[i] = (int) Math.min(m.objectif(), (long) p.progression[i] + quantite);
            if (p.progression[i] >= m.objectif()) {
                p.faites[i] = true;
                terminees.add(i);
            }
        }
        boolean bonus = false;
        if (!p.bonus && p.nombreFaites() == NB_MISSIONS) {
            p.bonus = true;
            bonus = true;
        }
        return new Resultat(List.copyOf(terminees), bonus);
    }

    /** Somme à verser pour ce résultat. */
    public static int gain(Resultat r, List<Mission> missions, Recompenses recompenses) {
        int total = 0;
        for (int i : r.terminees()) total += recompenses.de(missions.get(i).difficulte());
        if (r.bonus()) total += recompenses.bonus();
        return total;
    }

    /** « 5 h 12 min » avant minuit. */
    public static String tempsAvantMinuit(int secondesDepuisMinuit) {
        int reste = Math.max(0, 24 * 3600 - secondesDepuisMinuit);
        int h = reste / 3600;
        int min = (reste % 3600) / 60;
        return h > 0 ? h + " h " + min + " min" : Math.max(1, min) + " min";
    }
}
