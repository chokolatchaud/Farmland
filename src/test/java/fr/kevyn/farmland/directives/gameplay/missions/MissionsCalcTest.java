package fr.kevyn.farmland.directives.gameplay.missions;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissionsCalcTest {

    private static final UUID JOUEUR = UUID.fromString("0f6b1c9e-4d1a-4c47-9a35-0d9a3a7f1b22");
    private static final LocalDate JOUR = LocalDate.of(2026, 11, 20);
    private static final MissionsCalc.Recompenses R = MissionsCalc.Recompenses.PAR_DEFAUT;

    @Test
    void tirageStableDansLaJournee() {
        assertEquals(MissionsCalc.tirer(JOUEUR, JOUR), MissionsCalc.tirer(JOUEUR, JOUR));
    }

    @Test
    void troisTypesDifferentsUneDeChaqueDifficulte() {
        for (int j = 0; j < 200; j++) {
            List<MissionsCalc.Mission> m = MissionsCalc.tirer(UUID.randomUUID(), JOUR.plusDays(j));
            assertEquals(3, m.size());
            Set<TypeMission> types = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                types.add(m.get(i).type());
                assertEquals(Difficulte.values()[i], m.get(i).difficulte());
                assertEquals(m.get(i).type().objectif(m.get(i).difficulte()), m.get(i).objectif());
            }
            assertEquals(3, types.size(), "3 types différents");
        }
    }

    @Test
    void lesMissionsChangentSelonLeJourEtLeJoueur() {
        Set<List<MissionsCalc.Mission>> differentes = new HashSet<>();
        for (int j = 0; j < 30; j++) differentes.add(MissionsCalc.tirer(JOUEUR, JOUR.plusDays(j)));
        assertTrue(differentes.size() > 10, "les missions doivent varier d'un jour à l'autre");
        assertNotEquals(MissionsCalc.tirer(JOUEUR, JOUR), MissionsCalc.tirer(UUID.fromString("11111111-2222-3333-4444-555555555555"), JOUR));
    }

    @Test
    void tousLesTypesSortentUnJourOuLAutre() {
        Set<TypeMission> vus = new HashSet<>();
        for (int j = 0; j < 100; j++) MissionsCalc.tirer(JOUEUR, JOUR.plusDays(j)).forEach(m -> vus.add(m.type()));
        assertEquals(Set.of(TypeMission.values()), vus);
    }

    @Test
    void objectifsCroissantsSelonLaDifficulte() {
        for (TypeMission t : TypeMission.values()) {
            assertTrue(t.objectif(Difficulte.FACILE) > 0);
            assertTrue(t.objectif(Difficulte.FACILE) < t.objectif(Difficulte.MOYEN), t.name());
            assertTrue(t.objectif(Difficulte.MOYEN) < t.objectif(Difficulte.DIFFICILE), t.name());
        }
    }

    @Test
    void nouvelleProgressionAMinuit() {
        ProgressionJour hier = new ProgressionJour(JOUR.minusDays(1).toString());
        hier.progression[0] = 50;
        ProgressionJour aujourdhui = MissionsCalc.duJour(hier, JOUR);
        assertEquals(JOUR.toString(), aujourdhui.getDate());
        assertEquals(0, aujourdhui.getProgression(0));
        assertSame(aujourdhui, MissionsCalc.duJour(aujourdhui, JOUR), "même jour : même progression");
        assertEquals(JOUR.toString(), MissionsCalc.duJour(null, JOUR).getDate());
    }

    @Test
    void fichierAbimeRepartDeZero() {
        ProgressionJour abime = new ProgressionJour(JOUR.toString());
        abime.progression = new int[1];
        ProgressionJour p = MissionsCalc.duJour(abime, JOUR);
        assertEquals(3, p.progression.length);
    }

    @Test
    void progressionPlafonneeEtPayeeUneSeuleFois() {
        List<MissionsCalc.Mission> m = List.of(
                new MissionsCalc.Mission(TypeMission.MINEUR, Difficulte.FACILE, 250),
                new MissionsCalc.Mission(TypeMission.PECHEUR, Difficulte.MOYEN, 80),
                new MissionsCalc.Mission(TypeMission.COURSE, Difficulte.DIFFICILE, 3));
        ProgressionJour p = new ProgressionJour(JOUR.toString());

        assertTrue(MissionsCalc.avancer(p, m, TypeMission.MINEUR, 249).rien());
        assertTrue(MissionsCalc.avancer(p, m, TypeMission.FARMEUR, 1000).rien(), "un type absent ne compte pas");

        MissionsCalc.Resultat r = MissionsCalc.avancer(p, m, TypeMission.MINEUR, 10);
        assertEquals(List.of(0), r.terminees());
        assertFalse(r.bonus());
        assertEquals(250, p.getProgression(0), "jamais au-delà de l'objectif");
        assertEquals(100, MissionsCalc.gain(r, m, R));

        assertTrue(MissionsCalc.avancer(p, m, TypeMission.MINEUR, 500).rien(), "déjà payée");
        assertTrue(MissionsCalc.avancer(p, m, TypeMission.PECHEUR, 0).rien());
        assertTrue(MissionsCalc.avancer(p, m, TypeMission.PECHEUR, -5).rien());
        assertEquals(0, p.getProgression(1));
    }

    @Test
    void bonusQuandLesTroisSontFaites() {
        List<MissionsCalc.Mission> m = MissionsCalc.tirer(JOUEUR, JOUR);
        ProgressionJour p = new ProgressionJour(JOUR.toString());
        int total = 0;
        for (MissionsCalc.Mission mission : m) {
            MissionsCalc.Resultat r = MissionsCalc.avancer(p, m, mission.type(), Integer.MAX_VALUE);
            total += MissionsCalc.gain(r, m, R);
        }
        assertEquals(3, p.nombreFaites());
        assertTrue(p.bonusDonne());
        assertEquals(R.totalMaximum(), total);
        assertEquals(900, R.totalMaximum());
        for (TypeMission t : TypeMission.values()) {
            assertTrue(MissionsCalc.avancer(p, m, t, 1000).rien(), "plus rien à gagner aujourd'hui");
        }
    }

    @Test
    void textes() {
        assertEquals("Mine 250 blocs", TypeMission.MINEUR.description(250));
        assertEquals("Termine 1 course de bateaux", TypeMission.COURSE.description(1));
        assertEquals("Termine 3 courses de bateaux", TypeMission.COURSE.description(3));
        assertEquals("5 h 0 min", MissionsCalc.tempsAvantMinuit(19 * 3600));
        assertEquals("1 min", MissionsCalc.tempsAvantMinuit(24 * 3600 - 10));
        assertEquals("23 h 59 min", MissionsCalc.tempsAvantMinuit(60));
    }
}
