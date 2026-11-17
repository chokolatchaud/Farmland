package fr.kevyn.farmland.directives.metiers.pecheur;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventPecheTest {

    @Test
    void uneprisevautSeptActions() {
        assertEquals(7, EventPeche.MULTIPLICATEUR_PECHE);
        assertEquals(70, EventPeche.XP_PAR_ACTION * EventPeche.MULTIPLICATEUR_PECHE);
    }

    @Test
    void niveau0Ou1DonneExactementSeptJetons() {
        assertEquals(7, EventPeche.jetonsPeche(0));
        assertEquals(7, EventPeche.jetonsPeche(1));
    }

    @Test
    void jetonsEntreSeptEtSeptFoisLeNiveauEtMoyenneSeptFoisPlusGrande() {
        int niveau = 10;
        long somme = 0;
        int n = 20_000;
        for (int i = 0; i < n; i++) {
            int j = EventPeche.jetonsPeche(niveau);
            assertTrue(j >= 7 && j <= 7 * niveau, "hors bornes : " + j);
            somme += j;
        }
        // moyenne d'un tirage au niveau 10 = (10 + 2) / 3 = 4, donc 28 pour 7 tirages
        assertEquals(28.0, (double) somme / n, 0.5);
    }
}
