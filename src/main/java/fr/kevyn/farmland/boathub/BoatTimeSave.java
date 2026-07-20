package fr.kevyn.farmland.boathub;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Sauvegarde des meilleurs temps de la course de bateaux.
 * Stocke une "file" des N meilleurs temps (tous joueurs confondus),
 * triee du plus rapide au plus lent, dans boat_times.json.
 * Un joueur peut apparaitre plusieurs fois s'il a plusieurs bons temps,
 * mais on ne garde que son MEILLEUR temps personnel dans le classement.
 */
public class BoatTimeSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "boat_times.json";
    private static final int MAX_ENTRIES = 10; // top 10 conserve

    /** Une entree du classement : joueur + son meilleur temps en secondes */
    public static class BoatTimeEntry {
        public String playerName;
        public int seconds;

        public BoatTimeEntry(String playerName, int seconds) {
            this.playerName = playerName;
            this.seconds = seconds;
        }
    }

    private static File getFile(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), FILE_NAME);
    }

    /** Charge le classement complet (trie du plus rapide au plus lent) */
    public static List<BoatTimeEntry> loadTimes(JavaPlugin plugin) {
        File file = getFile(plugin);
        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            List<BoatTimeEntry> list = gson.fromJson(reader, new TypeToken<List<BoatTimeEntry>>(){}.getType());
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().warning("[BoatRace] Impossible de charger " + FILE_NAME + " : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void saveTimes(JavaPlugin plugin, List<BoatTimeEntry> times) {
        try (FileWriter writer = new FileWriter(getFile(plugin))) {
            gson.toJson(times, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("[BoatRace] Impossible de sauvegarder " + FILE_NAME + " : " + e.getMessage());
        }
    }

    /**
     * Enregistre un temps de course. Ne garde que le MEILLEUR temps par joueur.
     * Retourne true si c'est un nouveau record personnel (temps ameliore ou premiere course).
     */
    public static boolean recordTime(JavaPlugin plugin, String playerName, int seconds) {
        List<BoatTimeEntry> times = loadTimes(plugin);

        BoatTimeEntry existant = null;
        for (BoatTimeEntry entry : times) {
            if (entry.playerName.equalsIgnoreCase(playerName)) {
                existant = entry;
                break;
            }
        }

        boolean record = false;
        if (existant == null) {
            times.add(new BoatTimeEntry(playerName, seconds));
            record = true;
        } else if (seconds < existant.seconds) {
            existant.seconds = seconds;
            record = true;
        }

        // tri croissant (le plus rapide en premier) et on garde le top MAX_ENTRIES
        times.sort((a, b) -> Integer.compare(a.seconds, b.seconds));
        if (times.size() > MAX_ENTRIES) {
            times = new ArrayList<>(times.subList(0, MAX_ENTRIES));
        }

        saveTimes(plugin, times);
        return record;
    }

    /** Top N temps (deja tries du plus rapide au plus lent) */
    public static List<BoatTimeEntry> getTop(JavaPlugin plugin, int n) {
        List<BoatTimeEntry> times = loadTimes(plugin);
        if (times.size() <= n) return times;
        return new ArrayList<>(times.subList(0, n));
    }
}
