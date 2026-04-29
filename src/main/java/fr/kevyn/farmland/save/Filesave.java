package fr.kevyn.farmland.save;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class Filesave {

    public static void SavePlayerserverFile(JavaPlugin plugin) {
        Map<UUID, PlayerServer> Allplayerserver = PlayerserverHashMap.getInstance().getHashMapPlayer();
        for (PlayerServer players : Allplayerserver.values()) {
            UUID uuid = players.getUuid();
            File file = new File(plugin.getDataFolder() + "/players", uuid.toString() + ".json");
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(new Gson().toJson(players));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveOnePlayerServerFile(JavaPlugin plugin, PlayerServer playerserver) {
        if (playerserver == null) return;
        UUID uuid = playerserver.getUuid();
        File file = new File(plugin.getDataFolder() + "/players", uuid.toString() + ".json");
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(playerserver));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void LoadPlayerserverFile(JavaPlugin plugin) {
        File folder = new File(plugin.getDataFolder() + "/players");
        Gson gson = new Gson();
        if (!folder.exists()) return;
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try (FileReader reader = new FileReader(file)) {
                    PlayerServer player = gson.fromJson(reader, PlayerServer.class);
                    PlayerserverHashMap.getInstance().getHashMapPlayer().put(player.getUuid(), player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}