package fr.kevyn.farmland.directives.plot.une;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Enchères du « plot à la une », sans Bukkit (testées par JUnit) :
 * chaque joueur mise des $FB sur son plot (argent dépensé, jamais rendu),
 * les mises s'additionnent, le classement repart à zéro chaque lundi.
 */
public final class UneCalc {

    /** Mise minimale, pour éviter les mises de 1 $FB. */
    public static final long MISE_MINIMALE = 1_000;

    private UneCalc() {
    }

    /** Mises de la semaine (propriétaire du plot → total misé). */
    public static class Donnees {
        String semaine;
        Map<String, Long> mises = new HashMap<>();
    }

    public enum Reponse { OK, MONTANT_TROP_PETIT, SOLDE_INSUFFISANT }

    /** Résultat d'un changement de semaine : le plot gagnant de la semaine finie, s'il y en avait un. */
    public record FinDeSemaine(UUID gagnant, long mise) { }

    /** « 2026-W47 » : semaine ISO, commence le lundi. */
    public static String semaine(LocalDate jour) {
        return jour.get(IsoFields.WEEK_BASED_YEAR) + "-W" + String.format("%02d", jour.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    }

    /**
     * Remet les mises à zéro si la semaine a changé. Renvoie le gagnant de la semaine finie
     * (null si rien n'a changé ou si personne n'avait misé).
     */
    public static FinDeSemaine verifierSemaine(Donnees d, LocalDate jour) {
        String actuelle = semaine(jour);
        if (d.mises == null) d.mises = new HashMap<>();
        if (actuelle.equals(d.semaine)) return null;
        List<UUID> top = classement(d);
        FinDeSemaine fin = (d.semaine != null && !top.isEmpty()) ? new FinDeSemaine(top.get(0), mise(d, top.get(0))) : null;
        d.semaine = actuelle;
        d.mises.clear();
        return fin;
    }

    /** Vérifie la mise et l'ajoute ; le débit du solde est fait par l'appelant si OK. */
    public static Reponse miser(Donnees d, UUID proprietaire, long montant, long solde) {
        if (montant < MISE_MINIMALE) return Reponse.MONTANT_TROP_PETIT;
        if (montant > solde) return Reponse.SOLDE_INSUFFISANT;
        if (d.mises == null) d.mises = new HashMap<>();
        d.mises.merge(proprietaire.toString(), montant, (a, b) -> a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b);
        return Reponse.OK;
    }

    public static long mise(Donnees d, UUID proprietaire) {
        if (d.mises == null) return 0;
        Long m = d.mises.get(proprietaire.toString());
        return m == null ? 0 : m;
    }

    /** Plots classés de la plus grosse mise à la plus petite (mises nulles exclues). */
    public static List<UUID> classement(Donnees d) {
        List<UUID> liste = new ArrayList<>();
        if (d.mises == null) return liste;
        for (Map.Entry<String, Long> e : d.mises.entrySet()) {
            if (e.getValue() == null || e.getValue() <= 0) continue;
            try {
                liste.add(UUID.fromString(e.getKey()));
            } catch (IllegalArgumentException ignore) {
                // clé illisible : ignorée
            }
        }
        liste.sort(Comparator.comparingLong((UUID id) -> mise(d, id)).reversed().thenComparing(UUID::toString));
        return liste;
    }

    /** Rang (1 = en tête) ou 0 si le plot n'a pas misé. */
    public static int rang(Donnees d, UUID proprietaire) {
        return classement(d).indexOf(proprietaire) + 1;
    }

    /** « 3 j 4 h » avant lundi 0 h. */
    public static String tempsAvantLundi(LocalDateTime maintenant) {
        LocalDateTime lundi = maintenant.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();
        Duration reste = Duration.between(maintenant, lundi);
        long jours = reste.toDays();
        long heures = reste.toHoursPart();
        if (jours > 0) return jours + " j " + heures + " h";
        return heures > 0 ? heures + " h " + reste.toMinutesPart() + " min" : Math.max(1, reste.toMinutesPart()) + " min";
    }

    /** « 10000 », « 10k », « 1,5m » → montant, ou -1 si illisible. */
    public static long lireMontant(String texte) {
        if (texte == null) return -1;
        String t = texte.trim().toLowerCase().replace(" ", "").replace("_", "").replace(',', '.');
        long multiplicateur = 1;
        if (t.endsWith("k")) { multiplicateur = 1_000; t = t.substring(0, t.length() - 1); }
        else if (t.endsWith("m")) { multiplicateur = 1_000_000; t = t.substring(0, t.length() - 1); }
        if (t.isEmpty() || !t.matches("\\d{1,12}(\\.\\d{1,3})?")) return -1;
        java.math.BigDecimal valeur = new java.math.BigDecimal(t).multiply(java.math.BigDecimal.valueOf(multiplicateur));
        if (valeur.stripTrailingZeros().scale() > 0) return -1; // pas de centimes
        return valeur.longValueExact();
    }

    /** « 200 000 » : montant lisible. */
    public static String montant(long m) {
        return String.format(java.util.Locale.FRANCE, "%,d", m).replace(' ', ' ').replace(' ', ' ');
    }
}
