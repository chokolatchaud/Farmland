package fr.kevyn.farmland.directives.marché.calcul;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.directives.infrastructure.DebugLog;
import fr.kevyn.farmland.directives.marché.affichage.MarketFlavor;
import fr.kevyn.farmland.directives.marché.affichage.MarketHolograms;
import fr.kevyn.farmland.doonees.marche.Market;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.persistance.marché.MarketSave;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * Fait vivre le marché : à chaque cycle (5 minutes par défaut), la demande
 * simulée absorbe le volume normal de chaque métier (voir MarketCalc.appliquerDemande) ;
 * une fois par heure, le volume normal est réappris à partir des ventes (MarketCalc.apprendreVolume) ;
 * puis le marché est sauvegardé et les hologrammes rafraîchis.
 *
 * Toutes les 30 minutes, un bulletin économique annonce le métier dont le prix
 * a le plus baissé (surproduction) ou, à défaut, le plus remonté.
 */
public final class MarketCalcTask {

    /** Variation minimale du coefficient (en points de %) pour mériter un bulletin. */
    static final int SEUIL_BULLETIN = 5;
    private static final long MINUTES_ENTRE_BULLETINS = 30;

    private static long intervalleMinutes = 5;
    private static int cyclesDepuisBulletin = 0;
    private static long minutesDepuisApprentissage = 0;
    // coefficients au dernier bulletin, pour mesurer la variation sur 30 minutes
    private static final Map<String, Integer> coefsAuDernierBulletin = new HashMap<>();

    public static void demarrer(FarmlandMain plugin, long minutes) {
        intervalleMinutes = Math.max(1, minutes);
        Market market = MarketHolder.get();
        MarketCalc.initialiser(market);
        memoriserCoefficients(market);

        long ticks = 20L * 60 * intervalleMinutes;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> effectuerRecalcul(plugin), ticks, ticks);
    }

    /** Force un cycle immédiat (demande + sauvegarde + bulletin) - utile pour /marketadmin recalc */
    public static void forcerRecalcul(FarmlandMain plugin) {
        cyclesDepuisBulletin = Integer.MAX_VALUE - 1; // le bulletin sort tout de suite
        effectuerRecalcul(plugin);
    }

    private static void effectuerRecalcul(FarmlandMain plugin) {
        if (MarketHolder.get() == null) {
            plugin.getLogger().warning("[Market] Recalcul ignoré : marché non initialisé.");
            return;
        }
        Market market = MarketHolder.get();
        double heures = intervalleMinutes / 60.0;

        // apprentissage du volume normal, une fois par heure pleine
        minutesDepuisApprentissage += intervalleMinutes;
        boolean apprendre = minutesDepuisApprentissage >= 60;

        StringBuilder journal = new StringBuilder();
        for (String metier : MarketCalc.METIERS) {
            int avant = market.getCoefficient(metier);
            if (apprendre) {
                long vendus = MarketCalc.prendreVentesApprentissage(metier);
                double volume = MarketCalc.apprendreVolume(MarketCalc.getVolume(metier, market), vendus, minutesDepuisApprentissage / 60.0);
                MarketCalc.setVolume(metier, market, volume);
            }
            double surplus = MarketCalc.getSurplus(metier, market);
            MarketCalc.setSurplus(metier, market, MarketCalc.appliquerDemande(surplus, heures, MarketCalc.getVolume(metier, market)));
            if (journal.length() > 0) journal.append(", ");
            journal.append(metier).append(' ').append(avant).append("->").append(market.getCoefficient(metier))
                    .append(" (volume ").append(Math.round(MarketCalc.getVolume(metier, market))).append("/h)");
        }
        if (apprendre) minutesDepuisApprentissage = 0;

        cyclesDepuisBulletin++;
        if (cyclesDepuisBulletin * intervalleMinutes >= MINUTES_ENTRE_BULLETINS) {
            genererEvenementMarche(market, MarketCalc.getVentesParMetier());
            MarketCalc.resetVentes();
            memoriserCoefficients(market);
            cyclesDepuisBulletin = 0;
        }

        MarketSave.saveMarket(plugin, market);
        MarketHolograms.updateAll(plugin);

        DebugLog.debug(plugin, "[Market] Cycle : " + journal);
        JournalEvenements.enregistrer("marche_recalcul", null, journal.toString());
    }

    private static void memoriserCoefficients(Market market) {
        for (String metier : MarketCalc.METIERS) {
            coefsAuDernierBulletin.put(metier, market.getCoefficient(metier));
        }
    }

    private static void genererEvenementMarche(Market market, Map<String, Long> ventes) {
        Map<String, Integer> variations = new HashMap<>();
        for (String metier : MarketCalc.METIERS) {
            int avant = coefsAuDernierBulletin.getOrDefault(metier, market.getCoefficient(metier));
            variations.put(metier, market.getCoefficient(metier) - avant);
        }

        String metier = choisirMetierBulletin(variations);
        if (metier == null) {
            market.setLastEventMetier("");
            market.setLastEventMessage("");
            return;
        }

        boolean hausse = variations.get(metier) > 0;
        long jetonsVendus = ventes.getOrDefault(metier, 0L);
        market.setLastEventMetier(metier);
        market.setLastEventMessage(MarketFlavor.getMessage(metier, hausse));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6§l📊 Bulletin économique");
        if (hausse) {
            Bukkit.broadcastMessage("§a▲ " + MarketCalc.nomAffiche(metier) + " : le marché se redresse (" + market.getCoefficient(metier) + "%)");
        } else {
            Bukkit.broadcastMessage("§c▼ " + MarketCalc.nomAffiche(metier) + " : surproduction (" + jetonsVendus + " jetons vendus, " + market.getCoefficient(metier) + "%)");
        }
        Bukkit.broadcastMessage("§7" + market.getLastEventMessage());
        Bukkit.broadcastMessage("");
    }

    /**
     * Métier du bulletin : la plus forte BAISSE d'au moins SEUIL_BULLETIN points,
     * sinon la plus forte HAUSSE d'au moins SEUIL_BULLETIN points, sinon aucun (null).
     * Visibilité package : testé par MarketCalcTaskTest.
     */
    static String choisirMetierBulletin(Map<String, Integer> variations) {
        String pireBaisse = null;
        int baisse = -SEUIL_BULLETIN + 1;
        String meilleureHausse = null;
        int hausse = SEUIL_BULLETIN - 1;
        for (String metier : MarketCalc.METIERS) {
            int v = variations.getOrDefault(metier, 0);
            if (v < baisse) { baisse = v; pireBaisse = metier; }
            if (v > hausse) { hausse = v; meilleureHausse = metier; }
        }
        return pireBaisse != null ? pireBaisse : meilleureHausse;
    }
}
