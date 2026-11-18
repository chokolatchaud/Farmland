package fr.kevyn.farmland.directives.gameplay.menus;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OutilsModeleTest {

    @Test
    void modeleDeChaqueOutilDeMetier() {
        assertEquals("custom:outil_mineur", Outils.modeleAttendu(Material.NETHERITE_PICKAXE, false));
        assertEquals("custom:outil_farmeur", Outils.modeleAttendu(Material.NETHERITE_HOE, false));
        assertEquals("custom:outil_pecheur", Outils.modeleAttendu(Material.FISHING_ROD, false));
        assertEquals("custom:outil_agriculteur", Outils.modeleAttendu(Material.NETHERITE_AXE, false));
        assertEquals("custom:outil_tueur", Outils.modeleAttendu(Material.NETHERITE_SWORD, false));
    }

    @Test
    void outilDeMaitre() {
        assertEquals("custom:outil_mineur_maitre", Outils.modeleAttendu(Material.NETHERITE_PICKAXE, true));
        assertEquals("custom:outil_tueur_maitre", Outils.modeleAttendu(Material.NETHERITE_SWORD, true));
    }

    @Test
    void pasUnOutilDeMetier() {
        assertNull(Outils.modeleAttendu(Material.IRON_PICKAXE, false));
        assertNull(Outils.modeleAttendu(Material.BOW, true));
    }
}
