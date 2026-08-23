package fr.kevyn.farmland.save;

import com.google.gson.JsonObject;

public class CorrecteurJson {



    public static void MigrationJson(JsonObject json, int configymlversion) {
        int versionActuelle = json.has("GsonSave") ? json.get("GsonSave").getAsInt() : 0;

        // TANT QUE le fichier n'a pas rattrape la vraie version, on avance etape par etape
        while (versionActuelle < configymlversion) {

            if (versionActuelle == 0) {
                //logique de reparation "0 -> 1" ici
                // On construit la NOUVELLE structure (un objet JSON qui deviendra la Map)
                JsonObject toolLevels = new JsonObject();

                // On lit chaque ANCIEN champ, un par un, s'il existe encore
                if (json.has("houeLevel")) {
                    toolLevels.addProperty("HOUE", json.get("houeLevel").getAsInt());
                }
                if (json.has("canneLevel")) {
                    toolLevels.addProperty("CANNE", json.get("canneLevel").getAsInt());
                }
                if (json.has("epeeLevel")) {
                    toolLevels.addProperty("EPEE", json.get("epeeLevel").getAsInt());
                }
                if (json.has("hacheLevel")) {
                    toolLevels.addProperty("HACHE", json.get("hacheLevel").getAsInt());
                }

                // On AJOUTE la nouvelle structure dans le json principal
                json.add("toolLevels", toolLevels);

                // On RETIRE les vieilles cles, devenues inutiles
                json.remove("houeLevel");
                json.remove("canneLevel");
                json.remove("epeeLevel");
                json.remove("hacheLevel");
            }
            else if (versionActuelle == 1) {
                //logique de reparation "1 -> 2" ici
            }
            else if (versionActuelle == 2) {
                //logique de reparation "2 -> 3" ici
            }

            versionActuelle++; // on avance d'un cran a chaque tour
            json.addProperty("GsonSave", versionActuelle); // on met a jour la version DANS le json
        }
}
}
