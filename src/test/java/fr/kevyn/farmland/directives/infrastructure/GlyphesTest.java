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

    @Test
    void fondsDesMenusAlignesSurLaPolice() {
        // Doivent rester alignés sur glyphes.json : fond_guide E100, fond_sac E101, fond_vente E102, espaces F001/F002
        assertEquals(0xE100, Glyphes.FOND_GUIDE.charAt(0));
        assertEquals(0xE101, Glyphes.FOND_SAC.charAt(0));
        assertEquals(0xE102, Glyphes.FOND_VENTE.charAt(0));
        String titre = Glyphes.titreAvecFond(Glyphes.FOND_SAC, "Sac");
        assertEquals("§f§8Sac", titre);
    }

    @Test
    void fondsDeTousLesMenus() {
        // Alignés sur glyphes.json : E103 à E108
        assertEquals(0xE103, Glyphes.FOND_GUIDE_PAGE.charAt(0));
        assertEquals(0xE104, Glyphes.FOND_COSMETIQUES.charAt(0));
        assertEquals(0xE105, Glyphes.FOND_METIERS.charAt(0));
        assertEquals(0xE106, Glyphes.FOND_CONFIG.charAt(0));
        assertEquals(0xE107, Glyphes.FOND_GRILLE.charAt(0));
        assertEquals(0xE108, Glyphes.FOND_AGRANDIR.charAt(0));
    }
}
