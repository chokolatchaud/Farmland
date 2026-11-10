package fr.kevyn.farmland.directives.infrastructure.journal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegroupementTest {

    @Test
    void troisBonusDeviennentUneLigne() {
        Regroupement r = new Regroupement();
        r.ajouter("bonus_blocs", "Alice", 25, 150, "blocs");
        r.ajouter("bonus_blocs", "Alice", 25, 150, "blocs");
        r.ajouter("bonus_blocs", "Alice", 25, 150, "blocs");

        List<Regroupement.Ligne> lignes = r.vider();
        assertEquals(1, lignes.size());
        assertEquals(new Regroupement.Ligne("bonus_blocs", "Alice", "+75 $FB (450 blocs)"), lignes.get(0));
        assertTrue(r.vider().isEmpty(), "vider remet à zéro");
    }

    @Test
    void unCumulParJoueur() {
        Regroupement r = new Regroupement();
        r.ajouter("bonus_blocs", "Alice", 25, 150, "blocs");
        r.ajouter("bonus_blocs", "Bob", 25, 150, "blocs");
        assertEquals(2, r.vider().size());
    }

    @Test
    void viderJoueurNeTouchePasAuxAutres() {
        Regroupement r = new Regroupement();
        r.ajouter("bonus_blocs", "Alice", 25, 150, "blocs");
        r.ajouter("bonus_blocs", "Bob", 25, 150, "blocs");

        List<Regroupement.Ligne> alice = r.viderJoueur("Alice");
        assertEquals(1, alice.size());
        assertEquals("Alice", alice.get(0).joueur());

        List<Regroupement.Ligne> reste = r.vider();
        assertEquals(1, reste.size());
        assertEquals("Bob", reste.get(0).joueur());
    }

    @Test
    void ligneFinaleAuFormatJournal() {
        Regroupement r = new Regroupement();
        r.ajouter("bonus_blocs", "Alice", 25, 150, "blocs");
        Regroupement.Ligne l = r.vider().get(0);
        assertEquals(";bonus_blocs;Alice;+25 $FB (150 blocs)",
                FormatJournal.ligne(null, l.type(), l.joueur(), l.valeur()));
    }

    @Test
    void argumentsNuls() {
        Regroupement r = new Regroupement();
        r.ajouter(null, null, 25, 150, null);
        assertEquals(1, r.viderJoueur(null).size());
    }
}
