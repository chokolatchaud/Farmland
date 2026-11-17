package fr.kevyn.farmland.directives.metiers.agriculteur;

/**
 * Formule de degats PARTAGEE entre l'Epee (Tueur) et la Hache (Agriculteur).
 * Niveaux INFINIS. Les degats montent de +1 par niveau (5 au niveau 0)
 * jusqu'au niveau NIVEAU_PLAFOND_DEGATS (13), soit 18 degats au maximum :
 * DEGATS_MAX (20) n'est jamais atteint (verifie par ArmesUtilTest).
 * Au-dela, le niveau continue de monter sans ajouter de degats.
 * Pour viser 20 degats, il faudrait NIVEAU_PLAFOND_DEGATS = 15 (choix du developpeur).
 */
public class ArmesUtil {

    public static final double DEGATS_MAX = 20.0;
    public static final int NIVEAU_PLAFOND_DEGATS = 13; // niveau ou les degats atteignent DEGATS_MAX | limité a 13 car décalage de 1 à la formule du nv excedentaire et celle du tirermultiplicateur

    public static double calculerDegats(int niveau) {
        int niveauBorne = Math.max(0, Math.min(NIVEAU_PLAFOND_DEGATS, niveau));
        return Math.min(DEGATS_MAX, 5.0 + niveauBorne);
    }

    public static int niveauExcedentaire(int niveau) {
        return Math.max(0, niveau - NIVEAU_PLAFOND_DEGATS);
    }
}
