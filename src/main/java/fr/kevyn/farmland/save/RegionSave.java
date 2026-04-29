package fr.kevyn.farmland.save;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;

public class RegionSave {

    // ===== SAVE TOUTES LES REGIONS =====
    public static void saveAllRegions(JavaPlugin plugin) {
        for (GameRegion region : GameRegionHashMap.getInstance().getRegionhashmap()) {
            saveOneRegion(plugin, region);
        }
    }

    // ===== SAVE UNE REGION =====
    public static void saveOneRegion(JavaPlugin plugin, GameRegion region) {
        if (region == null) return;
        File file = new File(plugin.getDataFolder() + "/regions", region.getName() + ".json");
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(region));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD TOUTES LES REGIONS =====
    public static void loadAllRegions(JavaPlugin plugin) {
        File folder = new File(plugin.getDataFolder() + "/regions");
        if (!folder.exists()) return;
        Gson gson = new Gson();
        for (File file : folder.listFiles()) {
            if (!file.isFile() || !file.getName().endsWith(".json")) continue;
            try (FileReader reader = new FileReader(file)) {
                GameRegion region = gson.fromJson(reader, GameRegion.class);
                GameRegionHashMap.getInstance().addregion(region);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}