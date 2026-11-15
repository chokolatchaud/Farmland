package fr.kevyn.farmland.directives.gameplay.recompense;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
