package fr.kevyn.farmland.directives.gameplay.guide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hologrammes du guide (entités TextDisplay, comme ceux du marché).
 * Un hologramme par thème de GuideTextes.HOLOGRAMMES, posé par
 * /guideadmin holo set <thème>. Les emplacements sont gardés dans
 * guide_holograms.json ; les entités ne sont pas sauvegardées par le monde :
 * elles sont recréées par updateAll (au démarrage puis toutes les 10 s,
 * seulement si le chunk est chargé).
 */
public final class GuideHolograms {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FICHIER = "guide_holograms.json";
    private static final String TAG_PREFIX = "farmland_guide_";
    private static final double RAYON_NETTOYAGE = 2;

    public static class HoloLoc {
        public String world;
        double x, y, z;
    }

    private static Map<String, HoloLoc> emplacements = new HashMap<>();
    private static final Map<String, UUID> entites = new HashMap<>();

    private GuideHolograms() {
    }

    public static Map<String, HoloLoc> getEmplacements() {
        return emplacements;
    }

    public static void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), FICHIER);
        if (!file.exists()) return;
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            Map<String, HoloLoc> data = GSON.fromJson(reader, new TypeToken<Map<String, HoloLoc>>() {}.getType());
            if (data != null) emplacements = data;
        } catch (IOException | RuntimeException e) {
            plugin.getLogger().warning("[Guide] Impossible de charger " + FICHIER + " : " + e.getMessage());
        }
    }

    private static void save(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), FICHIER);
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(emplacements, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("[Guide] Impossible de sauvegarder " + FICHIER + " : " + e.getMessage());
        }
    }

    /** Pose (ou déplace) l'hologramme d'un thème au-dessus de la position donnée. */
    public static void poser(JavaPlugin plugin, String theme, Location loc) {
        theme = theme.toLowerCase();
        retirerEntite(theme);
        HoloLoc holo = new HoloLoc();
        holo.world = loc.getWorld().getName();
        holo.x = loc.getX();
        holo.y = loc.getY() + 1.5;
        holo.z = loc.getZ();
        emplacements.put(theme, holo);
        save(plugin);
        updateAll();
    }

    public static boolean retirer(JavaPlugin plugin, String theme) {
        theme = theme.toLowerCase();
        if (emplacements.remove(theme) == null) return false;
        retirerEntite(theme);
        save(plugin);
        return true;
    }

    /** Fait apparaître les hologrammes manquants dont le chunk est chargé. Fil principal uniquement. */
    public static void updateAll() {
        for (Map.Entry<String, HoloLoc> entry : emplacements.entrySet()) {
            String theme = entry.getKey();
            HoloLoc holo = entry.getValue();
            String texte = GuideTextes.HOLOGRAMMES.get(theme);
            if (texte == null) continue; // thème supprimé du code

            World world = Bukkit.getWorld(holo.world);
            if (world == null || !world.isChunkLoaded(((int) Math.floor(holo.x)) >> 4, ((int) Math.floor(holo.z)) >> 4)) {
                continue;
            }
            if (entiteVivante(theme) != null) continue;

            Location loc = new Location(world, holo.x, holo.y, holo.z);
            // nettoie d'éventuels restes avant de recréer
            for (Entity e : world.getNearbyEntities(loc, RAYON_NETTOYAGE, RAYON_NETTOYAGE, RAYON_NETTOYAGE)) {
                if (e instanceof TextDisplay && e.getScoreboardTags().contains(TAG_PREFIX + theme)) e.remove();
            }
            TextDisplay display = world.spawn(loc, TextDisplay.class, d -> {
                d.setBillboard(Display.Billboard.CENTER);
                d.setShadowed(true);
                d.setLineWidth(260);
                d.setBackgroundColor(Color.fromARGB(110, 0, 0, 0));
                d.setPersistent(false);
                d.addScoreboardTag(TAG_PREFIX + theme);
                d.text(LegacyComponentSerializer.legacySection().deserialize(texte));
            });
            entites.put(theme, display.getUniqueId());
        }
    }

    private static Entity entiteVivante(String theme) {
        UUID id = entites.get(theme);
        if (id == null) return null;
        Entity e = Bukkit.getEntity(id);
        return (e != null && e.isValid()) ? e : null;
    }

    private static void retirerEntite(String theme) {
        Entity e = entiteVivante(theme);
        if (e != null) e.remove();
        entites.remove(theme);
    }
}
