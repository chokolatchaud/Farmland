package fr.kevyn.farmland.directives.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlyphesTest {

    @Test
    void codesFixesDuResourcePack() {
        // Doivent rester alignés sur ressourcepackgenerator/glyphes/glyphes.json
        assertEquals(0xE000, Glyphes.PIECE.charAt(0));
        assertEquals(0xE001, Glyphes.MINEUR.charAt(0));
        assertEquals(0xE002, Glyphes.FARMEUR.charAt(0));
        assertEquals(0xE003, Glyphes.PECHEUR.charAt(0));
        assertEquals(0xE004, Glyphes.AGRICULTEUR.charAt(0));
        assertEquals(0xE005, Glyphes.TUEUR.charAt(0));
    }

    @Test
    void argentAvecLaPieceEnBlanc() {
        assertEquals("§6250 §f\uE000§6", Glyphes.argent(250, "§6"));
    }

    @Test
    void iconeDeChaqueMetier() {
        assertEquals(Glyphes.MINEUR, Glyphes.metier(JobType.MINEUR));
        assertEquals(Glyphes.TUEUR, Glyphes.metier(JobType.TUEUR));
    }
}
