package fr.kevyn.farmland.directives.cosmetic.titres;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TitresCalcTest {

    @Test
    void achatSeulementAvecLeSolde() {
        TitresCalc.Donnees d = new TitresCalc.Donnees();
        assertEquals(TitresCalc.Achat.SOLDE_INSUFFISANT, TitresCalc.acheter(d, Titre.MAGNAT, 24_999));
        assertFalse(TitresCalc.possede(d, Titre.MAGNAT, 0), "rien enregistré sans l'argent");
        assertEquals(TitresCalc.Achat.OK, TitresCalc.acheter(d, Titre.MAGNAT, 25_000));
        assertTrue(TitresCalc.possede(d, Titre.MAGNAT, 0));
        assertEquals(TitresCalc.Achat.DEJA_POSSEDE, TitresCalc.acheter(d, Titre.MAGNAT, 1_000_000), "jamais payé deux fois");
    }

    @Test
    void titresAuMeriteNonAchetables() {
        TitresCalc.Donnees d = new TitresCalc.Donnees();
        assertEquals(TitresCalc.Achat.AU_MERITE, TitresCalc.acheter(d, Titre.MAITRE_MINEUR, 1_000_000));
        assertFalse(TitresCalc.possede(d, Titre.MAITRE_MINEUR, 49));
        assertTrue(TitresCalc.possede(d, Titre.MAITRE_MINEUR, 50));
        assertEquals(TitresCalc.Equipement.NON_POSSEDE, TitresCalc.equiper(d, Titre.MAITRE_MINEUR, 10));
        assertEquals(TitresCalc.Equipement.OK, TitresCalc.equiper(d, Titre.MAITRE_MINEUR, 50));
    }

    @Test
    void prefixeDuChat() {
        TitresCalc.Donnees d = new TitresCalc.Donnees();
        assertEquals("", TitresCalc.prefixe(d, 0));
        assertEquals("", TitresCalc.prefixe(null, 0));
        TitresCalc.acheter(d, Titre.BATISSEUR, 2_000);
        assertEquals(TitresCalc.Equipement.OK, TitresCalc.equiper(d, Titre.BATISSEUR, 0));
        assertEquals("§a[Bâtisseur]§r ", TitresCalc.prefixe(d, 0));
        d.actif = "TITRE_SUPPRIME";
        assertEquals("", TitresCalc.prefixe(d, 0), "titre inconnu : rien");
        d.actif = Titre.LEGENDE.name();
        assertEquals("", TitresCalc.prefixe(d, 0), "titre porté mais pas possédé : rien");
    }

    @Test
    void catalogue() {
        Set<String> affichages = new HashSet<>();
        for (Titre t : Titre.values()) {
            assertTrue(affichages.add(t.getAffichage()), "affichage en double : " + t);
            assertEquals(t.auMerite(), t.getPrix() == 0, t.name());
            assertFalse(t.getAffichage().contains("%"), "le format du chat ne supporte pas %");
        }
        assertNull(Titre.depuisNom(null));
        assertEquals(Titre.MAGNAT, Titre.depuisNom("MAGNAT"));
    }
}
