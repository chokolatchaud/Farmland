package fr.kevyn.farmland.menufarm;

import java.util.Random;

/**
 * Systeme de multiplicateur par palier, partage entre la Houe (Farmeur) et
 * la Canne (Pecheur). Le niveau debloque des paliers (niveau N debloque les
 * multiplicateurs x1 a xN), et a chaque recolte/peche, un tirage aleatoire
 * PONDERE determine quel multiplicateur s'applique - les paliers bas sont
 * plus frequents que les hauts.
 */
public class MultiplicateurUtil {

    private static final Random random = new Random();

    public static int tirerMultiplicateur(int niveau) {
        if (niveau <= 1) return 1;

        int poidsTotal = 0;
        for (int palier = 1; palier <= niveau; palier++) {
            poidsTotal += (niveau - palier + 1);
        }

        int tirage = random.nextInt(poidsTotal);
        int cumul = 0;
        for (int palier = 1; palier <= niveau; palier++) {
            cumul += (niveau - palier + 1);
            if (tirage < cumul) return palier;
        }

        return 1;
    }
}
