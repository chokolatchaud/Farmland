package fr.kevyn.farmland.market;

import java.util.HashMap;
import java.util.Map;

/**
 * Coeur du systeme de marche par offre/demande.
 * Fonctionne directement par NOM DE METIER (String) - les Jetons ne sont
 * plus des Material stockes dans un inventaire generique, juste 5 champs
 * dedies sur PlayerServer (jetonMineur, jetonFarmeur...), donc plus besoin
 * de passer par un Material intermediaire pour identifier de quoi on parle.
 *
 * Le prix reel de vente = prix de base * (coefficient du metier / 100).
 * Plus un metier est vendu, plus son coefficient baisse (marche qui
 * s'ecroule si trop d'offre) - voir MarketCalcTask pour le recalcul
 * periodique.
 */
public class MarketCalc {

    public static final String MINEUR = "Mineur";
    public static final String FARMEUR = "Farmeur";
    public static final String PECHEUR = "Pecheur";
    public static final String AGRICULTEUR = "Agriculteur";
    public static final String TUEUR = "Tueur";

    // ===== PRIX DE BASE DES 5 JETONS (avant coefficient de marche) =====
    private static final Map<String, Integer> PRIX_DE_BASE = new HashMap<>();
    static {
        PRIX_DE_BASE.put(FARMEUR, 8);
        PRIX_DE_BASE.put(MINEUR, 15);
        PRIX_DE_BASE.put(AGRICULTEUR, 12);
        PRIX_DE_BASE.put(PECHEUR, 10);
        PRIX_DE_BASE.put(TUEUR, 20);
    }

    public static int getPrixDeBase(String metier) {
        return PRIX_DE_BASE.getOrDefault(metier, 0);
    }

    /** Prix reel actuel = prix de base * coefficient du metier concerne / 100 */
    public static int getPrixActuel(String metier, Market market) {
        Integer base = PRIX_DE_BASE.get(metier);
        if (base == null) return 0;

        int coefficient = getCoefficientMetier(metier, market);
        return Math.max(1, (int) Math.round(base * (coefficient / 100.0)));
    }

    private static int getCoefficientMetier(String metier, Market market) {
        return switch (metier) {
            case MINEUR -> market.getMoneyforcoefMineur();
            case FARMEUR -> market.getMoneyforcoefFarmeur();
            case AGRICULTEUR -> market.getMoneyforcoefAgriculteur();
            case PECHEUR -> market.getMoneyforcoefPecheur();
            case TUEUR -> market.getMoneyforcoefTueur();
            default -> 100;
        };
    }

    // ===== SUIVI DES VENTES DEPUIS LE DERNIER CYCLE (en memoire, remis a zero a chaque recalcul) =====
    private static final Map<String, Integer> ventesParMetier = new HashMap<>();

    public static void enregistrerVente(String metier) {
        ventesParMetier.put(metier, ventesParMetier.getOrDefault(metier, 0) + 1);
    }

    public static Map<String, Integer> getVentesParMetier() {
        return ventesParMetier;
    }

    public static void resetVentes() {
        ventesParMetier.clear();
    }
}
