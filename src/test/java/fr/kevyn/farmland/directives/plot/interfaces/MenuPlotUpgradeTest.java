package fr.kevyn.farmland.directives.plot.interfaces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Prix des agrandissements de plot (/plot buy). */
class MenuPlotUpgradeTest {

    @Test
    void prixDuPremierPalier() {
        assertEquals(50, MenuPlotUpgrade.getCost(0));
        assertEquals(50, MenuPlotUpgrade.getCost(6));
        assertEquals(65, MenuPlotUpgrade.getCost(7));
        assertEquals(100, MenuPlotUpgrade.getCost(20));
    }

    @Test
    void chaquePalierAjouteVingt() {
        assertEquals(70, MenuPlotUpgrade.getCost(21));   // palier 2
        assertEquals(120, MenuPlotUpgrade.getCost(41));
        assertEquals(90, MenuPlotUpgrade.getCost(42));   // palier 3
        assertEquals(50 + 10 * 20, MenuPlotUpgrade.getCost(210)); // palier 11
    }

    @Test
    void rangNegatifRefuse() {
        assertEquals(-1, MenuPlotUpgrade.getCost(-1));
    }

    @Test
    void coutTotalDuPremierPalier() {
        int total = 0;
        for (int r = 0; r < MenuPlotUpgrade.getMaxUpgrades(); r++) total += MenuPlotUpgrade.getCost(r);
        assertEquals(7 * 50 + 7 * 65 + 7 * 100, total); // 1 505 $FB pour +105 de bordure
    }
}
