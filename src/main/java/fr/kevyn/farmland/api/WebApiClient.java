package fr.kevyn.farmland.api;

import com.google.gson.Gson;

import fr.kevyn.farmland.boathub.BoatTimeSave;
import okhttp3.*;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebApiClient {

    private static final String EP_SERVER = "/api/server/status";
    private static final String EP_MARKET = "/api/market/metiers";
    private static final String EP_LEADER = "/api/leaderboard";
    private static final String EP_VOTE   = "/api/vote/sites";
    private static final String EP_BOATTIMES = "/api/boatrace/times";

    // pousse le classement complet des meilleurs temps de la course de bateaux
    public void pushBoatTimes(List<BoatTimeSave.BoatTimeEntry> times) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (BoatTimeSave.BoatTimeEntry entry : times) {
            data.add(Map.of("playerName", entry.playerName, "seconds", entry.seconds));
        }
        post(EP_BOATTIMES, data);
        plugin.getLogger().info("[WebAPI] Classement bateaux pousse (" + data.size() + " temps)");
    }

    // pousse la liste complete des sites de vote 
    // le nom affiche sur le site = le domaine de l'URL
    public void pushVoteSites(List<String> urls, String reward) {
        List<Map<String, Object>> sites = new ArrayList<>();
        int order = 1;
        for (String url : urls) {
            String name = url.replace("https://", "").replace("http://", "");
            if (name.contains("/")) name = name.substring(0, name.indexOf("/"));
            sites.add(Map.of("name", name, "url", url, "reward", reward, "order", order));
            order++;
        }
        post(EP_VOTE, sites);
        plugin.getLogger().info("[WebAPI] " + sites.size() + " site(s) de vote pousses vers le site");
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final Plugin plugin;
    private final String baseUrl;
    private final String apiKey;
    private final OkHttpClient http;
    private final Gson gson = new Gson();

    public WebApiClient(Plugin plugin, String baseUrl, String apiKey) {
        this.plugin  = plugin;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey  = apiKey;
        this.http = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .callTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    // pousse le statut du serveur
    public void pushServerStatus(int online, int max, String version) {
        post(EP_SERVER, Map.of("online_players", online, "max_players", max, "version", version));
    }

    /**
     * Pousse l'etat complet du marche par metier vers le site (remplace
     * l'ancien pushStructurePrice(), pense pour le systeme de structures
     * qui n'existe plus). Envoie les 5 coefficients + le prix de vente
     * actuel de chaque ressource, calcules via MarketCalc.
     *
     * ATTENTION : format cote a adapter selon ce que ton backend de site
     * attend reellement - je n'ai pas acces a ce code, ceci est un point
     * de depart raisonnable, pas une garantie de correspondance exacte.
     */
    public void pushMarketMetiers(fr.kevyn.farmland.market.Market market) {
        Map<String, Object> coefficients = Map.of(
                "mineur", market.getMoneyforcoefMineur(),
                "farmeur", market.getMoneyforcoefFarmeur(),
                "agriculteur", market.getMoneyforcoefAgriculteur(),
                "pecheur", market.getMoneyforcoefPecheur(),
                "tueur", market.getMoneyforcoefTueur()
        );

        List<Map<String, Object>> prix = new ArrayList<>();
        for (String metier : new String[] {
                fr.kevyn.farmland.market.MarketCalc.MINEUR,
                fr.kevyn.farmland.market.MarketCalc.FARMEUR,
                fr.kevyn.farmland.market.MarketCalc.AGRICULTEUR,
                fr.kevyn.farmland.market.MarketCalc.PECHEUR,
                fr.kevyn.farmland.market.MarketCalc.TUEUR
        }) {
            prix.add(Map.of(
                    "metier", metier,
                    "prixActuel", fr.kevyn.farmland.market.MarketCalc.getPrixActuel(metier, market)
            ));
        }

        post(EP_MARKET, Map.of("coefficients", coefficients, "prix", prix));
    }

    // pousse la balance + blocs poses + niveaux des 5 metiers d'un joueur vers le classement
    public void pushPlayerBalance(String username, double balance, int blocpose,
                                  int niveauMineur, int niveauFarmeur, int niveauPecheur,
                                  int niveauAgriculteur, int niveauTueur) {
        post(EP_LEADER, Map.of(
                "username",   username,
                "balance",    balance,
                "blocpose",   blocpose,
                "niveauMineur", niveauMineur,
                "niveauFarmeur", niveauFarmeur,
                "niveauPecheur", niveauPecheur,
                "niveauAgriculteur", niveauAgriculteur,
                "niveauTueur", niveauTueur
        ));
    }

    // HTTP POST asynchrone — jamais bloquant sur le main thread
    private void post(String path, Object bodyObj) {
        if (baseUrl == null || baseUrl.isEmpty() || apiKey == null || apiKey.isEmpty()) return;
        Request req = new Request.Builder()
                .url(baseUrl + path)
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(bodyObj).getBytes(StandardCharsets.UTF_8), JSON))
                .build();
        http.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, IOException e) {
                plugin.getLogger().log(Level.WARNING, "[WebAPI] echec POST " + path + " : " + e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) {
                try (Response r = response) {
                    if (!r.isSuccessful()) {
                        String body = r.body() != null ? r.body().string() : "";
                        plugin.getLogger().warning("[WebAPI] " + path + " -> HTTP " + r.code() + " | " + body);
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("[WebAPI] erreur lecture reponse : " + e.getMessage());
                }
            }
        });
    }
}
