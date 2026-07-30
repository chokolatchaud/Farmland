package fr.kevyn.farmland.game;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Hologrammes du guide — 100% natif (TextDisplay), meme pattern que
 * MarketHolograms : UN hologramme par section, positionnable independamment
 * (ex: la section AFK pres du spawn, la section plot pres du menu d'upgrade...).
 * Pose par /guideholo set <section>. Contenu statique (pas de rafraichissement
 * dynamique necessaire, juste un respawn si le chunk n'etait pas charge).
 */
public class GuideHologram {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String HOLO_FILE = "guide_holograms.json";
    private static final String TAG_PREFIX = "farmland_guide_holo_";

    public static class HoloLoc {
        String world;
        double x, y, z;
    }

    /** Les sections valides pour la commande */
    public static final String[] SECTIONS = { "construction", "plot", "bateau", "vote", "afk" };

    // section -> emplacement (charge depuis le fichier)
    private static Map<String, HoloLoc> emplacements = new HashMap<>();
    // section -> entite TextDisplay actuellement en jeu
    private static final Map<String, UUID> entites = new HashMap<>();

    // ===== CHARGEMENT / SAUVEGARDE =====
    public static void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Map<String, HoloLoc> data = gson.fromJson(reader, new TypeToken<Map<String, HoloLoc>>(){}.getType());
            if (data != null) emplacements = data;
            plugin.getLogger().info("[GuideHolo] " + emplacements.size() + " hologramme(s) du guide charge(s)");
        } catch (Exception e) {
            plugin.getLogger().warning("[GuideHolo] Impossible de charger " + HOLO_FILE + " : " + e.getMessage());
        }
    }

    private static void save(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(emplacements, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("[GuideHolo] Impossible de sauvegarder " + HOLO_FILE + " : " + e.getMessage());
        }
    }

    public static boolean isValidSection(String section) {
        for (String s : SECTIONS) {
            if (s.equalsIgnoreCase(section)) return true;
        }
        return false;
    }

    public static Map<String, HoloLoc> getEmplacements() {
        return emplacements;
    }

    /** Pose (ou deplace) l'hologramme d'une section a l'emplacement donne */
    public static void setHologram(JavaPlugin plugin, String section, Location loc) {
        section = section.toLowerCase();
        despawn(section, loc.getWorld());

        HoloLoc holo = new HoloLoc();
        holo.world = loc.getWorld().getName();
        holo.x = loc.getX();
        holo.y = loc.getY() + 1.2;
        holo.z = loc.getZ();
        emplacements.put(section, holo);
        save(plugin);
        updateAll(plugin);
    }

    /** Supprime l'hologramme d'une section */
    public static boolean removeHologram(JavaPlugin plugin, String section) {
        section = section.toLowerCase();
        HoloLoc holo = emplacements.remove(section);
        if (holo == null) return false;
        World world = Bukkit.getWorld(holo.world);
        despawn(section, world);
        save(plugin);
        return true;
    }

    // ===== SPAWN / MISE A JOUR =====
    public static void updateAll(JavaPlugin plugin) {
        for (Map.Entry<String, HoloLoc> entry : emplacements.entrySet()) {
            String section = entry.getKey();
            HoloLoc holo = entry.getValue();

            World world = Bukkit.getWorld(holo.world);
            if (world == null) continue;
            if (!world.isChunkLoaded(((int) holo.x) >> 4, ((int) holo.z) >> 4)) continue;

            Location loc = new Location(world, holo.x, holo.y, holo.z);
            TextDisplay display = getSpawned(section, world);

            if (display == null) {
                for (Entity e : world.getNearbyEntities(loc, 2, 2, 2)) {
                    if (e instanceof TextDisplay && e.getScoreboardTags().contains(TAG_PREFIX + section)) {
                        e.remove();
                    }
                }
                final String sectionTag = section;
                display = world.spawn(loc, TextDisplay.class, entity -> {
                    entity.setBillboard(Display.Billboard.CENTER);
                    entity.setShadowed(true);
                    entity.setPersistent(false);
                    entity.addScoreboardTag(TAG_PREFIX + sectionTag);
                });
                entites.put(section, display.getUniqueId());
            }

            display.text(LegacyComponentSerializer.legacySection().deserialize(buildText(section)));
        }
    }

    private static TextDisplay getSpawned(String section, World world) {
        UUID id = entites.get(section);
        if (id == null || world == null) return null;
        Entity e = Bukkit.getEntity(id);
        if (e instanceof TextDisplay && e.isValid()) return (TextDisplay) e;
        return null;
    }

    private static void despawn(String section, World world) {
        TextDisplay display = getSpawned(section, world);
        if (display != null) display.remove();
        entites.remove(section);
    }

    // ===== TEXTE DE CHAQUE SECTION (identique au contenu de /tuto) =====
    private static String buildText(String section) {
        switch (section.toLowerCase()) {
            case "construction":
                return "§8§m                        \n"
                     + "§e🏗 CONSTRUIS ET GAGNE\n"
                     + "§7Construis librement sur ton plot, puis\n"
                     + "§7//wand + /define <nom> pour noter ta\n"
                     + "§7structure. Elle rapporte 150-500 $FB\n"
                     + "§7toutes les 30 min, même hors ligne (20%).\n"
                     + "§7/market pour voir les prix en direct.\n"
                     + "§8§m                        ";

            case "plot":
                return "§8§m                        \n"
                     + "§6🏠 AMÉLIORE TON PLOT\n"
                     + "§7/plot buy agrandit ta bordure\n"
                     + "§7(+5 blocs par upgrade). Système infini :\n"
                     + "§7le prix monte de +20 $FB tous les 21 achats.\n"
                     + "§8§m                        ";

            case "bateau":
                return "§8§m                        \n"
                     + "§b🚤 COURSE DE BATEAUX\n"
                     + "§7/joinboat depuis N'IMPORTE OÙ pour\n"
                     + "§7rejoindre la prochaine course. Passe les\n"
                     + "§73 points de contrôle dans l'ordre puis\n"
                     + "§7franchis l'arrivée.\n"
                     + "§7Podium du jour : 500/250/100 $FB !\n"
                     + "§8§m                        ";

            case "vote":
                return "§8§m                        \n"
                     + "§d🗳 VOTE ET COSMÉTIQUES\n"
                     + "§7/vote affiche les sites de vote\n"
                     + "§7(+1h WorldEdit par vote).\n"
                     + "§7/buy cosmetic pour t'acheter un\n"
                     + "§7chapeau exclusif à porter.\n"
                     + "§8§m                        ";

            case "afk":
                return "§8§m                        \n"
                     + "§c⏱ ATTENTION À L'AFK\n"
                     + "§720 min sans bouger = statut AFK.\n"
                     + "§7Tes structures rapportent alors\n"
                     + "§7seulement 20% le temps que tu sois\n"
                     + "§7inactif. Reste actif !\n"
                     + "§8§m                        ";

            default:
                return "§7Section inconnue";
        }
    }
}
