package fr.kevyn.farmland.directives.marché.calcul;

import fr.kevyn.farmland.doonees.marche.Market;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Marche par offre et demande (MarketCalc) avec les reglages par defaut :
 * volume min 2 000/h, apprentissage 72 h plafonne a 3x, echelle 24 h de volume,
 * demi-vie 12 h, penurie max 4 echelles.
 */
class MarcheOffreDemandeTest {

    private static final double CINQ_MIN = 5 / 60.0;

    @BeforeEach
    void reglagesParDefaut() {
        MarketCalc.configurer(2000, 72, 3, 24, 12, 4);
        MarketCalc.resetVentes();
        MarketCalc.resetVentesApprentissage();
    }

    @AfterEach
    void nettoyage() {
        MarketCalc.resetVentes();
        MarketCalc.resetVentesApprentissage();
    }

    private static Market marcheNeuf() {
        Market m = new Market(100, 100, 100, 100, 100);
        MarketCalc.initialiser(m);
        return m;
    }

    /** Un cycle de 5 min du marche (demande + apprentissage horaire), comme MarketCalcTask. */
    private static void cycle(Market m, int numeroCycle) {
        for (String metier : MarketCalc.METIERS) {
            if (numeroCycle % 12 == 0) {
                long vendus = MarketCalc.prendreVentesApprentissage(metier);
                MarketCalc.setVolume(metier, m, MarketCalc.apprendreVolume(MarketCalc.getVolume(metier, m), vendus, 1.0));
            }
            double s = MarketCalc.getSurplus(metier, m);
            MarketCalc.setSurplus(metier, m, MarketCalc.appliquerDemande(s, CINQ_MIN, MarketCalc.getVolume(metier, m)));
        }
    }

    // ===== COURBE : BORNES -90 % / +90 % =====

    @Test
    void surplusNulDonneLePrixDeBase() {
        assertEquals(1.0, MarketCalc.multiplicateur(0, 48_000), 1e-12);
        assertEquals(100, MarketCalc.coefficient(0, 48_000));
    }

    @Test
    void lePrixResteEntreMoins90EtPlus90PourCent() {
        assertEquals(0.1, MarketCalc.multiplicateur(1e12, 48_000), 1e-9);
        assertEquals(1.9, MarketCalc.multiplicateur(-1e12, 48_000), 1e-9);
        for (double s = -1e7; s <= 1e7; s += 12_345.6) {
            double m = MarketCalc.multiplicateur(s, 48_000);
            assertTrue(m >= 0.1 - 1e-12 && m <= 1.9 + 1e-12, "multiplicateur " + m);
        }
    }

    @Test
    void surplusPourCoefficientEstLInverseDuCoefficient() {
        for (int c = 11; c <= 189; c++) {
            assertEquals(c, MarketCalc.coefficient(MarketCalc.surplusPourCoefficient(c, 48_000), 48_000), "coef " + c);
        }
        assertEquals(11, MarketCalc.coefficient(MarketCalc.surplusPourCoefficient(0, 48_000), 48_000));
        assertEquals(189, MarketCalc.coefficient(MarketCalc.surplusPourCoefficient(500, 48_000), 48_000));
    }

    // ===== VENTES =====

    @Test
    void venteEnUneFoisOuEnPlusieursALaSuiteRapportePareil() {
        Market enUneFois = marcheNeuf();
        Market enPlusieurs = marcheNeuf();
        long g1 = MarketCalc.vendre(MarketCalc.FARMEUR, enUneFois, 50_000).gain();
        double g2 = 0;
        for (int i = 0; i < 50; i++) {
            g2 += MarketCalc.recette(MarketCalc.FARMEUR, MarketCalc.getSurplus(MarketCalc.FARMEUR, enPlusieurs), 1_000,
                    MarketCalc.echelle(MarketCalc.FARMEUR, enPlusieurs));
            MarketCalc.vendre(MarketCalc.FARMEUR, enPlusieurs, 1_000);
        }
        assertEquals(g1, g2, 1.0);
        assertEquals(enUneFois.getSurplus(MarketCalc.FARMEUR), enPlusieurs.getSurplus(MarketCalc.FARMEUR), 1e-9);
    }

    @Test
    void estimerNeModifiePasLeMarche() {
        Market m = marcheNeuf();
        MarketCalc.Vente e = MarketCalc.estimer(MarketCalc.MINEUR, m, 5_000);
        assertEquals(0.0, m.getSurplus(MarketCalc.MINEUR), 1e-12);
        MarketCalc.Vente v = MarketCalc.vendre(MarketCalc.MINEUR, m, 5_000);
        assertEquals(e, v);
        assertEquals(5_000.0, m.getSurplus(MarketCalc.MINEUR), 1e-9);
    }

    @Test
    void lePrixMoyenEstEntreLePrixAvantEtApres() {
        Market m = marcheNeuf();
        MarketCalc.Vente v = MarketCalc.vendre(MarketCalc.TUEUR, m, 20_000);
        assertTrue(v.prixApres() < v.prixMoyen() && v.prixMoyen() < v.prixAvant());
        assertEquals(17.0, v.prixAvant(), 1e-9);
    }

    @Test
    void uneVenteNeToucheQueSonMetier() {
        Market m = marcheNeuf();
        MarketCalc.vendre(MarketCalc.FARMEUR, m, 100_000);
        for (String metier : MarketCalc.METIERS) {
            if (!metier.equals(MarketCalc.FARMEUR)) assertEquals(100, m.getCoefficient(metier), metier);
        }
    }

    @Test
    void venteDeZeroJetonNeChangeRien() {
        Market m = marcheNeuf();
        assertEquals(0, MarketCalc.vendre(MarketCalc.TUEUR, m, 0).gain());
        assertEquals(0.0, m.getSurplus(MarketCalc.TUEUR), 1e-12);
    }

    // ===== DEMANDE ET APPRENTISSAGE =====

    @Test
    void venteReguliereLePrixRevientProcheDuPrixDeBase() {
        // 1 mineur niveau 30 (~18 100 jetons/h) qui vend toutes les 30 min pendant 14 jours.
        // Le marche part du volume minimal (2 000/h) : il lui faut environ une semaine pour apprendre.
        Market m = marcheNeuf();
        double parHeure = 1700 * 32 / 3.0;
        double jour7 = 0, jour14 = 0;
        for (int c = 1; c <= 14 * 24 * 12; c++) {
            if (c % 6 == 0) MarketCalc.vendre(MarketCalc.MINEUR, m, Math.round(parHeure / 2));
            cycle(m, c);
            if (c > 6 * 24 * 12 && c <= 7 * 24 * 12) jour7 += m.getCoefficient(MarketCalc.MINEUR) / 288.0;
            if (c > 13 * 24 * 12) jour14 += m.getCoefficient(MarketCalc.MINEUR) / 288.0;
        }
        assertTrue(jour7 > 80 && jour7 < 100, "prix moyen du 7e jour : " + jour7);
        assertTrue(jour14 > 92 && jour14 < 105, "prix moyen du 14e jour : " + jour14);
        assertTrue(MarketCalc.getVolume(MarketCalc.MINEUR, m) > 17_000, "volume appris " + MarketCalc.getVolume(MarketCalc.MINEUR, m));
    }

    @Test
    void unDumpNeFaitPasGonflerLeVolumeAppris() {
        double v = MarketCalc.apprendreVolume(18_000, 500_000, 1.0);
        // plafonne a 3x18 000 : +36 000 * 1/72 = +500
        assertEquals(18_500, v, 1e-6);
    }

    @Test
    void volumeJamaisSousLeMinimum() {
        assertEquals(2000, MarketCalc.apprendreVolume(2000, 0, 1.0), 1e-9);
        assertEquals(2000, MarketCalc.apprendreVolume(2100, 0, 72.0), 1e-9);
    }

    @Test
    void sansVenteLePrixMonteJusquAPlus90PourCent() {
        Market m = marcheNeuf();
        int c = 0;
        // echelle = 24 h de volume : la penurie se creuse d'une echelle par jour sans vente
        while (m.getCoefficient(MarketCalc.PECHEUR) < 170 && c < 10_000) cycle(m, ++c);
        double heures170 = c * 5 / 60.0;
        assertTrue(heures170 > 20 && heures170 < 30, "heures pour +70 % : " + heures170);
        for (int i = 0; i < 96 * 12; i++) cycle(m, ++c);
        assertEquals(190, m.getCoefficient(MarketCalc.PECHEUR));
    }

    @Test
    void lePrixRemonteApresUnDump() {
        Market m = marcheNeuf();
        MarketCalc.vendre(MarketCalc.FARMEUR, m, 500_000);
        assertEquals(10, m.getCoefficient(MarketCalc.FARMEUR));
        int c = 0;
        while (m.getCoefficient(MarketCalc.FARMEUR) < 100 && c < 10_000) cycle(m, ++c);
        double heures = c * 5 / 60.0;
        assertTrue(heures > 12 && heures < 72, "heures pour revenir au prix de base : " + heures);
    }

    // ===== PORTIONS DU MENU /bag =====

    @Test
    void quantitePourcentage() {
        assertEquals(100, MarketCalc.quantitePourcentage(1000, 10));
        assertEquals(250, MarketCalc.quantitePourcentage(1000, 25));
        assertEquals(500, MarketCalc.quantitePourcentage(1000, 50));
        assertEquals(1000, MarketCalc.quantitePourcentage(1000, 100));
        assertEquals(1, MarketCalc.quantitePourcentage(3, 10), "au moins 1 jeton");
        assertEquals(7, MarketCalc.quantitePourcentage(7, 100));
        assertEquals(0, MarketCalc.quantitePourcentage(0, 50));
        assertEquals(214_748_364, MarketCalc.quantitePourcentage(Integer.MAX_VALUE, 10), "pas de debordement");
    }

    // ===== ANCIENNES SAUVEGARDES ET AFFICHAGE =====

    @Test
    void ancienneSauvegardeGardeSesCoefficients() {
        Market ancien = new Market(98, 50, 100, 120, 150);
        assertNull(ancien.getSurplus(MarketCalc.MINEUR));
        assertNull(ancien.getVolume(MarketCalc.MINEUR));
        MarketCalc.initialiser(ancien);
        assertEquals(98, ancien.getCoefficient(MarketCalc.MINEUR));
        assertEquals(50, ancien.getCoefficient(MarketCalc.FARMEUR));
        assertEquals(100, ancien.getCoefficient(MarketCalc.AGRICULTEUR));
        assertEquals(120, ancien.getCoefficient(MarketCalc.PECHEUR));
        assertEquals(150, ancien.getCoefficient(MarketCalc.TUEUR));
        assertEquals(2000, ancien.getVolume(MarketCalc.MINEUR), 1e-9);
    }

    @Test
    void prixAvecSurplus() {
        Market m = marcheNeuf();
        MarketCalc.setSurplus(MarketCalc.AGRICULTEUR, m,
                MarketCalc.surplusPourCoefficient(50, MarketCalc.echelle(MarketCalc.AGRICULTEUR, m)));
        assertEquals(23 * 0.5, MarketCalc.getPrixActuel(MarketCalc.AGRICULTEUR, m), 0.01);
    }

    @Test
    void nomAfficheDuPecheurAvecAccent() {
        assertEquals("Pêcheur", MarketCalc.nomAffiche(MarketCalc.PECHEUR));
        assertEquals("Mineur", MarketCalc.nomAffiche(MarketCalc.MINEUR));
    }
}
