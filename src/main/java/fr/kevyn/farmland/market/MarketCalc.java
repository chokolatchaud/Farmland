package fr.kevyn.farmland.market;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

/**
 * Coeur du systeme de marche par offre/demande.
 * Chaque ressource appartient a UN metier (Mineur/Farmeur/Pecheur/
 * Agriculteur/Tueur). Le prix reel de vente = prix de base * (coefficient
 * du metier / 100). Plus un metier est vendu, plus son coefficient baisse
 * (marche qui s'ecroule si trop d'offre) - voir MarketCalcTask pour le
 * recalcul periodique.
 */
public class MarketCalc {

    // ===== PRIX DE BASE (avant coefficient de marche) - a ajuster selon ton equilibrage =====
    private static final Map<Material, Integer> PRIX_DE_BASE = new HashMap<>();
    static {
        // Farmeur
        PRIX_DE_BASE.put(Material.WHEAT, 5);
        PRIX_DE_BASE.put(Material.CARROT, 4);
        PRIX_DE_BASE.put(Material.POTATO, 4);
        PRIX_DE_BASE.put(Material.PUMPKIN, 8);

        // Mineur
        PRIX_DE_BASE.put(Material.COAL, 5);
        PRIX_DE_BASE.put(Material.IRON_INGOT, 15);
        PRIX_DE_BASE.put(Material.GOLD_INGOT, 25);
        PRIX_DE_BASE.put(Material.DIAMOND, 80);

        // Agriculteur
        PRIX_DE_BASE.put(Material.PORKCHOP, 6);
        PRIX_DE_BASE.put(Material.BEEF, 8);
        PRIX_DE_BASE.put(Material.CHICKEN, 5);
        PRIX_DE_BASE.put(Material.MUTTON, 7);

        // Pecheur
        PRIX_DE_BASE.put(Material.COD, 5);
        PRIX_DE_BASE.put(Material.SALMON, 7);
        PRIX_DE_BASE.put(Material.TROPICAL_FISH, 10);
        PRIX_DE_BASE.put(Material.PUFFERFISH, 12);

        // Tueur
        PRIX_DE_BASE.put(Material.ROTTEN_FLESH, 3);
        PRIX_DE_BASE.put(Material.BONE, 6);
        PRIX_DE_BASE.put(Material.GUNPOWDER, 10);
        PRIX_DE_BASE.put(Material.SHULKER_SHELL, 100);
    }

    // ===== A QUEL METIER APPARTIENT CHAQUE RESSOURCE =====
    public static String getMetierDeRessource(Material material) {
        return switch (material) {
            case WHEAT, CARROT, POTATO, PUMPKIN -> "Farmeur";
            case COAL, IRON_INGOT, GOLD_INGOT, DIAMOND -> "Mineur";
            case PORKCHOP, BEEF, CHICKEN, MUTTON -> "Agriculteur";
            case COD, SALMON, TROPICAL_FISH, PUFFERFISH -> "Pecheur";
            case ROTTEN_FLESH, BONE, GUNPOWDER, SHULKER_SHELL -> "Tueur";
            default -> null;
        };
    }

    /** Prix reel actuel = prix de base * coefficient du metier concerne / 100 */
    public static int getPrixDeBase(Material material) {
        return PRIX_DE_BASE.getOrDefault(material, 0);
    }

    public static int getPrixActuel(Material material, Market market) {
        Integer base = PRIX_DE_BASE.get(material);
        if (base == null) return 0;

        String metier = getMetierDeRessource(material);
        if (metier == null) return base;

        int coefficient = getCoefficientMetier(metier, market);
        return Math.max(1, (int) Math.round(base * (coefficient / 100.0)));
    }

    private static int getCoefficientMetier(String metier, Market market) {
        return switch (metier) {
            case "Mineur" -> market.getMoneyforcoefMineur();
            case "Farmeur" -> market.getMoneyforcoefFarmeur();
            case "Agriculteur" -> market.getMoneyforcoefAgriculteur();
            case "Pecheur" -> market.getMoneyforcoefPecheur();
            case "Tueur" -> market.getMoneyforcoefTueur();
            default -> 100;
        };
    }

    // ===== SUIVI DES VENTES DEPUIS LE DERNIER CYCLE (en memoire, remis a zero a chaque recalcul) =====
    private static final Map<String, Integer> ventesParMetier = new HashMap<>();

    public static void enregistrerVente(Material material) {
        String metier = getMetierDeRessource(material);
        if (metier == null) return;
        ventesParMetier.put(metier, ventesParMetier.getOrDefault(metier, 0) + 1);
    }

    public static Map<String, Integer> getVentesParMetier() {
        return ventesParMetier;
    }

    public static void resetVentes() {
        ventesParMetier.clear();
    }
}
