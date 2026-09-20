package fr.kevyn.farmland.save;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CorrecteurJson {

    private CorrecteurJson() {
    }

    public static void MigrationJson(JsonObject json, int configymlversion) {
        if (json == null || configymlversion < 0) {
            return;
        }

        int versionActuelle = getVersion(json);

        if (versionActuelle > configymlversion) {
            return;
        }

        while (versionActuelle < configymlversion) {
            if (versionActuelle == 0) {
                migrateToolsV0ToV1(json);
            }

            versionActuelle++;
            json.addProperty("GsonSave", versionActuelle);
        }
    }

    private static int getVersion(JsonObject json) {
        if (!json.has("GsonSave") || json.get("GsonSave").isJsonNull()) {
            return 0;
        }

        try {
            return Math.max(0, json.get("GsonSave").getAsInt());
        } catch (RuntimeException exception) {
            return 0;
        }
    }

    private static void migrateToolsV0ToV1(JsonObject json) {
        JsonObject toolLevels;

        if (json.has("toolLevels") && json.get("toolLevels").isJsonObject()) {
            toolLevels = json.getAsJsonObject("toolLevels");
        } else {
            toolLevels = new JsonObject();
        }

        copyLegacyToolLevel(json, toolLevels, "houeLevel", "HOUE");
        copyLegacyToolLevel(json, toolLevels, "canneLevel", "CANNE");
        copyLegacyToolLevel(json, toolLevels, "epeeLevel", "EPEE");
        copyLegacyToolLevel(json, toolLevels, "hacheLevel", "HACHE");

        json.add("toolLevels", toolLevels);

        json.remove("houeLevel");
        json.remove("canneLevel");
        json.remove("epeeLevel");
        json.remove("hacheLevel");
    }

    private static void copyLegacyToolLevel(JsonObject json, JsonObject toolLevels,
                                             String legacyKey, String newKey) {
        if (toolLevels.has(newKey)) {
            return;
        }

        JsonElement legacyValue = json.get(legacyKey);
        if (legacyValue != null && !legacyValue.isJsonNull()) {
            try {
                toolLevels.addProperty(newKey, legacyValue.getAsInt());
            } catch (RuntimeException ignored) {
                // Une valeur legacy invalide ne doit pas écraser une donnée existante.
            }
        }
    }
}
