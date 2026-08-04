package fr.kevyn.farmland.agriculteur;

/**
 * Formule de degats PARTAGEE entre l'Epee (Agriculteur) et la Hache (Tueur).
 * Les deux niveaux restent separes sur PlayerServer (epeeLevel/hacheLevel),
 * mais la meme formule de calcul s'applique aux deux - "lies" au sens du
 * calcul, pas de la progression.
 */
public class ArmesUtil {

	/** Degat de base a 1.0 (comme un poing/epee de base), +0.5 par niveau */
	public static double calculerDegats(int niveau) {
		return 1.0 + (niveau * 0.5);
	}
}
