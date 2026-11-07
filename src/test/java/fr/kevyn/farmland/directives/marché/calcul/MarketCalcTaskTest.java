package fr.kevyn.farmland.directives.marché.calcul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests de MarketCalcTask.recalculer(coefficientActuel, nombreDeVentes).
 *
 * Rappel des regles :
 * - plus de 20 ventes : coefficient - 2% (division entiere, donc tronquee)
 * - moins de 5 ventes et coefficient < 100 : coefficient + 2% (tronque)
 * - sinon : inchange
 * - resultat toujours ramene entre 50 et 150
 */
class MarketCalcTaskTest {

    // ===== SURPRODUCTION (plus de 20 ventes) =====

    @Test
    void plusDe20Ventes_leCoefficientBaisse() {
        assertEquals(98, MarketCalcTask.recalculer(100, 21));
    }

    @Test
    void exactement20Ventes_leCoefficientNeChangePas() {
        assertEquals(100, MarketCalcTask.recalculer(100, 20));
    }

    // ===== RECUPERATION (moins de 5 ventes) =====

    @Test
    void moinsDe5Ventes_coefficientSous100_leCoefficientMonte() {
        // 80 * 2 / 100 = 1,6 -> tronque a 1
        assertEquals(81, MarketCalcTask.recalculer(80, 0));
    }

    @Test
    void exactement5Ventes_coefficientSous100_leCoefficientNeChangePas() {
        assertEquals(80, MarketCalcTask.recalculer(80, 5));
    }

    @Test
    void moinsDe5Ventes_coefficientA100_leCoefficientNeChangePas() {
        assertEquals(100, MarketCalcTask.recalculer(100, 0));
    }

    @Test
    void moinsDe5Ventes_coefficientAuDessusDe100_leCoefficientNeChangePas() {
        assertEquals(130, MarketCalcTask.recalculer(130, 2));
    }

    // ===== ZONE STABLE (entre 5 et 20 ventes) =====

    @Test
    void entre5Et20Ventes_coefficientSous100_leCoefficientNeChangePas() {
        assertEquals(80, MarketCalcTask.recalculer(80, 10));
    }

    @Test
    void entre5Et20Ventes_coefficientAuDessusDe100_leCoefficientNeChangePas() {
        assertEquals(120, MarketCalcTask.recalculer(120, 10));
    }

    // ===== BORNES 50 ET 150 =====

    @Test
    void borneBasse_leCoefficientNeDescendPasSous50() {
        // 50 - 1 = 49 -> ramene a 50
        assertEquals(50, MarketCalcTask.recalculer(50, 21));
    }

    @Test
    void justeAuDessusDeLaBorneBasse_leCoefficientArriveA50() {
        assertEquals(50, MarketCalcTask.recalculer(51, 21));
    }

    @Test
    void coefficientSous50_estRemonteA50() {
        // 40 * 2 / 100 = 0 -> 40, puis ramene a 50
        assertEquals(50, MarketCalcTask.recalculer(40, 0));
    }

    @Test
    void borneHaute_unCoefficientAuDessusDe150_estRameneA150() {
        assertEquals(150, MarketCalcTask.recalculer(160, 10));
    }

    @Test
    void borneHaute_coefficientA150_baisseDe3Points() {
        // 150 * 2 / 100 = 3
        assertEquals(147, MarketCalcTask.recalculer(150, 21));
    }

    // ===== TRONCATURE DES ENTIERS =====

    @Test
    void troncature_hausse_99DevientCent() {
        // 99 * 2 / 100 = 1,98 -> tronque a 1
        assertEquals(100, MarketCalcTask.recalculer(99, 0));
    }

    @Test
    void troncature_baisse_149Devient147() {
        // 149 * 2 / 100 = 2,98 -> tronque a 2
        assertEquals(147, MarketCalcTask.recalculer(149, 21));
    }
}
