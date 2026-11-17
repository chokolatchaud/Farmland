package fr.kevyn.farmland.directives.marché.calcul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests de MarketCalcTask.choisirMetierBulletin(variations).
 *
 * Le bulletin economique (toutes les 30 min) annonce :
 * - le metier dont le coefficient a le plus BAISSE d'au moins 5 points (surproduction) ;
 * - sinon celui qui a le plus MONTE d'au moins 5 points (redressement) ;
 * - sinon aucun bulletin.
 * (L'ancienne regle "+/-2 % selon 20 / 5 ventes" a ete remplacee par l'offre et la demande, voir MarcheOffreDemandeTest.)
 */
class MarketCalcTaskTest {

    private static Map<String, Integer> variations(int mineur, int farmeur, int agriculteur, int pecheur, int tueur) {
        Map<String, Integer> v = new HashMap<>();
        v.put(MarketCalc.MINEUR, mineur);
        v.put(MarketCalc.FARMEUR, farmeur);
        v.put(MarketCalc.AGRICULTEUR, agriculteur);
        v.put(MarketCalc.PECHEUR, pecheur);
        v.put(MarketCalc.TUEUR, tueur);
        return v;
    }

    @Test
    void aucuneVariationAucunBulletin() {
        assertNull(MarketCalcTask.choisirMetierBulletin(variations(0, 0, 0, 0, 0)));
    }

    @Test
    void variationsSousLeSeuilAucunBulletin() {
        assertNull(MarketCalcTask.choisirMetierBulletin(variations(4, -4, 3, -1, 2)));
    }

    @Test
    void baisseDe5PointsDonneUnBulletin() {
        assertEquals(MarketCalc.FARMEUR, MarketCalcTask.choisirMetierBulletin(variations(0, -5, 0, 0, 0)));
    }

    @Test
    void laPlusForteBaissePasseAvantLesHausses() {
        assertEquals(MarketCalc.TUEUR, MarketCalcTask.choisirMetierBulletin(variations(30, -6, 0, 0, -40)));
    }

    @Test
    void sansBaisseLaPlusForteHausse() {
        assertEquals(MarketCalc.PECHEUR, MarketCalcTask.choisirMetierBulletin(variations(6, 0, 2, 12, 0)));
    }

    @Test
    void metierAbsentCompteCommeZero() {
        Map<String, Integer> v = new HashMap<>();
        v.put(MarketCalc.MINEUR, -10);
        assertEquals(MarketCalc.MINEUR, MarketCalcTask.choisirMetierBulletin(v));
    }
}
