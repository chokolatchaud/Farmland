package fr.kevyn.farmland.agriculteur;

/**
 * Formule de degats PARTAGEE entre l'Epee (Tueur) et la Hache (Agriculteur).
 * Niveaux INFINIS. Les degats montent de +1 par niveau (5 au niveau 0)
 * jusqu'a un plafond de 20 (atteint au niveau 15) - au-dela, le niveau
 * continue de monter mais n'ajoute plus de degats : il alimente a la place
 * les chances de "duplique" (voir RecompenseUtil, meme systeme de
 * multiplicateur que Farmeur/Pecheur/Mineur).
 */
public class ArmesUtil {

    public static final double DEGATS_MAX = 20.0;
    public static final int NIVEAU_PLAFOND_DEGATS = 15; // niveau ou les degats atteignent DEGATS_MAX

    public static double calculerDegats(int niveau) {
        int niveauBorne = Math.max(0, Math.min(NIVEAU_PLAFOND_DEGATS, niveau));
        return Math.min(DEGATS_MAX, 5.0 + niveauBorne);
    }

    /** Niveau "en trop" au-dela du plafond de degats, qui alimente le multiplicateur de duplique */
    public static int niveauExcedentaire(int niveau) {
        return Math.max(0, niveau - NIVEAU_PLAFOND_DEGATS);
    }
}
