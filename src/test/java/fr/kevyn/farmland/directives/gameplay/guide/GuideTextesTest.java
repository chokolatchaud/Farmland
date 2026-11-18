package fr.kevyn.farmland.directives.gameplay.guide;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuideTextesTest {

    /** Texte sans codes couleur (§x). */
    private static String sansCouleurs(String s) {
        return s.replaceAll("§.", "");
    }

    @Test
    void lesSixThemesDesHologrammes() {
        assertEquals(List.of("bienvenue", "plot", "metiers", "vendre", "depenser", "activites"),
                List.copyOf(GuideTextes.HOLOGRAMMES.keySet()));
        assertTrue(GuideTextes.themeExiste("Metiers"));
        assertFalse(GuideTextes.themeExiste("inconnu"));
        assertFalse(GuideTextes.themeExiste(null));
    }

    @Test
    void messageDuChatCourt() {
        assertTrue(GuideTextes.GUIDE_CHAT.size() <= 8, "le message de bienvenue doit rester court");
        for (String ligne : GuideTextes.GUIDE_CHAT) {
            assertTrue(sansCouleurs(ligne).length() <= 80, "ligne trop longue : " + ligne);
        }
        assertFalse(String.join(" ", GuideTextes.GUIDE_CHAT).contains("livre"), "plus de livre : le guide est un menu");
    }

    @Test
    void reglesLisiblesDansUneInfobulle() {
        for (String ligne : GuideTextes.REGLES) {
            assertTrue(sansCouleurs(ligne).length() <= 50, "ligne de règle trop longue : " + ligne);
        }
        assertTrue(String.join(" ", GuideTextes.REGLES).contains("/reportmsg"));
    }

    @Test
    void prochainObjectifSelonLaSituation() {
        assertTrue(GuideTextes.prochainObjectif(false, 0, 0, 50).contains("outil"), "nouveau joueur : prendre un outil");
        assertTrue(GuideTextes.prochainObjectif(true, 120, 0, 50).contains("Vends tes 120 jetons"));
        assertTrue(GuideTextes.prochainObjectif(false, 5, 0, 50).contains("Vends"), "des jetons à vendre passent avant l'outil");
        assertTrue(GuideTextes.prochainObjectif(true, 0, 60, 50).contains("Agrandis ton plot pour 50 $FB"));
        assertTrue(GuideTextes.prochainObjectif(true, 0, 10, 50).contains("Travaille"));
    }

    @Test
    void barreDeProgression() {
        assertEquals(10, sansCouleurs(GuideMenu.barre(0, 100)).length());
        assertEquals("§a" + "■".repeat(5) + "§8" + "■".repeat(5), GuideMenu.barre(50, 100));
        assertEquals("§a" + "■".repeat(10) + "§8", GuideMenu.barre(500, 100));
        assertEquals("§a§8" + "■".repeat(10), GuideMenu.barre(5, 0));
    }
}
