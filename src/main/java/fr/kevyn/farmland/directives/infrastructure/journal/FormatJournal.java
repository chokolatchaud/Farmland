package fr.kevyn.farmland.directives.infrastructure.journal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format d'une ligne du journal : date;type;joueur;valeur
 * Pur (aucune dépendance Bukkit) pour pouvoir être testé.
 */
public final class FormatJournal {

    public static final String FICHIER_COURANT = "evenements.log";

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMAT_JOUR = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Pattern ARCHIVE = Pattern.compile("^evenements-(\\d{4}-\\d{2}-\\d{2})\\.log$");

    private FormatJournal() {
    }

    /** Une ligne complète, sans retour à la ligne. Ne lève jamais d'erreur. */
    public static String ligne(LocalDateTime date, String type, String joueur, Object valeur) {
        String d = date == null ? "" : FORMAT_DATE.format(date);
        String t = nettoyer(type);
        if (t.isEmpty()) t = "inconnu";
        String j = nettoyer(joueur);
        if (j.isEmpty()) j = "-";
        String v;
        try {
            v = valeur == null ? "" : nettoyer(String.valueOf(valeur));
        } catch (RuntimeException e) {
            v = "?";
        }
        return d + ";" + t + ";" + j + ";" + v;
    }

    /** Retire ce qui casserait le format : séparateur et retours à la ligne. */
    static String nettoyer(String texte) {
        if (texte == null) return "";
        return texte.replace(';', ',').replace('\r', ' ').replace('\n', ' ').trim();
    }

    /** Nom de l'archive d'un jour : evenements-AAAA-MM-JJ.log */
    public static String nomArchive(LocalDate jour) {
        return "evenements-" + FORMAT_JOUR.format(jour) + ".log";
    }

    /**
     * Vrai seulement pour une archive evenements-AAAA-MM-JJ.log plus vieille que
     * retentionJours. Tout autre nom de fichier renvoie faux.
     */
    public static boolean archiveASupprimer(String nomFichier, LocalDate aujourdHui, int retentionJours) {
        if (nomFichier == null || aujourdHui == null) return false;
        Matcher m = ARCHIVE.matcher(nomFichier);
        if (!m.matches()) return false;
        try {
            LocalDate jour = LocalDate.parse(m.group(1), FORMAT_JOUR);
            return jour.isBefore(aujourdHui.minusDays(retentionJours));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
