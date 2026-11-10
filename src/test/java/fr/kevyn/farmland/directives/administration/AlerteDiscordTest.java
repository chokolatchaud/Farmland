package fr.kevyn.farmland.directives.administration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlerteDiscordTest {

    private static final long HEURE = AlerteDiscord.INTERVALLE_RAPPEL_MS;

    @BeforeEach
    void reset() {
        AlerteDiscord.reinitialiser();
    }

    @Test
    void premiereFoisEcrite() {
        assertEquals("msg", AlerteDiscord.aEcrire("http:statut:404", "msg", 0));
    }

    @Test
    void silenceAvantUneHeure() {
        AlerteDiscord.aEcrire("k", "msg", 0);
        assertNull(AlerteDiscord.aEcrire("k", "msg", 1000));
        assertNull(AlerteDiscord.aEcrire("k", "msg", HEURE - 1));
    }

    @Test
    void rappelApresUneHeureAvecCompteur() {
        AlerteDiscord.aEcrire("k", "msg", 0);
        AlerteDiscord.aEcrire("k", "msg", 10);
        AlerteDiscord.aEcrire("k", "msg", 20);
        String rappel = AlerteDiscord.aEcrire("k", "msg", HEURE);
        assertTrue(rappel.startsWith("msg"));
        assertTrue(rappel.contains("2 autre(s) occurrence(s)"));
        // le rappel suivant repart d'une heure après ce rappel
        assertNull(AlerteDiscord.aEcrire("k", "msg", HEURE + 10));
    }

    @Test
    void clesIndependantes() {
        AlerteDiscord.aEcrire("non-configure:statut", "a", 0);
        assertEquals("b", AlerteDiscord.aEcrire("non-configure:message", "b", 5));
    }
}
