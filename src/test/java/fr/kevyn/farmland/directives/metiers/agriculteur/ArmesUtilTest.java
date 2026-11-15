package fr.kevyn.farmland.directives.metiers.agriculteur;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArmesUtilTest {

    @Test
    void degatsMontentDeUnParNiveau() {
        assertEquals(5.0, ArmesUtil.calculerDegats(0));
        assertEquals(6.0, ArmesUtil.calculerDegats(1));
        assertEquals(15.0, ArmesUtil.calculerDegats(10));
    }

    @Test
    void degatsPlafonnesAuNiveauPlafond() {
        // Plafond réel : niveau 13 -> 5 + 13 = 18 dégâts (DEGATS_MAX = 20 n'est jamais atteint)
        assertEquals(18.0, ArmesUtil.calculerDegats(ArmesUtil.NIVEAU_PLAFOND_DEGATS));
        assertEquals(18.0, ArmesUtil.calculerDegats(14));
        assertEquals(18.0, ArmesUtil.calculerDegats(1000));
    }

    @Test
    void niveauNegatifTraiteCommeZero() {
        assertEquals(5.0, ArmesUtil.calculerDegats(-3));
    }

    @Test
    void niveauExcedentaire() {
        assertEquals(0, ArmesUtil.niveauExcedentaire(0));
        assertEquals(0, ArmesUtil.niveauExcedentaire(13));
        assertEquals(1, ArmesUtil.niveauExcedentaire(14));
        assertEquals(87, ArmesUtil.niveauExcedentaire(100));
    }
}
