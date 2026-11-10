package fr.kevyn.farmland.directives.infrastructure.journal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JournalEvenementsTest {

    @BeforeEach
    @AfterEach
    void reset() {
        JournalEvenements.viderPourTest();
    }

    @Test
    void neLeveJamaisAvecArgumentsNuls() {
        assertDoesNotThrow(() -> {
            JournalEvenements.enregistrer(null, null, null);
            JournalEvenements.enregistrerDetail(null, null, null);
            JournalEvenements.cumuler(null, null, 0, 0, null);
            JournalEvenements.finJoueur(null);
            JournalEvenements.arreter();
        });
        String l = JournalEvenements.prochaineLigneEnAttente();
        assertTrue(l.endsWith(";inconnu;-;"), l);
    }

    @Test
    void neLeveJamaisSiLaValeurPlante() {
        Object piege = new Object() {
            @Override
            public String toString() {
                throw new IllegalStateException("boum");
            }
        };
        assertDoesNotThrow(() -> JournalEvenements.enregistrer("t", "j", piege));
        assertTrue(JournalEvenements.prochaineLigneEnAttente().endsWith(";t;j;?"));
    }

    @Test
    void detailIgnoreAuNiveauNormal() {
        JournalEvenements.definirNiveauPourTest(true, false);
        JournalEvenements.enregistrerDetail("plot_meteo", "Alice", "nuit");
        assertNull(JournalEvenements.prochaineLigneEnAttente());

        JournalEvenements.definirNiveauPourTest(true, true);
        JournalEvenements.enregistrerDetail("plot_meteo", "Alice", "nuit");
        assertTrue(JournalEvenements.prochaineLigneEnAttente().endsWith(";plot_meteo;Alice;nuit"));
    }

    @Test
    void rienQuandInactif() {
        JournalEvenements.definirNiveauPourTest(false, false);
        JournalEvenements.enregistrer("pay", "Alice", "-5");
        assertNull(JournalEvenements.prochaineLigneEnAttente());
    }

    @Test
    void finJoueurEcritSesCumuls() {
        JournalEvenements.cumuler("bonus_blocs", "Alice", 25, 150, "blocs");
        JournalEvenements.cumuler("bonus_blocs", "Alice", 25, 150, "blocs");
        JournalEvenements.finJoueur("Alice");
        assertTrue(JournalEvenements.prochaineLigneEnAttente().endsWith(";bonus_blocs;Alice;+50 $FB (300 blocs)"));
        assertNull(JournalEvenements.prochaineLigneEnAttente());
    }

    @Test
    void purgeNeSupprimeQueLesVieillesArchives(@TempDir Path dossier) throws IOException {
        String[] aSupprimer = { "evenements-2026-09-01.log", "evenements-2026-10-10.log" };
        String[] aGarder = { "evenements-2026-10-11.log", "evenements-2026-11-09.log", "evenements.log",
                "evenements-2020-01-01.log.bak", "market_history.json", "config.yml", "autre-2020-01-01.log" };
        for (String n : aSupprimer) Files.writeString(dossier.resolve(n), "x");
        for (String n : aGarder) Files.writeString(dossier.resolve(n), "x");
        Files.createDirectory(dossier.resolve("evenements-2020-01-02.log")); // dossier : jamais supprimé

        int nb = JournalEvenements.purger(dossier.toFile(), LocalDate.of(2026, 11, 10), 30);

        assertEquals(2, nb);
        for (String n : aSupprimer) assertFalse(new File(dossier.toFile(), n).exists(), n);
        for (String n : aGarder) assertTrue(new File(dossier.toFile(), n).exists(), n);
        assertTrue(Files.isDirectory(dossier.resolve("evenements-2020-01-02.log")));
    }
}
