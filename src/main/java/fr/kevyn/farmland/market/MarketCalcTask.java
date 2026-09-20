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

        // Pour la récupération, on choisit le métier qui a le plus gros recul
        // du coefficient vers 100, afin que l'événement corresponde réellement
        // à un changement du marché. En cas d'égalité, on départage par le moins de ventes.
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
                case MarketCalc.MINEUR -> {
                    coefficientAvant = market.getMoneyforcoefMineur();
                    coefficientApres = recalculer(coefficientAvant, nombreDeVentes);
                }
                case MarketCalc.FARMEUR -> {
                    coefficientAvant = market.getMoneyforcoefFarmeur();
                    coefficientApres = recalculer(coefficientAvant, nombreDeVentes);
                }
                case MarketCalc.AGRICULTEUR -> {
                    coefficientAvant = market.getMoneyforcoefAgriculteur();
                    coefficientApres = recalculer(coefficientAvant, nombreDeVentes);
                }
                case MarketCalc.PECHEUR -> {
                    coefficientAvant = market.getMoneyforcoefPecheur();
                    coefficientApres = recalculer(coefficientAvant, nombreDeVentes);
                }
                case MarketCalc.TUEUR -> {
                    coefficientAvant = market.getMoneyforcoefTueur();
                    coefficientApres = recalculer(coefficientAvant, nombreDeVentes);
                }
                default -> {
                    continue;
                }
            }

            int gain = coefficientApres - coefficientAvant;
            if (gain > meilleurGain || (gain == meilleurGain && nombreDeVentes < moinsDeVentes)) {
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
