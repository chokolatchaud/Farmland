package fr.kevyn.farmland.directives.gameplay.missions;

/** Une mission de chaque difficulté par jour, dans cet ordre. */
public enum Difficulte {
    FACILE("Facile", "§a"),
    MOYEN("Moyenne", "§e"),
    DIFFICILE("Difficile", "§c");

    private final String nom;
    private final String couleur;

    Difficulte(String nom, String couleur) {
        this.nom = nom;
        this.couleur = couleur;
    }

    public String getNom() { return nom; }

    public String getCouleur() { return couleur; }
}
