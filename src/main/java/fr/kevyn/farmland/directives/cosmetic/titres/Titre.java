package fr.kevyn.farmland.directives.cosmetic.titres;

import fr.kevyn.farmland.directives.infrastructure.JobType;

/**
 * Titres affichés dans le chat avant le grade : « [Magnat] [Joueur] Pseudo : message ».
 * Les uns s'achètent dans /shop, les autres se gagnent (niveau 50 d'un métier).
 * Le nom de la constante est enregistré dans titres.json : ne jamais le renommer.
 */
public enum Titre {

    BATISSEUR("§a[Bâtisseur]", 2_000, null),
    FERMIER("§e[Fermier]", 2_000, null),
    AVENTURIER("§b[Aventurier]", 3_000, null),
    ARCHITECTE("§9[Architecte]", 5_000, null),
    MAGNAT("§6[Magnat]", 25_000, null),
    LEGENDE("§d§l[Légende]", 100_000, null),

    MAITRE_MINEUR("§7[Maître Mineur]", 0, JobType.MINEUR),
    MAITRE_FARMEUR("§7[Maître Farmeur]", 0, JobType.FARMEUR),
    MAITRE_PECHEUR("§7[Maître Pêcheur]", 0, JobType.PECHEUR),
    MAITRE_AGRICULTEUR("§7[Maître Agriculteur]", 0, JobType.AGRICULTEUR),
    MAITRE_TUEUR("§7[Maître Tueur]", 0, JobType.TUEUR);

    /** Niveau de métier qui débloque un titre au mérite. */
    public static final int NIVEAU_MERITE = 50;

    private final String affichage;
    private final int prix;
    private final JobType metier;

    Titre(String affichage, int prix, JobType metier) {
        this.affichage = affichage;
        this.prix = prix;
        this.metier = metier;
    }

    /** Texte coloré du titre, ex. « §6[Magnat] ». */
    public String getAffichage() { return affichage; }

    public int getPrix() { return prix; }

    /** Métier du titre au mérite, ou null pour un titre à acheter. */
    public JobType getMetier() { return metier; }

    public boolean auMerite() { return metier != null; }

    /** Titre lu dans un fichier, ou null s'il n'existe plus. */
    public static Titre depuisNom(String nom) {
        if (nom == null) return null;
        try {
            return valueOf(nom);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
