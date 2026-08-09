package fr.kevyn.farmland.menufarm;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.gson.reflect.TypeToken;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Hologrammes de classement par metier - top 5 joueurs, meme architecture
 * que MarketHolograms (TextDisplay natif, JSON pour les emplacements).
 * Pose via /classementadmin holo set <metier>.
 */
public class LeaderboardHolograms {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String HOLO_FILE = "leaderboard_holograms.json";
    private static final String TAG_PREFIX = "farmland_classement_holo_";
    private static final int TOP_N = 5;

    public static class HoloLoc {
        String world;
        double x, y, z;
    }

    private record Entree(String nom, int niveau) {}

    private static Map<String, HoloLoc> emplacements = new HashMap<>();
    private static final Map<String, UUID> entites = new HashMap<>();

    public static final String[] METIERS = { "Mineur", "Farmeur", "Agriculteur", "Pecheur", "Tueur" };

    public static void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Map<String, HoloLoc> data = gson.fromJson(reader, new TypeToken<Map<String, HoloLoc>>(){}.getType());
            if (data != null) emplacements = data;
            plugin.getLogger().info("[Holo] " + emplacements.size() + " hologramme(s) de classement chargé(s)");
        } catch (IOException e) {
            plugin.getLogger().warning("[Holo] Impossible de charger " + HOLO_FILE + " : " + e.getMessage());
        }
    }

    private static void save(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(emplacements, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("[Holo] Impossible de sauvegarder " + HOLO_FILE + " : " + e.getMessage());
        }
    }

    public static boolean isValidMetier(String metier) {
        for (String m : METIERS) {
            if (m.equalsIgnoreCase(metier)) return true;
        }
        return false;
    }

    public static void setHologram(JavaPlugin plugin, String metier, Location loc) {
        metier = metier.toLowerCase();
        despawn(metier, loc.getWorld());

        HoloLoc holo = new HoloLoc();
        holo.world = loc.getWorld().getName();
        holo.x = loc.getX();
        holo.y = loc.getY() + 1.2;
        holo.z = loc.getZ();
        emplacements.put(metier, holo);
        save(plugin);
        updateAll(plugin);
    }

    public static boolean removeHologram(JavaPlugin plugin, String metier) {
        metier = metier.toLowerCase();
        HoloLoc holo = emplacements.remove(metier);
        if (holo == null) return false;
        World world = Bukkit.getWorld(holo.world);
        despawn(metier, world);
        save(plugin);
        return true;
    }

    public static Map<String, HoloLoc> getEmplacements() {
        return emplacements;
    }

    /** A appeler depuis le thread principal uniquement (spawn d'entites) */
    public static void updateAll(JavaPlugin plugin) {
        for (Map.Entry<String, HoloLoc> entry : emplacements.entrySet()) {
            String metier = entry.getKey();
            HoloLoc holo = entry.getValue();

            World world = Bukkit.getWorld(holo.world);
            if (world == null) continue;
            if (!world.isChunkLoaded(((int) holo.x) >> 4, ((int) holo.z) >> 4)) continue;

            Location loc = new Location(world, holo.x, holo.y, holo.z);
            TextDisplay display = getSpawned(metier, world);

            if (display == null) {
                for (Entity e : world.getNearbyEntities(loc, 2, 2, 2)) {
                    if (e instanceof TextDisplay && e.getScoreboardTags().contains(TAG_PREFIX + metier)) {
                        e.remove();
                    }
                }
                final String metierTag = metier;
                display = world.spawn(loc, TextDisplay.class, entity -> {
                    entity.setBillboard(Display.Billboard.CENTER);
                    entity.setShadowed(true);
                    entity.setPersistent(false);
                    entity.addScoreboardTag(TAG_PREFIX + metierTag);
                });
                entites.put(metier, display.getUniqueId());
            }

            display.text(LegacyComponentSerializer.legacySection().deserialize(buildText(metier)));
        }
    }

    private static TextDisplay getSpawned(String metier, World world) {
        UUID id = entites.get(metier);
        if (id == null || world == null) return null;
        Entity e = Bukkit.getEntity(id);
        if (e instanceof TextDisplay && e.isValid()) return (TextDisplay) e;
        return null;
    }

    private static void despawn(String metier, World world) {
        TextDisplay display = getSpawned(metier, world);
        if (display != null) display.remove();
        entites.remove(metier);
    }

    private static String buildText(String metier) {
        List<Entree> entrees = new ArrayList<>();
        String metierCapitalise = capitalize(metier);

        for (Map.Entry<UUID, PlayerServer> entry : PlayerserverHashMap.getInstance().getHashMapPlayer().entrySet()) {
            PlayerServer ps = entry.getValue();
            int niveau = getNiveau(ps, metier);
            entrees.add(new Entree(ps.getName(), niveau));
        }

        entrees.sort(Comparator.comparingInt(Entree::niveau).reversed());

        StringBuilder sb = new StringBuilder();
        sb.append("§6✦ Top ").append(metierCapitalise).append(" ✦\n");

        if (entrees.isEmpty()) {
            sb.append("§7En attente de joueurs...");
            return sb.toString();
        }

        String[] medailles = { "§6#1", "§7#2", "§c#3", "§f#4", "§f#5" };
        int limite = Math.min(TOP_N, entrees.size());
        for (int i = 0; i < limite; i++) {
            Entree e = entrees.get(i);
            sb.append(medailles[i]).append(" §f").append(e.nom()).append(" §7- niveau §b").append(e.niveau());
            if (i < limite - 1) sb.append("\n");
        }

        return sb.toString();
    }

    private static int getNiveau(PlayerServer ps, String metier) {
        return switch (metier.toLowerCase()) {
            case "mineur" -> ps.getCobblestonegeneratorlevel();
            case "farmeur" -> ps.getHoueLevel();
            case "pecheur" -> ps.getCanneLevel();
            case "agriculteur" -> ps.getHacheLevel();
            case "tueur" -> ps.getEpeeLevel();
            default -> 0;
        };
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
