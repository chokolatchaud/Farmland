package fr.kevyn.farmland.market;

import java.util.Map;

import org.bukkit.Bukkit;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.save.MarketSave;

/**
 * Planifie et exécute les recalculs périodiques du marché.
 *
 * Toutes les 30 minutes, les ventes du dernier cycle sont utilisées pour
 * ajuster les coefficients des métiers.
 */
public final class MarketCalcTask {

    private static final int SEUIL_SURPRODUCTION = 20; // au dela, le marche s'effondre
    private static final int SEUIL_RECUPERATION = 5;    // en dessous, le marche remonte
    private static final int VARIATION_POURCENT = 2;
    private static final int COEF_MIN = 50;
    private static final int COEF_MAX = 150;
    private static final long RECALC_INTERVAL_TICKS = 20L * 60 * 30;

    public static void demarrer(FarmlandMain plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> effectuerRecalcul(plugin),
            RECALC_INTERVAL_TICKS, RECALC_INTERVAL_TICKS); // toutes les 30 minutes
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

        genererEvenementMarche(market, ventes);

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

    private static void genererEvenementMarche(Market market, Map<String, Integer> ventes) {
        String[] metiers = {
            MarketCalc.MINEUR, MarketCalc.FARMEUR, MarketCalc.AGRICULTEUR,
            MarketCalc.PECHEUR, MarketCalc.TUEUR
        };

        String metierSurproduction = null;
        int maxVentes = SEUIL_SURPRODUCTION;
        int coefficientAvantMineur = 0;
        int coefficientAvantFarmeur = 0;
        int coefficientAvantAgriculteur = 0;
        int coefficientAvantPecheur = 0;
        int coefficientAvantTueur = 0;

        for (String metier : metiers) {
            int nombreDeVentes = ventes.getOrDefault(metier, 0);
            if (nombreDeVentes > maxVentes) {
                maxVentes = nombreDeVentes;
                metierSurproduction = metier;
            }
        }

        if (metierSurproduction != null) {
            market.setLastEventMetier(metierSurproduction);
            market.setLastEventMessage(MarketFlavor.getMessage(metierSurproduction, false));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§6§l📊 Bulletin économique");
            Bukkit.broadcastMessage("§c▼ " + metierSurproduction + " : surproduction (" + maxVentes + " ventes)");
            Bukkit.broadcastMessage("§7" + market.getLastEventMessage());
            Bukkit.broadcastMessage("");
            return;
        }

        // En récupération, on ne sélectionne un métier que si son coefficient
        // a réellement augmenté pendant ce recalcul. Puis on choisit celui
        // dont le marché a le plus progressé.
        String metierRecuperation = null;
        int meilleurGain = 0;
        int moinsDeVentes = Integer.MAX_VALUE;

        for (String metier : metiers) {
            int nombreDeVentes = ventes.getOrDefault(metier, 0);
            if (nombreDeVentes >= SEUIL_RECUPERATION) {
                continue;
            }

            int coefficientAvant;
            int coefficientApres;

            switch (metier) {
                case MarketCalc.MINEUR -> coefficientAvant = market.getMoneyforcoefMineur();
                case MarketCalc.FARMEUR -> coefficientAvant = market.getMoneyforcoefFarmeur();
                case MarketCalc.AGRICULTEUR -> coefficientAvant = market.getMoneyforcoefAgriculteur();
                case MarketCalc.PECHEUR -> coefficientAvant = market.getMoneyforcoefPecheur();
                case MarketCalc.TUEUR -> coefficientAvant = market.getMoneyforcoefTueur();
                default -> {
                    continue;
                }
            }

            // Ce recalcul est appelé avant l'écriture du snapshot, donc pour
            // connaître le vrai changement il faut comparer avec 2% de la
            // valeur actuelle, comme le ferait recalculer().
            coefficientApres = recalculer(coefficientAvant, nombreDeVentes);
            int gain = coefficientApres - coefficientAvant;

            if (gain > meilleurGain || (gain == meilleurGain && gain > 0 && nombreDeVentes < moinsDeVentes)) {
                meilleurGain = gain;
                moinsDeVentes = nombreDeVentes;
                metierRecuperation = metier;
            }
        }

        if (metierRecuperation != null && meilleurGain > 0) {
            int ventesRecuperation = ventes.getOrDefault(metierRecuperation, 0);
            market.setLastEventMetier(metierRecuperation);
            market.setLastEventMessage(MarketFlavor.getMessage(metierRecuperation, true));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§6§l📊 Bulletin économique");
            Bukkit.broadcastMessage("§a▲ " + metierRecuperation + " : le marché se redresse (" + ventesRecuperation + " ventes)");
            Bukkit.broadcastMessage("§7" + market.getLastEventMessage());
            Bukkit.broadcastMessage("");
            return;
        }

        market.setLastEventMetier("");
        market.setLastEventMessage("");
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
