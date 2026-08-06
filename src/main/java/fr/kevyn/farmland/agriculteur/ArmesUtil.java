package fr.kevyn.farmland.agriculteur;

/**
 * Formule de degats PARTAGEE entre l'Epee (Tueur) et la Hache (Agriculteur).
 * Niveau 0 = 5 degats (base), niveau 10 = 15 degats (+1 par niveau).
 */
public class ArmesUtil {

    public static final int NIVEAU_MAX = 10;

    public static double calculerDegats(int niveau) {
        int niveauBorne = Math.max(0, Math.min(NIVEAU_MAX, niveau));
        return 5.0 + niveauBorne;
    }
}
