package fr.kevyn.farmland.directives.administration;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DerniereConnexionTest {

    @Test
    void dateLisibleOuInconnue() {
        long ms = LocalDateTime.of(2026, 11, 20, 14, 51).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        assertEquals("20/11/2026 à 14:51", PlayerAdminCommands.derniereConnexion(ms));
        assertEquals("inconnue", PlayerAdminCommands.derniereConnexion(0));
        assertEquals("inconnue", PlayerAdminCommands.derniereConnexion(-1));
    }
}
