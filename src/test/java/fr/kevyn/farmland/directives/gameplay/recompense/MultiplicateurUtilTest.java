package fr.kevyn.farmland.directives.gameplay.recompense;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiplicateurUtilTest {

    @Test
    void niveauUnOuMoinsDonneToujoursUn() {
        for (int i = 0; i < 100; i++) {
            assertEquals(1, MultiplicateurUtil.tirerMultiplicateur(1));
            assertEquals(1, MultiplicateurUtil.tirerMultiplicateur(0));
            assertEquals(1, MultiplicateurUtil.tirerMultiplicateur(-5));
        }
    }

    @Test
    void resultatToujoursEntreUnEtLeNiveau() {
        for (int niveau = 2; niveau <= 30; niveau++) {
            for (int i = 0; i < 500; i++) {
                int m = MultiplicateurUtil.tirerMultiplicateur(niveau);
                assertTrue(m >= 1 && m <= niveau, "niveau " + niveau + " -> " + m);
            }
        }
    }

    @Test
    void lesPetitsMultiplicateursSontLesPlusFrequents() {
        // Poids : palier p vaut (niveau - p + 1). Au niveau 5 : P(1) = 5/15 = 33 %, P(5) = 1/15 = 6,7 %.
        int niveau = 5;
        int tirages = 150_000;
        int[] compte = new int[niveau + 1];
        for (int i = 0; i < tirages; i++) {
            compte[MultiplicateurUtil.tirerMultiplicateur(niveau)]++;
        }
        for (int p = 1; p < niveau; p++) {
            assertTrue(compte[p] > compte[p + 1], "palier " + p + " doit sortir plus souvent que " + (p + 1));
        }
        double p1 = compte[1] / (double) tirages;
        double p5 = compte[5] / (double) tirages;
        assertEquals(1.0 / 3, p1, 0.01);
        assertEquals(1.0 / 15, p5, 0.01);
    }
}
