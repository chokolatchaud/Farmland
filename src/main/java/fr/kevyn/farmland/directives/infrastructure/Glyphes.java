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

    // ===== Fonds illustrés des menus (images 176 px de large, dans le titre) =====
    public static final String FOND_GUIDE = ""; // 5 lignes (176x110)
    public static final String FOND_SAC = "";   // 3 lignes (176x74), /bag
    public static final String FOND_VENTE = ""; // 3 lignes (176x74), menu de vente d'un jeton
    public static final String FOND_GUIDE_PAGE = "";  // 4 lignes, pages du guide
    public static final String FOND_COSMETIQUES = ""; // 2 lignes, /buy cosmetic
    public static final String FOND_METIERS = "";     // 4 lignes, /metiers
    public static final String FOND_CONFIG = "";      // 3 lignes, /plot config
    public static final String FOND_GRILLE = "";      // 6 lignes, niveaux d'un métier et /plot visit
    public static final String FOND_AGRANDIR = "";    // 6 lignes, /plot buy
    public static final String FOND_BOUTIQUE = "";    // 4 lignes, /shop
    public static final String FOND_TITRES = "";      // 6 lignes, /shop > Titres
    public static final String FOND_UNE = "";         // 4 lignes, /shop > Plot à la une
    /** Reculs du texte : -8 px avant l'image, -169 px après pour replacer le titre. */
    public static final String RECUL_8 = "";
    public static final String RECUL_169 = "";

    /**
     * Titre de menu avec un fond illustré : le fond couvre la partie haute de la
     * fenêtre (les cases vides du menu ne doivent pas être remplies de vitres).
     */
    public static String titreAvecFond(String fond, String titre) {
        return "§f" + RECUL_8 + fond + RECUL_169 + "§8" + titre;
    }

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
