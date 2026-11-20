package fr.kevyn.farmland.directives.gameplay.missions;

import org.bukkit.Material;

/**
 * Types de missions du jour. Les objectifs sont calés sur les mesures du 17/11
 * (actions par heure de chaque métier) : facile ≈ 10 min, moyen ≈ 20 min, difficile ≈ 40 min.
 * Les animaux et les monstres sont comptés sans spawner, d'où des objectifs plus bas.
 */
public enum TypeMission {

    MINEUR("Mine %d blocs", Material.NETHERITE_PICKAXE, "pioche", 250, 600, 1200),
    FARMEUR("Récolte %d cultures", Material.NETHERITE_HOE, "houe", 150, 350, 700),
    PECHEUR("Pêche %d fois", Material.FISHING_ROD, "peche", 40, 80, 150),
    AGRICULTEUR("Tue %d animaux", Material.NETHERITE_AXE, "hache", 25, 60, 120),
    TUEUR("Tue %d monstres", Material.NETHERITE_SWORD, "epee", 25, 60, 120),
    MARCHE("Vends %d jetons", Material.GOLD_INGOT, "bag", 200, 500, 1000),
    COURSE("Termine %d course(s) de bateaux", Material.OAK_BOAT, "joinboat", 1, 2, 3);

    private final String texte;
    private final Material icone;
    private final String commande;
    private final int[] objectifs;

    TypeMission(String texte, Material icone, String commande, int facile, int moyen, int difficile) {
        this.texte = texte;
        this.icone = icone;
        this.commande = commande;
        this.objectifs = new int[] {facile, moyen, difficile};
    }

    public int objectif(Difficulte difficulte) {
        return objectifs[difficulte.ordinal()];
    }

    /** « Mine 250 blocs », « Termine 1 course de bateaux ». */
    public String description(int objectif) {
        String s = String.format(texte, objectif);
        return objectif > 1 ? s.replace("(s)", "s") : s.replace("(s)", "");
    }

    public Material getIcone() { return icone; }

    /** Commande lancée par le bouton du guide (recevoir l'outil, ouvrir le sac, rejoindre la course). */
    public String getCommande() { return commande; }
}
