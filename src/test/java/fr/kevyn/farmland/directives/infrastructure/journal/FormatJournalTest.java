package fr.kevyn.farmland.directives.infrastructure.journal;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatJournalTest {

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 11, 10, 18, 5, 3);

    @Test
    void formatSimple() {
        assertEquals("2026-11-10 18:05:03;pay;Alice;-50 $FB vers Bob",
                FormatJournal.ligne(DATE, "pay", "Alice", "-50 $FB vers Bob"));
    }

    @Test
    void separateurEtRetoursNeutralises() {
        String l = FormatJournal.ligne(DATE, "ty;pe", "A;b", "x;y\nz\r");
        assertEquals("2026-11-10 18:05:03;ty,pe;A,b;x,y z", l);
        assertEquals(4, l.split(";", -1).length);
    }

    @Test
    void valeursNulles() {
        assertEquals(";inconnu;-;", FormatJournal.ligne(null, null, null, null));
        assertEquals("2026-11-10 18:05:03;marche_recalcul;-;Mineur 100->98",
                FormatJournal.ligne(DATE, "marche_recalcul", "  ", "Mineur 100->98"));
    }

    @Test
    void valeurDontToStringPlante() {
        Object piege = new Object() {
            @Override
            public String toString() {
                throw new IllegalStateException("boum");
            }
        };
        assertEquals("2026-11-10 18:05:03;t;j;?", FormatJournal.ligne(DATE, "t", "j", piege));
    }

    @Test
    void nomArchive() {
        assertEquals("evenements-2026-11-09.log", FormatJournal.nomArchive(LocalDate.of(2026, 11, 9)));
    }

    @Test
    void archiveASupprimerSeulementLesVieillesArchives() {
        LocalDate auj = LocalDate.of(2026, 11, 10);
        assertTrue(FormatJournal.archiveASupprimer("evenements-2026-10-10.log", auj, 30));  // 31 jours
        assertFalse(FormatJournal.archiveASupprimer("evenements-2026-10-11.log", auj, 30)); // 30 jours : gardée
        assertFalse(FormatJournal.archiveASupprimer("evenements-2026-11-09.log", auj, 30));
        // autres fichiers : jamais
        assertFalse(FormatJournal.archiveASupprimer("evenements.log", auj, 30));
        assertFalse(FormatJournal.archiveASupprimer("evenements-2020-01-01.log.bak", auj, 30));
        assertFalse(FormatJournal.archiveASupprimer("evenements-2020-13-45.log", auj, 30));
        assertFalse(FormatJournal.archiveASupprimer("autre-2020-01-01.log", auj, 30));
        assertFalse(FormatJournal.archiveASupprimer("market_history.json", auj, 30));
        assertFalse(FormatJournal.archiveASupprimer(null, auj, 30));
    }
}
