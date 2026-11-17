package fr.kevyn.farmland.directives.gameplay.recompense;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecompenseUtilTest {

    @Test
    void xpNecessaireCentFoisNiveauPlusUn() {
        assertEquals(100, RecompenseUtil.getXpNeeded(0));
        assertEquals(200, RecompenseUtil.getXpNeeded(1));
        assertEquals(1100, RecompenseUtil.getXpNeeded(10));
    }

    @Test
    void xpCumuleePourAtteindreUnNiveau() {
        // Du niveau 0 au niveau 10 : 100 + 200 + ... + 1000 = 5 500 XP
        int total = 0;
        for (int n = 0; n < 10; n++) total += RecompenseUtil.getXpNeeded(n);
        assertEquals(5500, total);
    }

    @Test
    void vitesseFarmeurUnPourCentParNiveau() {
        assertEquals(1.0, RecompenseUtil.getBreakSpeedFarmeur(0), 1e-9);
        assertEquals(1.5, RecompenseUtil.getBreakSpeedFarmeur(50), 1e-9);
    }

    @Test
    void jetonsArmeSuiventLeNiveauDesLeNiveau1() {
        // Avant : 1 jeton fixe jusqu'au niveau 13. Maintenant : tirage 1..niveau.
        assertEquals(1, RecompenseUtil.tirerJetonsArme(0));
        assertEquals(1, RecompenseUtil.tirerJetonsArme(1));
        boolean auDessusDe1 = false;
        long somme = 0;
        int n = 20_000;
        for (int i = 0; i < n; i++) {
            int j = RecompenseUtil.tirerJetonsArme(10);
            assertTrue(j >= 1 && j <= 10, "jetons hors bornes : " + j);
            if (j > 1) auDessusDe1 = true;
            somme += j;
        }
        assertTrue(auDessusDe1, "au niveau 10, plus d'un jeton doit sortir");
        // moyenne theorique (niveau + 2) / 3 = 4
        assertEquals(4.0, (double) somme / n, 0.15);
    }
}
