package fr.kevyn.farmland.directives.plot.une;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UneCalcTest {

    private static final UUID A = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    private static final UUID B = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    private static final LocalDate JEUDI = LocalDate.of(2026, 11, 19);

    private static UneCalc.Donnees semaineDu(LocalDate jour) {
        UneCalc.Donnees d = new UneCalc.Donnees();
        UneCalc.verifierSemaine(d, jour);
        return d;
    }

    @Test
    void exempleDuProprietaire() {
        // j'ai mis 100k, un autre a mis 200k : je me retrouve 2e
        UneCalc.Donnees d = semaineDu(JEUDI);
        assertEquals(UneCalc.Reponse.OK, UneCalc.miser(d, A, 100_000, 150_000));
        assertEquals(UneCalc.Reponse.OK, UneCalc.miser(d, B, 200_000, 500_000));
        assertEquals(List.of(B, A), UneCalc.classement(d));
        assertEquals(2, UneCalc.rang(d, A));
        // les mises s'additionnent : A remet 150k et repasse devant
        assertEquals(UneCalc.Reponse.OK, UneCalc.miser(d, A, 150_000, 150_000));
        assertEquals(250_000, UneCalc.mise(d, A));
        assertEquals(1, UneCalc.rang(d, A));
    }

    @Test
    void miseRefusee() {
        UneCalc.Donnees d = semaineDu(JEUDI);
        assertEquals(UneCalc.Reponse.MONTANT_TROP_PETIT, UneCalc.miser(d, A, 999, 1_000_000));
        assertEquals(UneCalc.Reponse.MONTANT_TROP_PETIT, UneCalc.miser(d, A, -5_000, 1_000_000));
        assertEquals(UneCalc.Reponse.SOLDE_INSUFFISANT, UneCalc.miser(d, A, 10_001, 10_000));
        assertEquals(0, UneCalc.mise(d, A), "rien enregistré");
        assertEquals(0, UneCalc.rang(d, A));
        assertTrue(UneCalc.classement(d).isEmpty());
    }

    @Test
    void remiseAZeroLeLundi() {
        UneCalc.Donnees d = semaineDu(JEUDI);
        UneCalc.miser(d, A, 5_000, 5_000);
        UneCalc.miser(d, B, 9_000, 9_000);
        assertNull(UneCalc.verifierSemaine(d, JEUDI.plusDays(3)), "dimanche : même semaine");
        assertEquals(9_000, UneCalc.mise(d, B));

        UneCalc.FinDeSemaine fin = UneCalc.verifierSemaine(d, JEUDI.plusDays(4)); // lundi
        assertNotNull(fin);
        assertEquals(B, fin.gagnant());
        assertEquals(9_000, fin.mise());
        assertTrue(UneCalc.classement(d).isEmpty());
        assertNull(UneCalc.verifierSemaine(d, JEUDI.plusDays(11)), "semaine sans mise : pas de gagnant");
        assertEquals(UneCalc.semaine(JEUDI.plusDays(11)), d.semaine);
    }

    @Test
    void semaineIso() {
        assertEquals("2026-W47", UneCalc.semaine(JEUDI));
        assertEquals(UneCalc.semaine(LocalDate.of(2026, 11, 16)), UneCalc.semaine(LocalDate.of(2026, 11, 22)));
        assertEquals("2026-W53", UneCalc.semaine(LocalDate.of(2027, 1, 1)), "début janvier rattaché à la semaine ISO");
    }

    @Test
    void textes() {
        assertEquals("200 000", UneCalc.montant(200_000));
        assertEquals("3 j 0 h", UneCalc.tempsAvantLundi(LocalDateTime.of(2026, 11, 20, 0, 0)));
        assertEquals("1 h 30 min", UneCalc.tempsAvantLundi(LocalDateTime.of(2026, 11, 22, 22, 30)));
        assertEquals(10_000, UneCalc.lireMontant("10000"));
        assertEquals(10_000, UneCalc.lireMontant("10k"));
        assertEquals(1_500_000, UneCalc.lireMontant("1,5m"));
        assertEquals(-1, UneCalc.lireMontant("abc"));
        assertEquals(-1, UneCalc.lireMontant("-500"));
        assertEquals(-1, UneCalc.lireMontant("1.5"));
        assertEquals(-1, UneCalc.lireMontant(""));
    }
}
