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

    // ===== PRIX DE BASE (avant coefficient de marche) =====
    // Calibre selon : difficulte de farm, prix d'achat au /shop si necessaire,
    // et duree/effort pour obtenir la ressource (voir explications par metier)
    private static final Map<Material, Integer> PRIX_DE_BASE = new HashMap<>();
    static {
        // Farmeur - graine achetee (1/3/5/10$) puis pousse. Citrouille reellement
        // plus lente/complexe a pousser en vanilla (tige + fruit separe)
        PRIX_DE_BASE.put(Material.WHEAT, 5);
        PRIX_DE_BASE.put(Material.CARROT, 10);
        PRIX_DE_BASE.put(Material.POTATO, 15);
        PRIX_DE_BASE.put(Material.PUMPKIN, 30);

        // Mineur - aucun achat, prix inversement proportionnel a la vraie
        // chance de tirage du Cobblegenerator (charbon 20%, fer 10%, or 5%, diamant 2.5%)
        PRIX_DE_BASE.put(Material.COAL, 5);
        PRIX_DE_BASE.put(Material.IRON_INGOT, 12);
        PRIX_DE_BASE.put(Material.GOLD_INGOT, 25);
        PRIX_DE_BASE.put(Material.DIAMOND, 60);

        // Agriculteur - oeuf achete (10/20/30/50$), 1 ressource par kill,
        // le prix doit nettement depasser le cout de l'oeuf pour que la boucle ait un sens
        PRIX_DE_BASE.put(Material.CHICKEN, 18);
        PRIX_DE_BASE.put(Material.MUTTON, 35);
        PRIX_DE_BASE.put(Material.BEEF, 50);
        PRIX_DE_BASE.put(Material.PORKCHOP, 80);

        // Pecheur - aucun achat, temps d'attente egal pour les 4 dans ce systeme,
        // prix base sur la rareté/exotisme reel du poisson
        PRIX_DE_BASE.put(Material.COD, 4);
        PRIX_DE_BASE.put(Material.SALMON, 7);
        PRIX_DE_BASE.put(Material.TROPICAL_FISH, 18);
        PRIX_DE_BASE.put(Material.PUFFERFISH, 18);

        // Tueur - oeuf achete (10/20/30/100$), le Shulker est VOLONTAIREMENT le
        // plus cher des 4 (vraie difficulte de fin de jeu, Cite de l'End)
        PRIX_DE_BASE.put(Material.GUNPOWDER, 18);
        PRIX_DE_BASE.put(Material.BONE, 35);
        PRIX_DE_BASE.put(Material.ROTTEN_FLESH, 50);
        PRIX_DE_BASE.put(Material.SHULKER_SHELL, 160);
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
