package fr.kevyn.farmland.directives.plot.interfaces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheProfilsTest {

    private static final long ATTENTE = CacheProfils.ATTENTE_APRES_ECHEC_MS;

    @Test
    void sansEchecOnPeutDemander() {
        assertTrue(CacheProfils.peutDemander(null, 1_000));
    }

    @Test
    void apresUnEchecOnAttend30Minutes() {
        assertFalse(CacheProfils.peutDemander(1_000L, 1_000));
        assertFalse(CacheProfils.peutDemander(1_000L, 1_000 + ATTENTE - 1));
        assertTrue(CacheProfils.peutDemander(1_000L, 1_000 + ATTENTE));
    }
}
