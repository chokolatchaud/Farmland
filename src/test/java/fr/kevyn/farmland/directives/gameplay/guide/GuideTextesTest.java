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
    void guideDuChatCourt() {
        assertTrue(GuideTextes.GUIDE_CHAT.size() <= 10, "/guide doit rester court");
        for (String ligne : GuideTextes.GUIDE_CHAT) {
            assertTrue(sansCouleurs(ligne).length() <= 80, "ligne trop longue : " + ligne);
        }
    }

    @Test
    void pagesDuLivreLisibles() {
        // Un livre Minecraft : 100 pages max ; une page affiche ~14 lignes de ~19 caractères.
        assertTrue(GuideTextes.PAGES_LIVRE.size() <= 100);
        for (int i = 0; i < GuideTextes.PAGES_LIVRE.size(); i++) {
            String page = sansCouleurs(GuideTextes.PAGES_LIVRE.get(i));
            assertTrue(page.length() <= 300, "page " + (i + 1) + " trop longue : " + page.length() + " caractères");
            assertFalse(page.isBlank(), "page " + (i + 1) + " vide");
        }
    }

    /**
     * Estimation des lignes affichées : une page de livre montre 14 lignes
     * d'environ 19 caractères, avec retour à la ligne entre les mots.
     */
    static int lignesAffichees(String page) {
        int lignes = 0;
        for (String ligne : sansCouleurs(page).split("\n", -1)) {
            int largeur = 0;
            int compte = 1;
            for (String mot : ligne.split(" ")) {
                int ajout = mot.length() + (largeur > 0 ? 1 : 0);
                if (largeur > 0 && largeur + ajout > 19) {
                    compte++;
                    largeur = mot.length();
                } else {
                    largeur += ajout;
                }
            }
            lignes += compte;
        }
        return lignes;
    }

    @Test
    void chaquePageTientDansLeLivre() {
        StringBuilder tropLongues = new StringBuilder();
        for (int i = 0; i < GuideTextes.PAGES_LIVRE.size(); i++) {
            int lignes = lignesAffichees(GuideTextes.PAGES_LIVRE.get(i));
            if (lignes > 14) tropLongues.append("page ").append(i + 1).append(" : ").append(lignes).append(" lignes ; ");
        }
        assertTrue(tropLongues.isEmpty(), "max 14 lignes par page -> " + tropLongues);
    }

    @Test
    void chiffresDuGuideAlignesSurLeCode() {
        String livre = String.join("\n", GuideTextes.PAGES_LIVRE);
        assertTrue(livre.contains("200 $FB"), "prix de /buy worldedit (BuyCommands.WE_COST)");
        assertTrue(livre.contains("75 $FB"), "gain du calcul du chat");
        assertTrue(livre.contains("+25 $FB"), "bonus 150 blocs posés");
        assertTrue(livre.contains("500 / 250 / 100 $FB"), "podium de la course");
    }
}
