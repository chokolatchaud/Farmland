package fr.kevyn.farmland.directives.infrastructure;

import fr.kevyn.farmland.directives.infrastructure.JobType;

/**
 * Icônes de la police du resource pack (ressourcepackgenerator/glyphes/glyphes.json).
 * Chaque constante est un caractère affiché comme une petite image (8x8).
 * Toujours précédé de §f : une icône prend la couleur du texte qui la précède.
 * Les codes sont fixés dans glyphes.json : ne jamais les changer, seulement en ajouter.
 */
public final class Glyphes {

    private Glyphes() {
    }

    /** Pièce FarmLand ($FB). */
    public static final String PIECE = "";
    public static final String MINEUR = "";
    public static final String FARMEUR = "";
    public static final String PECHEUR = "";
    public static final String AGRICULTEUR = "";
    public static final String TUEUR = "";

    /** « 1 234 » suivi de la pièce, en blanc pour garder les couleurs de l'icône. */
    public static String argent(long montant, String couleurTexte) {
        return couleurTexte + montant + " §f" + PIECE + couleurTexte;
    }

    public static String metier(JobType job) {
        return switch (job) {
            case MINEUR -> MINEUR;
            case FARMEUR -> FARMEUR;
            case PECHEUR -> PECHEUR;
            case AGRICULTEUR -> AGRICULTEUR;
            case TUEUR -> TUEUR;
        };
    }
}
