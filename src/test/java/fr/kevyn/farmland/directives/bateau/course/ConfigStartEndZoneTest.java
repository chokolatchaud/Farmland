package fr.kevyn.farmland.directives.bateau.course;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConfigStartEndZoneTest {

    @Test
    void pluriel() {
        assertEquals("§eDépart dans 5 secondes", ConfigStartEndZone.messageDecompte(5));
        assertEquals("§eDépart dans 30 secondes", ConfigStartEndZone.messageDecompte(30));
    }

    @Test
    void singulier() {
        assertEquals("§eDépart dans 1 seconde", ConfigStartEndZone.messageDecompte(1));
    }

    @Test
    void rienAZero() {
        assertNull(ConfigStartEndZone.messageDecompte(0));
        assertNull(ConfigStartEndZone.messageDecompte(-1));
    }

    @Test
    void secondesAnnoncees() {
        StringBuilder annoncees = new StringBuilder();
        for (int s = 30; s >= 0; s--) {
            if (ConfigStartEndZone.messageDecompte(s) != null) annoncees.append(s).append(' ');
        }
        assertEquals("30 20 10 5 4 3 2 1 ", annoncees.toString());
    }
}
