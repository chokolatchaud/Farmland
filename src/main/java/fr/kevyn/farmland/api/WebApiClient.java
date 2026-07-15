package fr.kevyn.farmland.api;

import com.google.gson.Gson;
import okhttp3.*;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebApiClient {

    private static final String EP_SERVER = "/api/server/status";
    private static final String EP_MARKET = "/api/market/structures";
    private static final String EP_LEADER = "/api/leaderboard";
    private static final String EP_VOTE   = "/api/vote/sites";

    // pousse la liste complete des sites de vote (depuis le config.yml du plugin)
    // le nom affiche sur le site = le domaine de l'URL
    public void pushVoteSites(java.util.List<String> urls, String reward) {
        java.util.List<Map<String, Object>> sites = new java.util.ArrayList<>();
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

    // pousse le prix d'une structure vers le marché du site
    public void pushStructurePrice(String name, double price, String category) {
        post(EP_MARKET, Map.of("name", name, "price", price, "category", category));
    }

    // pousse la balance + structures + blocs posés d'un joueur vers le classement
    public void pushPlayerBalance(String username, double balance, int structures, int blocpose) {
        post(EP_LEADER, Map.of(
                "username",   username,
                "balance",    balance,
                "structures", structures,
                "blocpose",   blocpose
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
            @Override public void onFailure(Call call, IOException e) {
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
