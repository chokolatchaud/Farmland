package fr.kevyn.farmland.boathub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Distribue le podium quotidien de la course de bateaux (500/250/100 $FB)
 * a chaque changement de jour (detecte en comparant la date sauvegardee a
 * la date du jour, verifie toutes les minutes). Vide ensuite le classement
 * du jour pour repartir a zero.
 */
public class DailyBoatReward {

    private static final String DATE_FILE = "boat_daily_lastreset.txt";
    private static final int[] REWARDS = { 500, 250, 100 };

    public static void checkAndRewardIfNewDay(JavaPlugin plugin) {
        File dateFile = new File(plugin.getDataFolder(), DATE_FILE);
        String today = LocalDate.now().toString();

        String lastReset = readLastReset(dateFile);
        if (today.equals(lastReset)) return; // deja fait aujourd'hui

        distributeReward(plugin);
        writeLastReset(dateFile, today);
    }

    private static void distributeReward(JavaPlugin plugin) {
        List<BoatTimeSave.BoatTimeEntry> daily = DailyBoatTimeSave.loadDailyTimes(plugin);

        if (daily.isEmpty()) {
            plugin.getLogger().info("[BoatRace] Aucune course aujourd'hui, pas de podium a distribuer");
            return;
        }

        Bukkit.broadcastMessage("§6§l═══ PODIUM DU JOUR — COURSE DE BATEAUX ═══");

        for (int i = 0; i < Math.min(3, daily.size()); i++) {
            BoatTimeSave.BoatTimeEntry entry = daily.get(i);
            int reward = REWARDS[i];

            PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(entry.playerName);
            if (ps != null) {
                ps.setMoney(ps.getMoney() + reward);
            }

            String medaille = i == 0 ? "🥇" : i == 1 ? "🥈" : "🥉";
            Bukkit.broadcastMessage("§e" + medaille + " " + entry.playerName + " §7— " + entry.seconds + "s §a+" + reward + " $FB");
            plugin.getLogger().info("[BoatRace] Podium quotidien : " + entry.playerName + " (#" + (i + 1) + ") +" + reward + " $FB");
        }

        Bukkit.broadcastMessage("§6§l═══════════════════════════════════════");

        DailyBoatTimeSave.clearDailyTimes(plugin);
    }

    private static String readLastReset(File file) {
        if (!file.exists()) return "";
        try {
            return Files.readString(file.toPath()).trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static void writeLastReset(File file, String date) {
        try {
            Files.writeString(file.toPath(), date);
        } catch (IOException e) {
            // pas grave si ca echoue, on retentera au prochain check
        }
    }
}
