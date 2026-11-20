package fr.kevyn.farmland.directives.plot.jaime;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaimeCalcTest {

    private static final UUID A = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    private static final UUID B = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    private static final UUID C = UUID.fromString("00000000-0000-0000-0000-00000000000c");
    private static final LocalDate JOUR = LocalDate.of(2026, 11, 20);

    @Test
    void unJaimeParPlotEtParJour() {
        JaimeCalc.Donnees plotDeA = new JaimeCalc.Donnees();
        assertEquals(JaimeCalc.Reponse.OK, JaimeCalc.aimer(plotDeA, B, A, JOUR));
        assertEquals(JaimeCalc.Reponse.DEJA_AUJOURDHUI, JaimeCalc.aimer(plotDeA, B, A, JOUR));
        assertEquals(1, plotDeA.getTotal());
        assertEquals(JaimeCalc.Reponse.OK, JaimeCalc.aimer(plotDeA, C, A, JOUR), "un autre joueur peut aimer");
        assertEquals(JaimeCalc.Reponse.OK, JaimeCalc.aimer(plotDeA, B, A, JOUR.plusDays(1)), "le lendemain, à nouveau");
        assertEquals(3, plotDeA.getTotal());
    }

    @Test
    void jamaisSonProprePlot() {
        JaimeCalc.Donnees plotDeA = new JaimeCalc.Donnees();
        assertEquals(JaimeCalc.Reponse.PROPRE_PLOT, JaimeCalc.aimer(plotDeA, A, A, JOUR));
        assertEquals(0, plotDeA.getTotal());
    }

    @Test
    void votesAnciensOubliesMaisTotalGarde() {
        JaimeCalc.Donnees plot = new JaimeCalc.Donnees();
        JaimeCalc.aimer(plot, B, A, JOUR);
        JaimeCalc.aimer(plot, C, A, JOUR.plusDays(1));
        JaimeCalc.oublierVotesAnciens(plot, JOUR.plusDays(1));
        assertEquals(Map.of(C.toString(), JOUR.plusDays(1).toString()), plot.votes);
        assertEquals(2, plot.getTotal());
        assertEquals(JaimeCalc.Reponse.DEJA_AUJOURDHUI, JaimeCalc.aimer(plot, C, A, JOUR.plusDays(1)));

        JaimeCalc.Donnees lu = new JaimeCalc.Donnees();
        lu.votes = null; // fichier sans votes
        assertEquals(JaimeCalc.Reponse.OK, JaimeCalc.aimer(lu, B, A, JOUR));
    }

    @Test
    void classementDuPlusAime() {
        Map<UUID, JaimeCalc.Donnees> plots = new HashMap<>();
        plots.put(A, avec(2));
        plots.put(B, avec(7));
        plots.put(C, avec(0));
        assertEquals(List.of(B, A), JaimeCalc.classement(plots), "trié, sans les plots à 0");
        assertTrue(JaimeCalc.classement(new HashMap<>()).isEmpty());
    }

    @Test
    void texte() {
        assertEquals("❤ 1 j'aime", JaimeCalc.texte(1));
        assertEquals("❤ 12 j'aime", JaimeCalc.texte(12));
    }

    private static JaimeCalc.Donnees avec(int total) {
        JaimeCalc.Donnees d = new JaimeCalc.Donnees();
        d.total = total;
        return d;
    }
}
