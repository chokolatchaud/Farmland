package fr.kevyn.farmland.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import discordwebhook.messagediscord;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerSave {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private PlayerSave() {
    }

    public static Gson creategsoninstance() {
        return GSON;
    }

    public String ConvertirEnJson(PlayerServer playerServer) {
        return GSON.toJson(playerServer);
    }

    public PlayerServer LireLeJson(String json) {
        return GSON.fromJson(json, PlayerServer.class);
    }

    public static void saveAllPlayerServerFile(JavaPlugin plugin) throws IOException {
        for (PlayerServer joueur : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
            saveOnePlayerServerFile(plugin, joueur);
        }
    }

    public static void saveOnePlayerServerFile(JavaPlugin plugin, PlayerServer playerServer) {
        if (plugin == null || playerServer == null || playerServer.getUuid() == null) {
            return;
        }

        File file = new File(plugin.getDataFolder() + "/players",
                playerServer.getUuid() + ".json");
        FileManager.savefile(file, GSON.toJson(playerServer));
    }

    public static void LoadPlayerserverFile(JavaPlugin plugin) {
        File folder = new File(plugin.getDataFolder() + "/players");

        if (!folder.exists()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            plugin.getLogger().warning("[PlayerSave] Impossible de lire le dossier players.");
            return;
        }

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json")) {
                continue;
            }

            try {
                String content = FileManager.Readfile(file);
                JsonObject json = parseObject(content, file);
                if (json == null) {
                    continue;
                }

                boolean changed = migrateLegacyName(json);
                String normalizedName = normalizeName(json);
                if (normalizedName != null && !normalizedName.equals(json.get("name").getAsString())) {
                    json.addProperty("name", normalizedName);
                    changed = true;
                } else if (normalizedName != null && !json.has("name")) {
                    json.addProperty("name", normalizedName);
                    changed = true;
                }

                int targetVersion = getConfiguredVersion(plugin);
                int fileVersion = getVersion(json);

                if (fileVersion < targetVersion) {
                    CorrecteurJson.MigrationJson(json, targetVersion);
                    changed = true;
                }

                if (changed) {
                    FileManager.savefile(file, GSON.toJson(json));
                }

                PlayerServer player = GSON.fromJson(json, PlayerServer.class);

                if (player == null || player.getUuid() == null) {
                    plugin.getLogger().severe(
                            "[PlayerSave] Fichier joueur invalide ignoré : " + file.getName());
                    continue;
                }

                PlayerserverHashMap.getInstance()
                        .AddplayerHaspMaps(player.getUuid(), player);
            } catch (Exception exception) {
                plugin.getLogger().severe(
                        "[PlayerSave] Impossible de charger " + file.getName()
                                + " : " + exception.getMessage());
            }
        }
    }

    private static JsonObject parseObject(String content, File file) {
        try {
            JsonElement element = JsonParser.parseString(content);
            if (!element.isJsonObject()) {
                throw new IllegalStateException("Le JSON racine n'est pas un objet.");
            }
            return element.getAsJsonObject();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static boolean migrateLegacyName(JsonObject json) {
        if (json.has("name") && !json.get("name").isJsonNull()
                && !json.get("name").getAsString().isBlank()) {
            return false;
        }

        JsonElement legacyName = json.get("Name");
        if (legacyName != null && !legacyName.isJsonNull()
                && !legacyName.getAsString().isBlank()) {
            json.addProperty("name", legacyName.getAsString());
            json.remove("Name");
            return true;
        }

        if (json.has("Name")) {
            json.remove("Name");
            return true;
        }

        return false;
    }

    private static String normalizeName(JsonObject json) {
        if (!json.has("name") || json.get("name").isJsonNull()) {
            return null;
        }

        String name = json.get("name").getAsString();
        if (name.isBlank()) {
            json.add("name", com.google.gson.JsonNull.INSTANCE);
            return null;
        }
        return name;
    }

    private static int getConfiguredVersion(JavaPlugin plugin) {
        return Math.max(0, plugin.getConfig().getInt("GsonSave", 0));
    }

    private static int getVersion(JsonObject json) {
        JsonElement value = json.get("GsonSave");
        if (value == null || value.isJsonNull()) {
            return 0;
        }

        try {
            return Math.max(0, value.getAsInt());
        } catch (RuntimeException exception) {
            return 0;
        }
    }

    private static String getPlayerName(JsonObject json) {
        JsonElement name = json.get("name");
        if (name != null && !name.isJsonNull()) {
            String value = name.getAsString();
            if (!value.isBlank()) {
                return value;
            }
        }

        JsonElement legacyName = json.get("Name");
        if (legacyName != null && !legacyName.isJsonNull()) {
            String value = legacyName.getAsString();
            if (!value.isBlank()) {
                return value;
            }
        }

        return "inconnu";
    }

    private static void logVerification(JavaPlugin plugin, String message) {
        plugin.getLogger().info("[PlayerSave] " + message);
    }

    public static void verifyallPlayerSaves(JavaPlugin plugin) {
        if (plugin == null) {
            return;
        }

        File folder = new File(plugin.getDataFolder() + "/players");
        if (!folder.exists()) {
            messagediscord.sendmessage(
                    "Aucune verification joueur : dossier inexistant", "statut");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            plugin.getLogger().warning(
                    "[PlayerSave] Impossible de lire le dossier players pendant la vérification.");
            return;
        }

        int configuredVersion = getConfiguredVersion(plugin);

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json")) {
                continue;
            }

            try {
                String content = FileManager.Readfile(file);
                JsonObject json = parseObject(content, file);
                if (json == null) {
                    plugin.getLogger().severe(
                            "[PlayerSave] JSON invalide ignoré pendant la vérification : "
                                    + file.getName());
                    continue;
                }

                boolean changed = false;

                if (migrateLegacyName(json)) {
                    changed = true;
                }

                if (json.has("name") && !json.get("name").isJsonNull()
                        && json.get("name").getAsString().isBlank()) {
                    json.add("name", com.google.gson.JsonNull.INSTANCE);
                    changed = true;
                }

                int fileVersion = getVersion(json);
                if (fileVersion < configuredVersion) {
                    CorrecteurJson.MigrationJson(json, configuredVersion);
                    changed = true;
                } else if (fileVersion > configuredVersion) {
                    logVerification(plugin,
                            "Version GsonSave " + fileVersion + " supérieure à la version configurée "
                                    + configuredVersion + " pour " + file.getName()
                                    + " : aucune migration appliquée.");
                }

                if (changed) {
                    FileManager.savefile(file, GSON.toJson(json));
                    logVerification(plugin,
                            "Fichier réparé : " + file.getName()
                                    + " (" + getPlayerName(json) + ")");
                } else {
                    logVerification(plugin,
                            "Aucun changement sur le fichier " + file.getName()
                                    + " (" + getPlayerName(json) + ")");
                }
            } catch (Exception exception) {
                plugin.getLogger().severe(
                        "[PlayerSave] Erreur de vérification pour " + file.getName()
                                + " : " + exception.getMessage());
            }
        }
    }
}
