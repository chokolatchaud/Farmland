package fr.kevyn.farmland.persistance.joueurs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.persistance.CorrecteurJson;
import fr.kevyn.farmland.persistance.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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

        int targetVersion = getConfiguredVersion(plugin);

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json")) {
                continue;
            }

            try {
                JsonObject json = parseObject(FileManager.Readfile(file));
                if (json == null) {
                    plugin.getLogger().severe(
                            "[PlayerSave] JSON invalide ignoré : " + file.getName());
                    continue;
                }

                // Anciennes sauvegardes : Name -> name et plotdata -> plotData.
                boolean changed = migrateLegacyFields(json);

                int fileVersion = getVersion(json);
                if (fileVersion < targetVersion) {
                    CorrecteurJson.MigrationJson(json, targetVersion);
                    changed = true;
                }

                /*
                 * On désérialise AVANT d'écraser le fichier.
                 * Une sauvegarde contenant un plot ne peut donc jamais devenir
                 * plotData = null à cause d'une migration ratée.
                 */
                PlayerServer player = GSON.fromJson(json, PlayerServer.class);

                if (player == null || player.getUuid() == null) {
                    plugin.getLogger().severe(
                            "[PlayerSave] Fichier joueur invalide ignoré : " + file.getName());
                    continue;
                }

                JsonElement plotJson = json.get("plotData");
                if (plotJson != null && !plotJson.isJsonNull()
                        && player.getPlotdata() == null) {
                    plugin.getLogger().severe(
                            "[PlayerSave] PROTECTION : PlotData non désérialisé pour "
                                    + file.getName() + ". Fichier NON réécrit.");
                    continue;
                }

                String canonicalJson = GSON.toJson(player);

                if (changed || !canonicalJson.equals(GSON.toJson(json))) {
                    FileManager.savefile(file, canonicalJson);
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

    private static JsonObject parseObject(String content) {
        try {
            JsonElement element = JsonParser.parseString(content);
            return element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static boolean migrateLegacyFields(JsonObject json) {
        boolean changed = false;

        changed |= migrateAlias(json, "Name", "name");
        changed |= migrateAlias(json, "plotdata", "plotData");

        if (json.has("name") && !json.get("name").isJsonNull()) {
            try {
                if (json.get("name").getAsString().isBlank()) {
                    json.add("name", JsonNull.INSTANCE);
                    changed = true;
                }
            } catch (RuntimeException ignored) {
                json.add("name", JsonNull.INSTANCE);
                changed = true;
            }
        }

        return changed;
    }

    private static boolean migrateAlias(JsonObject json, String legacyKey, String canonicalKey) {
        if (!json.has(legacyKey)) {
            return false;
        }

        JsonElement legacyValue = json.get(legacyKey);

        // Si la clé moderne contient déjà une vraie valeur, on garde celle-ci.
        if (json.has(canonicalKey)
                && !json.get(canonicalKey).isJsonNull()
                && !isEmptyValue(json.get(canonicalKey))) {
            json.remove(legacyKey);
            return true;
        }

        json.add(canonicalKey, legacyValue.deepCopy());
        json.remove(legacyKey);
        return true;
    }

    private static boolean isEmptyValue(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return true;
        }

        if (element.isJsonObject()) {
            return element.getAsJsonObject().entrySet().isEmpty();
        }

        if (element.isJsonArray()) {
            return element.getAsJsonArray().isEmpty();
        }

        if (element.isJsonPrimitive()
                && element.getAsJsonPrimitive().isString()) {
            return element.getAsString().isBlank();
        }

        return false;
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
            try {
                String value = name.getAsString();
                if (!value.isBlank()) {
                    return value;
                }
            } catch (RuntimeException ignored) {
            }
        }

        JsonElement legacyName = json.get("Name");
        if (legacyName != null && !legacyName.isJsonNull()) {
            try {
                String value = legacyName.getAsString();
                if (!value.isBlank()) {
                    return value;
                }
            } catch (RuntimeException ignored) {
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
                JsonObject json = parseObject(FileManager.Readfile(file));
                if (json == null) {
                    plugin.getLogger().severe(
                            "[PlayerSave] JSON invalide ignoré pendant la vérification : "
                                    + file.getName());
                    continue;
                }

                boolean changed = migrateLegacyFields(json);

                int fileVersion = getVersion(json);
                if (fileVersion < configuredVersion) {
                    CorrecteurJson.MigrationJson(json, configuredVersion);
                    changed = true;
                } else if (fileVersion > configuredVersion) {
                    logVerification(plugin,
                            "Version GsonSave " + fileVersion
                                    + " supérieure à la version configurée "
                                    + configuredVersion + " pour " + file.getName()
                                    + " : aucune migration appliquée.");
                }

                PlayerServer player = GSON.fromJson(json, PlayerServer.class);
                if (player == null || player.getUuid() == null) {
                    logVerification(plugin,
                            "Fichier non réécrit car les données joueur sont invalides : "
                                    + file.getName());
                    continue;
                }

                JsonElement plotJson = json.get("plotData");
                if (plotJson != null && !plotJson.isJsonNull()
                        && player.getPlotdata() == null) {
                    logVerification(plugin,
                            "Protection PlotData : fichier NON réécrit car plotData "
                                    + "n'a pas pu être désérialisé : " + file.getName());
                    continue;
                }

                String canonicalJson = GSON.toJson(player);
                if (!canonicalJson.equals(GSON.toJson(json))) {
                    changed = true;
                }

                if (changed) {
                    FileManager.savefile(file, canonicalJson);
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