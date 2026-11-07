package fr.kevyn.farmland.directives.marché.calcul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.kevyn.farmland.doonees.marche.Market;

/**
 * Tests de MarketCalc.getPrixActuel(metier, market).
 *
 * Prix actuel = prix de base * coefficient / 100, minimum 0,01.
 * Les prix sont des double : on compare avec une petite tolerance (DELTA)
 * car 9,9 peut etre stocke comme 9,900000000001.
 */
class MarketCalcTest {

    private static final double DELTA = 0.0001;

    @Test
    void mineur_coefficient100_prixDeBase() {
        Market market = new Market(100, 100, 100, 100, 100);
        assertEquals(15.0, MarketCalc.getPrixActuel(MarketCalc.MINEUR, market), DELTA);
    }

    @Test
    void farmeur_coefficient50_moitieDuPrix() {
        Market market = new Market(100, 50, 100, 100, 100);
        assertEquals(4.0, MarketCalc.getPrixActuel(MarketCalc.FARMEUR, market), DELTA);
    }

    @Test
    void tueur_coefficient150_prixPlus50Pourcent() {
        Market market = new Market(100, 100, 100, 100, 150);
        assertEquals(30.0, MarketCalc.getPrixActuel(MarketCalc.TUEUR, market), DELTA);
    }

    @Test
    void pecheur_coefficient99_prixAvecVirgule() {
        Market market = new Market(100, 100, 100, 99, 100);
        assertEquals(9.9, MarketCalc.getPrixActuel(MarketCalc.PECHEUR, market), DELTA);
    }

    @Test
    void agriculteur_coefficient75() {
        Market market = new Market(100, 100, 75, 100, 100);
        assertEquals(9.0, MarketCalc.getPrixActuel(MarketCalc.AGRICULTEUR, market), DELTA);
    }

    @Test
    void metierInconnu_prixZero() {
        Market market = new Market(100, 100, 100, 100, 100);
        assertEquals(0.0, MarketCalc.getPrixActuel("Bucheron", market), DELTA);
    }

    @Test
    void coefficientZero_prixMinimum001() {
        Market market = new Market(0, 100, 100, 100, 100);
        assertEquals(0.01, MarketCalc.getPrixActuel(MarketCalc.MINEUR, market), DELTA);
    }
}
