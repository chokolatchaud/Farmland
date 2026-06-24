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

    // pousse le statut du serveur (joueurs connectes, max, version)
    public void pushServerStatus(int online, int max, String version) {
        post(EP_SERVER, Map.of(
                "online_players", online,
                "max_players",    max,
                "version",        version
        ));
    }

    // pousse le prix d'une structure vers le marche du site (sans accents dans les noms)
    public void pushStructurePrice(String name, double price, String category) {
        post(EP_MARKET, Map.of(
                "name",     name,
                "price",    price,
                "category", category
        ));
    }

    // pousse la balance d'un joueur vers le classement
    public void pushPlayerBalance(String username, double balance, int structures) {
        post(EP_LEADER, Map.of(
                "username",   username,
                "balance",    balance,
                "structures", structures
        ));
    }

    // vérifie le compte site d'un joueur via son code /linkaccount
    // retourne true si la vérification a réussi, false sinon
    public boolean verifyAccount(String username, String uuid, String code) {
        if (baseUrl == null || baseUrl.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            return false;
        }
        try {
            String body = gson.toJson(Map.of("username", username, "uuid", uuid, "code", code));
            Request req = new Request.Builder()
                    .url(baseUrl + "/api/auth/verify-account")
                    .header("X-API-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(body.getBytes(StandardCharsets.UTF_8), JSON))
                    .build();
            try (Response response = http.newCall(req).execute()) {
                if (response.body() != null) {
                    String responseBody = response.body().string();
                    // Parse la réponse JSON pour récupérer ok et message
                    com.google.gson.JsonObject json = new com.google.gson.JsonParser().parse(responseBody).getAsJsonObject();
                    return json.get("ok").getAsBoolean();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[WebAPI] erreur verifyAccount : " + e.getMessage());
        }
        return false;
    }

    // retourne le message de réponse de verifyAccount
    public String verifyAccountMessage(String username, String uuid, String code) {
        if (baseUrl == null || baseUrl.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            return "WebAPI non configurée";
        }
        try {
            String body = gson.toJson(Map.of("username", username, "uuid", uuid, "code", code));
            Request req = new Request.Builder()
                    .url(baseUrl + "/api/auth/verify-account")
                    .header("X-API-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(body.getBytes(StandardCharsets.UTF_8), JSON))
                    .build();
            try (Response response = http.newCall(req).execute()) {
                if (response.body() != null) {
                    String responseBody = response.body().string();
                    com.google.gson.JsonObject json = new com.google.gson.JsonParser().parse(responseBody).getAsJsonObject();
                    return json.get("message").getAsString();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[WebAPI] erreur verifyAccountMessage : " + e.getMessage());
        }
        return "Erreur de connexion au site";
    }

    // HTTP POST asynchrone (fire-and-forget, jamais de blocage du main thread)
    // Fix Emergent : force UTF-8 pour eviter les problemes d'accents sur Windows
    private void post(String path, Object bodyObj) {
        if (baseUrl == null || baseUrl.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            return;
        }
        Request req = new Request.Builder()
                .url(baseUrl + path)
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(bodyObj).getBytes(StandardCharsets.UTF_8), JSON))
                .build();

        http.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                plugin.getLogger().log(Level.WARNING,
                        "[WebAPI] echec POST " + path + " : " + e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) {
                try (Response r = response) {
                    if (!r.isSuccessful()) {
                        // log le corps de la reponse pour diagnostiquer les erreurs
                        String body = r.body() != null ? r.body().string() : "";
                        plugin.getLogger().warning(
                                "[WebAPI] " + path + " -> HTTP " + r.code() + " | " + body);
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("[WebAPI] erreur lecture reponse : " + e.getMessage());
                }
            }
        });
    }
}
