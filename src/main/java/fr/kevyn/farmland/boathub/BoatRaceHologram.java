package fr.kevyn.farmland.boathub;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Hologramme des meilleurs temps de la course de bateaux — 100% natif (TextDisplay).
 * Un seul hologramme, pose par /raceadmin holo set, mis a jour a chaque nouveau temps
 * enregistre (recordTime dans BoatTimeSave). Position sauvegardee dans boat_hologram.json.
 * ATTENTION : update() doit etre appele depuis le thread principal (spawn d'entite).
 */
public class BoatRaceHologram {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String HOLO_FILE = "boat_hologram.json";
    private static final String TAG = "farmland_boat_holo";

    private static class HoloLoc {
        String world;
        double x, y, z;
    }

    private static HoloLoc emplacement = null;
    private static UUID entiteId = null;

    public static void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            emplacement = gson.fromJson(reader, HoloLoc.class);
            if (emplacement != null) {
                plugin.getLogger().info("[BoatRace][Holo] Hologramme des temps charge");
            }
        } catch (Exception e) {// IOException + JsonSyntaxException si le fichier est corrompu
            plugin.getLogger().warning("[BoatRace][Holo] Impossible de charger " + HOLO_FILE + " : " + e.getMessage());
        }
    }

    private static void save(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(emplacement, writer);
        } catch (Exception e) {// IOException + JsonSyntaxException si le fichier est corrompu
            plugin.getLogger().warning("[BoatRace][Holo] Impossible de sauvegarder " + HOLO_FILE + " : " + e.getMessage());
        }
    }

    public static void setHologram(JavaPlugin plugin, Location loc) {
        despawn(loc.getWorld());

        HoloLoc holo = new HoloLoc();
        holo.world = loc.getWorld().getName();
        holo.x = loc.getX();
        holo.y = loc.getY() + 1.2;
        holo.z = loc.getZ();
        emplacement = holo;
        save(plugin);
        update(plugin);
    }

    public static boolean removeHologram(JavaPlugin plugin) {
        if (emplacement == null) return false;
        World world = Bukkit.getWorld(emplacement.world);
        despawn(world);
        emplacement = null;

        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        if (file.exists()) file.delete();
        return true;
    }

    public static boolean hasHologram() {
        return emplacement != null;
    }

    /** Fait apparaitre l'hologramme si absent et rafraichit son texte (thread principal uniquement) */
    public static void update(JavaPlugin plugin) {
        if (emplacement == null) return;

        World world = Bukkit.getWorld(emplacement.world);
        if (world == null) return;
        if (!world.isChunkLoaded(((int) emplacement.x) >> 4, ((int) emplacement.z) >> 4)) return;

        Location loc = new Location(world, emplacement.x, emplacement.y, emplacement.z);
        TextDisplay display = getSpawned(world);

        if (display == null) {
            for (Entity e : world.getNearbyEntities(loc, 2, 2, 2)) {
                if (e instanceof TextDisplay && e.getScoreboardTags().contains(TAG)) {
                    e.remove();
                }
            }
            display = world.spawn(loc, TextDisplay.class, entity -> {
                entity.setBillboard(Display.Billboard.CENTER);
                entity.setShadowed(true);
                entity.setPersistent(false);
                entity.addScoreboardTag(TAG);
            });
            entiteId = display.getUniqueId();
        }

        display.text(LegacyComponentSerializer.legacySection().deserialize(buildText(plugin)));
    }

    private static TextDisplay getSpawned(World world) {
        if (entiteId == null || world == null) return null;
        Entity e = Bukkit.getEntity(entiteId);
        if (e instanceof TextDisplay && e.isValid()) return (TextDisplay) e;
        return null;
    }

    private static void despawn(World world) {
        TextDisplay display = getSpawned(world);
        if (display != null) display.remove();
        entiteId = null;
    }

    private static String buildText(JavaPlugin plugin) {
        List<BoatTimeSave.BoatTimeEntry> top = BoatTimeSave.getTop(plugin, 5);

        StringBuilder sb = new StringBuilder();
        sb.append("§8§m                              \n");
        sb.append("§b✦ §e§lMEILLEURS TEMPS §b✦\n");

        if (top.isEmpty()) {
            sb.append("§7Aucun temps enregistre\n");
            sb.append("§7Fais §b/hub §7puis §b/joinboat§7 !\n");
        } else {
            String[] medailles = { "§6🥇", "§7🥈", "§c🥉", "§f▪", "§f▪" };
            for (int i = 0; i < top.size(); i++) {
                BoatTimeSave.BoatTimeEntry entry = top.get(i);
                sb.append(medailles[Math.min(i, medailles.length - 1)])
                  .append(" §f").append(entry.playerName)
                  .append(" §8— §b").append(formatTemps(entry.seconds)).append("\n");
            }
        }

        sb.append("§8§m                              ");
        return sb.toString();
    }

    private static String formatTemps(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes > 0
            ? minutes + "m " + String.format("%02d", seconds) + "s"
            : seconds + "s";
    }
}
