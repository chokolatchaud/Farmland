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

    // ===== PRIX DE BASE DES 5 JETONS (avant coefficient de marche) =====
    // Un seul jeton par metier desormais (fini les listes de ressources
    // individuelles) - le prix reflete la difficulte/rarete globale du metier
    private static final Map<Material, Integer> PRIX_DE_BASE = new HashMap<>();
    static {
        PRIX_DE_BASE.put(fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_FARMEUR, 8);
        PRIX_DE_BASE.put(fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_MINEUR, 15);
        PRIX_DE_BASE.put(fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_AGRICULTEUR, 12);
        PRIX_DE_BASE.put(fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_PECHEUR, 10);
        PRIX_DE_BASE.put(fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_TUEUR, 20);
    }

    // ===== A QUEL METIER APPARTIENT CHAQUE JETON =====
    public static String getMetierDeRessource(Material material) {
        if (material == fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_FARMEUR) return "Farmeur";
        if (material == fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_MINEUR) return "Mineur";
        if (material == fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_AGRICULTEUR) return "Agriculteur";
        if (material == fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_PECHEUR) return "Pecheur";
        if (material == fr.kevyn.farmland.menufarm.RecompenseUtil.JETON_TUEUR) return "Tueur";
        return null;
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
