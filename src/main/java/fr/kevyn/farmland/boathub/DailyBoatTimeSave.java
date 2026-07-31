package fr.kevyn.farmland.boathub;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class DailyBoatTimeSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "boat_daily_times.json";

    private static File getFile(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), FILE_NAME);
    }

    public static List<BoatTimeSave.BoatTimeEntry> loadDailyTimes(JavaPlugin plugin) {
        File file = getFile(plugin);
        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            List<BoatTimeSave.BoatTimeEntry> list = gson.fromJson(reader, new TypeToken<List<BoatTimeSave.BoatTimeEntry>>(){}.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            plugin.getLogger().warning("[BoatRace] Impossible de charger " + FILE_NAME + " : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void saveDailyTimes(JavaPlugin plugin, List<BoatTimeSave.BoatTimeEntry> times) {
        try (FileWriter writer = new FileWriter(getFile(plugin))) {
            gson.toJson(times, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("[BoatRace] Impossible de sauvegarder " + FILE_NAME + " : " + e.getMessage());
        }
    }

    /** Enregistre un temps dans le classement du jour (meilleur temps par joueur, comme l'all-time) */
    public static void recordDailyTime(JavaPlugin plugin, String playerName, int seconds) {
        List<BoatTimeSave.BoatTimeEntry> times = loadDailyTimes(plugin);

        BoatTimeSave.BoatTimeEntry existant = null;
        for (BoatTimeSave.BoatTimeEntry entry : times) {
            if (entry.playerName.equalsIgnoreCase(playerName)) { existant = entry; break; }
        }

        if (existant == null) {
            times.add(new BoatTimeSave.BoatTimeEntry(playerName, seconds));
        } else if (seconds < existant.seconds) {
            existant.seconds = seconds;
        }

        times.sort((a, b) -> Integer.compare(a.seconds, b.seconds));
        saveDailyTimes(plugin, times);
    }

    /** Vide le classement du jour (appele apres distribution du podium quotidien) */
    public static void clearDailyTimes(JavaPlugin plugin) {
        saveDailyTimes(plugin, new ArrayList<>());
    }
}
