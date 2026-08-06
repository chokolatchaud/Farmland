package fr.kevyn.farmland.market;

/**
 * Garde le Market ACTUEL en memoire pendant que le serveur tourne.
 * Charge au demarrage depuis MarketSave, sauvegarde a chaque recalcul.
 */
public class MarketHolder {

    private static Market marketActuel;

    public static Market get() {
        if (marketActuel == null) {
            // valeurs de depart : coefficient 100 = prix de base, pour tous les metiers
            marketActuel = new Market(100, 100, 100, 100, 100);
        }
        return marketActuel;
    }

    public static void set(Market market) {
        marketActuel = market;
    }
}
