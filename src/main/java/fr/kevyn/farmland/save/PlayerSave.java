package fr.kevyn.farmland.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public static Gson creategsoninstance(){
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

    }

    public String ConvertirEnJson(PlayerServer playerServer) {

        return creategsoninstance().toJson(playerServer);

    }

    public PlayerServer LireLeJson(String json) {
        return creategsoninstance().fromJson(json, PlayerServer.class);

    }

    public static void saveAllPlayerServerFile(JavaPlugin plugin) throws IOException {
        for (PlayerServer joueur : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
            saveOnePlayerServerFile(plugin, joueur);
        }



    }

    public static void saveOnePlayerServerFile(JavaPlugin plugin, PlayerServer playerserver) {

        if (playerserver == null) return;

        UUID uuid = playerserver.getUuid();
        File file = new File(plugin.getDataFolder() + "/players", uuid.toString() + ".json");

        FileManager.savefile(file, (creategsoninstance().toJson(playerserver)));
        }


    public static void LoadPlayerserverFile(JavaPlugin plugin) {

        File folder = new File(plugin.getDataFolder() + "/players");


        if (!folder.exists()) return;
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
            PlayerServer player = creategsoninstance().fromJson(FileManager.Readfile(file), PlayerServer.class);
            PlayerserverHashMap.getInstance().AddplayerHaspMaps(player.getUuid(), player);
                }
            }
        }




    private static String getPlayerName(JsonObject json) {
        if (json.has("Name") && !json.get("Name").isJsonNull()) {
            return json.get("Name").getAsString();
        }
        return "inconnu";
    }

    public static void verifyallPlayerSaves(JavaPlugin plugin) {
        File folder = new File(plugin.getDataFolder() + "/players");
        if (!folder.exists()){
            messagediscord.sendmessage("AUcune verification joueur : fichier inexistant","statut" );
            return;
        };

        int configymlversion = plugin.getConfig().getInt("GsonSave",-1);
        if (configymlversion == -1) {
            messagediscord.sendmessage("AUcune verification joueur : fichier config a -1","statut ");
            return;
        }

        for (File file : folder.listFiles()) {

            if (file.isFile() && file.getName().endsWith(".json")) {
                String contenufichier = FileManager.Readfile(file);
                JsonObject stringjson = JsonParser.parseString(contenufichier).getAsJsonObject();
                if (stringjson.has("GsonSave")) {
                    int version = stringjson.get("GsonSave").getAsInt();
                    if (version == configymlversion) {
                        messagediscord.sendmessage("Aucun changement sur le fichier de " + getPlayerName(stringjson) ,"statut" );
                    }else{
                        //correction a mettre en place sur le json
                        //Cette Correction depend de la classe CorrecteurJson
                        CorrecteurJson.MigrationJson(stringjson, configymlversion);
                        String nouveauContenu = creategsoninstance().toJson(stringjson);
                        FileManager.savefile(file, nouveauContenu);
                    }
                }else{
                    stringjson.addProperty("GsonSave",0);
                    String nouveauContenu = creategsoninstance().toJson(stringjson);
                    FileManager.savefile(file, nouveauContenu);
                    messagediscord.sendmessage("Rajout de GSONSAVE sur le fichier de " + getPlayerName(stringjson) ,"statut" );
                }








            }
        }
    }

}





