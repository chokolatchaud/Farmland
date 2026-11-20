package fr.kevyn.farmland.directives.cosmetic.titres;

import java.util.HashSet;
import java.util.Set;

/**
 * Règles des titres, sans Bukkit (testées par JUnit).
 */
public final class TitresCalc {

    private TitresCalc() {
    }

    /** Titres d'un joueur : ceux qu'il a achetés et celui qu'il porte (nom de constante, ou null). */
    public static class Donnees {
        Set<String> possedes = new HashSet<>();
        String actif;

        public String getActif() { return actif; }
    }

    public enum Achat { OK, DEJA_POSSEDE, AU_MERITE, SOLDE_INSUFFISANT }

    public enum Equipement { OK, NON_POSSEDE }

    /** Un titre au mérite est possédé dès que le niveau du métier est atteint. */
    public static boolean possede(Donnees d, Titre titre, int niveauMetier) {
        if (titre.auMerite()) return niveauMetier >= Titre.NIVEAU_MERITE;
        return d.possedes != null && d.possedes.contains(titre.name());
    }

    /** Vérifie l'achat ; n'enregistre le titre que si l'achat est permis (le débit est fait par l'appelant). */
    public static Achat acheter(Donnees d, Titre titre, long solde) {
        if (titre.auMerite()) return Achat.AU_MERITE;
        if (d.possedes == null) d.possedes = new HashSet<>();
        if (d.possedes.contains(titre.name())) return Achat.DEJA_POSSEDE;
        if (solde < titre.getPrix()) return Achat.SOLDE_INSUFFISANT;
        d.possedes.add(titre.name());
        return Achat.OK;
    }

    public static Equipement equiper(Donnees d, Titre titre, int niveauMetier) {
        if (!possede(d, titre, niveauMetier)) return Equipement.NON_POSSEDE;
        d.actif = titre.name();
        return Equipement.OK;
    }

    /**
     * Préfixe du chat pour le titre porté (« §6[Magnat]§r ») ou "" sans titre.
     * Un titre au mérite dont le niveau n'est plus atteint n'est pas affiché.
     */
    public static String prefixe(Donnees d, int niveauMetierDuTitre) {
        if (d == null) return "";
        Titre t = Titre.depuisNom(d.actif);
        if (t == null || !possede(d, t, niveauMetierDuTitre)) return "";
        return t.getAffichage() + "§r ";
    }
}
