package fr.kevyn.farmland.Farming;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

/**
 * Suit les positions ou une culture a ete plantee LEGITIMEMENT (via le Planteur,
 * pas posee/collee par un joueur ou WorldEdit). Meme principe que le suivi de
 * position du Mineur (positionsLegitimes) - une culture qui n'est jamais passee
 * par cette liste ne pourra jamais etre "recoltee" (donner une ressource au /bag),
 * meme si elle arrive a maturite d'une autre maniere (triche).
 */
public class PositionsLegitimesFarm {

    private static final Set<Location> positions = new HashSet<>();

    public static void ajouter(Location location) {
        positions.add(location);
    }

    public static boolean estLegitime(Location location) {
        return positions.contains(location);
    }

    public static void retirer(Location location) {
        positions.remove(location);
    }
}
