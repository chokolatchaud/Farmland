package fr.kevyn.farmland.market;

import java.util.Map;

import org.bukkit.Bukkit;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.save.MarketSave;

/**
 * Toutes les 30 minutes, regarde combien de ventes ont eu lieu par metier
 * depuis le dernier cycle. Trop de ventes = trop d'offre = le prix de CE
 * metier s'effondre (coefficient baisse de 2%). Peu/pas de ventes = le
 * marche se redresse doucement vers 100 (prix de base).
 */
public class MarketCalcTask {

    private static final int SEUIL_SURPRODUCTION = 20; // au dela, le marche s'effondre
    private static final int SEUIL_RECUPERATION = 5;    // en dessous, le marche remonte
    private static final int VARIATION_POURCENT = 2;
    private static final int COEF_MIN = 50;
    private static final int COEF_MAX = 150;

    public static void demarrer(FarmlandMain plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> effectuerRecalcul(plugin),
            20L * 60 * 30, 20L * 60 * 30); // toutes les 30 minutes
    }

    /** Force un recalcul immediat, sans attendre le prochain cycle - utile pour /marketadmin recalc */
    public static void forcerRecalcul(FarmlandMain plugin) {
        effectuerRecalcul(plugin);
    }

    private static void effectuerRecalcul(FarmlandMain plugin) {
        Market market = MarketHolder.get();
        Map<String, Integer> ventes = MarketCalc.getVentesParMetier();

        market.setMoneyforcoefMineur(recalculer(market.getMoneyforcoefMineur(), ventes.getOrDefault("Mineur", 0)));
        market.setMoneyforcoefFarmeur(recalculer(market.getMoneyforcoefFarmeur(), ventes.getOrDefault("Farmeur", 0)));
        market.setMoneyforcoefAgriculteur(recalculer(market.getMoneyforcoefAgriculteur(), ventes.getOrDefault("Agriculteur", 0)));
        market.setMoneyforcoefPecheur(recalculer(market.getMoneyforcoefPecheur(), ventes.getOrDefault("Pecheur", 0)));
        market.setMoneyforcoefTueur(recalculer(market.getMoneyforcoefTueur(), ventes.getOrDefault("Tueur", 0)));

        MarketCalc.resetVentes();
        MarketSave.saveMarket(plugin, market);
        fr.kevyn.farmland.market.MarketHolograms.updateAll(plugin);

        // pousse aussi vers le site, si le module WebAPI est actif
        if (plugin.getConfig().getBoolean("webapi.enabled", false) && plugin.getWebApi() != null) {
            plugin.getWebApi().pushMarketMetiers(market);
        }

        plugin.getLogger().info("[Market] Recalcul effectue - Mineur:" + market.getMoneyforcoefMineur()
            + " Farmeur:" + market.getMoneyforcoefFarmeur()
            + " Agriculteur:" + market.getMoneyforcoefAgriculteur()
            + " Pecheur:" + market.getMoneyforcoefPecheur()
            + " Tueur:" + market.getMoneyforcoefTueur());
    }

    private static int recalculer(int coefficientActuel, int nombreDeVentes) {
        int nouveauCoefficient = coefficientActuel;

        if (nombreDeVentes > SEUIL_SURPRODUCTION) {
            nouveauCoefficient = coefficientActuel - (coefficientActuel * VARIATION_POURCENT / 100);
        } else if (nombreDeVentes < SEUIL_RECUPERATION && coefficientActuel < 100) {
            nouveauCoefficient = coefficientActuel + (coefficientActuel * VARIATION_POURCENT / 100);
        }

        return Math.max(COEF_MIN, Math.min(COEF_MAX, nouveauCoefficient));
    }
}
