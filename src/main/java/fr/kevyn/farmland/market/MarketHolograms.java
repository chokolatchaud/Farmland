package fr.kevyn.farmland.market;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import fr.kevyn.farmland.save.MarketSave;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Hologrammes du marché — 100% natif (entités TextDisplay, aucune dépendance).
 * Un hologramme par coefficient, posé par /marketadmin holo set <coef>.
 * Les emplacements sont sauvegardés dans holograms.json et les hologrammes
 * se mettent à jour à chaque changement du marché (updateAll).
 * ATTENTION : updateAll doit être appelé depuis le thread principal (spawn d'entités).
 */
public class MarketHolograms {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String HOLO_FILE = "holograms.json";
    private static final String TAG_PREFIX = "farmland_holo_";

    /** Emplacement sauvegardé d'un hologramme */
    public static class HoloLoc {
        String world;
        double x, y, z;
    }

    // coef -> emplacement (chargé depuis le fichier)
    private static Map<String, HoloLoc> emplacements = new HashMap<>();
    // coef -> entité TextDisplay actuellement en jeu
    private static final Map<String, UUID> entites = new HashMap<>();

    /** Les coefs valides pour la commande (sans accents) */
    public static final String[] COEFS = { "creativite", "architecture", "densite", "equilibre", "finition" };

    // ===== CHARGEMENT / SAUVEGARDE DU FICHIER =====
    public static void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), HOLO_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Map<String, HoloLoc> data = gson.fromJson(reader, new TypeToken<Map<String, HoloLoc>>(){}.getType());
            if (data != null) emplacements = data;
            plugin.getLogger().info("[Holo] " + emplacements.size() + " hologramme(s) de marché chargé(s)");
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

    // ===== COMMANDES =====
    public static boolean isValidCoef(String coef) {
        for (String c : COEFS) {
            if (c.equalsIgnoreCase(coef)) return true;
        }
        return false;
    }

    /** Pose (ou déplace) l'hologramme d'un coef à l'emplacement donné */
    public static void setHologram(JavaPlugin plugin, String coef, Location loc) {
        coef = coef.toLowerCase();
        despawn(coef, loc.getWorld());

        HoloLoc holo = new HoloLoc();
        holo.world = loc.getWorld().getName();
        holo.x = loc.getX();
        holo.y = loc.getY() + 1.2; // au-dessus de la tête du joueur
        holo.z = loc.getZ();
        emplacements.put(coef, holo);
        save(plugin);
        updateAll(plugin);
    }

    /** Supprime l'hologramme d'un coef */
    public static boolean removeHologram(JavaPlugin plugin, String coef) {
        coef = coef.toLowerCase();
        HoloLoc holo = emplacements.remove(coef);
        if (holo == null) return false;
        World world = Bukkit.getWorld(holo.world);
        despawn(coef, world);
        save(plugin);
        return true;
    }

    public static Map<String, HoloLoc> getEmplacements() {
        return emplacements;
    }

    // ===== SPAWN / MISE À JOUR =====

    /** Fait apparaître les hologrammes manquants et rafraîchit leur texte.
     *  À appeler à chaque changement du marché (thread principal uniquement !) */
    public static void updateAll(JavaPlugin plugin) {
        for (Map.Entry<String, HoloLoc> entry : emplacements.entrySet()) {
            String coef = entry.getKey();
            HoloLoc holo = entry.getValue();

            World world = Bukkit.getWorld(holo.world);
            if (world == null) continue;
            if (!world.isChunkLoaded(((int) holo.x) >> 4, ((int) holo.z) >> 4)) continue;

            Location loc = new Location(world, holo.x, holo.y, holo.z);
            TextDisplay display = getSpawned(coef, world);

            if (display == null) {
                // nettoie d'éventuels restes du même hologramme avant de respawn
                for (Entity e : world.getNearbyEntities(loc, 2, 2, 2)) {
                    if (e instanceof TextDisplay && e.getScoreboardTags().contains(TAG_PREFIX + coef)) {
                        e.remove();
                    }
                }
                final String coefTag = coef;
                display = world.spawn(loc, TextDisplay.class, entity -> {
                    entity.setBillboard(Display.Billboard.CENTER);
                    entity.setShadowed(true);
                    entity.setPersistent(false); // on gère nous-mêmes le respawn au démarrage
                    entity.addScoreboardTag(TAG_PREFIX + coefTag);
                });
                entites.put(coef, display.getUniqueId());
            }

            display.text(LegacyComponentSerializer.legacySection().deserialize(buildText(plugin, coef)));
        }
    }

    private static TextDisplay getSpawned(String coef, World world) {
        UUID id = entites.get(coef);
        if (id == null || world == null) return null;
        Entity e = Bukkit.getEntity(id);
        if (e instanceof TextDisplay && e.isValid()) return (TextDisplay) e;
        return null;
    }

    private static void despawn(String coef, World world) {
        TextDisplay display = getSpawned(coef, world);
        if (display != null) display.remove();
        entites.remove(coef);
    }

    // ===== TEXTE DE L'HOLOGRAMME =====
    private static String buildText(JavaPlugin plugin, String coef) {
        Market actuel = MarketSave.loadMarket(plugin);
        if (actuel == null) {
            return "§6✦ Marché ✦\n§7En attente de données...";
        }

        int valeur = getCoefValue(actuel, coef);
        List<fr.kevyn.farmland.save.MarketSave.MarketSnapshot> history = MarketSave.getFullHistory(plugin);

        // tendance par rapport à l'avant-dernier snapshot (avec pourcentage)
        String tendance = "§7─ stable";
        if (history.size() >= 2) {
            int precedent = getCoefValue(history.get(history.size() - 2).market, coef);
            int diff = valeur - precedent;
            if (precedent != 0 && diff != 0) {
                float pct = (diff * 100f) / precedent;
                String pctStr = String.format("%.1f", Math.abs(pct));
                if (diff > 0) tendance = "§a▲ +" + diff + "$ §2(+" + pctStr + "%)";
                else tendance = "§c▼ " + diff + "$ §4(-" + pctStr + "%)";
            }
        }

        // mini-courbe des 12 derniers points + stats sur l'historique
        String courbe = buildSparkline(history, coef);
        String stats = buildStats(history, coef);

        // heure du dernier point de l'historique ("2026-07-15 18:33:35" -> "18:33")
        String heure = "";
        if (!history.isEmpty()) {
            String t = history.get(history.size() - 1).timestamp;
            if (t != null && t.length() >= 16) heure = t.substring(11, 16);
        }

        return "§8§m                              \n"
             + "§6✦ §e§l" + getDisplayName(coef).toUpperCase() + " §6✦\n"
             + "§f§l" + valeur + " $FB  " + tendance + "\n"
             + courbe + "\n"
             + stats + "\n"
             + "§8Marché mis à jour : " + heure + " §8— §7/market\n"
             + "§8§m                              ";
    }

    /** Mini-courbe façon bourse avec les 12 derniers points de l'historique.
     *  Chaque barre est verte si le prix monte, rouge s'il baisse. */
    private static String buildSparkline(List<fr.kevyn.farmland.save.MarketSave.MarketSnapshot> history, String coef) {
        if (history.size() < 2) return "§7(pas encore d'historique)";

        int nb = Math.min(12, history.size());
        int[] valeurs = new int[nb];
        for (int i = 0; i < nb; i++) {
            valeurs[i] = getCoefValue(history.get(history.size() - nb + i).market, coef);
        }

        int min = valeurs[0], max = valeurs[0];
        for (int v : valeurs) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        String[] barres = { "▁", "▂", "▃", "▄", "▅", "▆", "▇", "█" };
        StringBuilder courbe = new StringBuilder();
        for (int i = 0; i < nb; i++) {
            int niveau = 0;
            if (max > min) niveau = (valeurs[i] - min) * (barres.length - 1) / (max - min);
            // couleur : vert si ça monte, rouge si ça baisse, gris si stable
            if (i == 0 || valeurs[i] == valeurs[i - 1]) courbe.append("§7");
            else if (valeurs[i] > valeurs[i - 1]) courbe.append("§a");
            else courbe.append("§c");
            courbe.append(barres[niveau]);
        }
        return courbe.toString();
    }

    /** Min / Max / Moyenne sur tout l'historique */
    private static String buildStats(List<fr.kevyn.farmland.save.MarketSave.MarketSnapshot> history, String coef) {
        if (history.isEmpty()) return "";
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, somme = 0;
        for (fr.kevyn.farmland.save.MarketSave.MarketSnapshot snap : history) {
            int v = getCoefValue(snap.market, coef);
            if (v < min) min = v;
            if (v > max) max = v;
            somme += v;
        }
        int moyenne = somme / history.size();
        return "§7Min §f" + min + "$ §8│ §7Max §f" + max + "$ §8│ §7Moy §f" + moyenne + "$";
    }

    private static int getCoefValue(Market market, String coef) {
        switch (coef.toLowerCase()) {
            case "creativite": return market.getMoneyforcoefCréativité();
            case "architecture": return market.getMoneyforcoefArchitecture();
            case "densite": return market.getMoneyforcoefDensité();
            case "equilibre": return market.getMoneyforcoefÉquilibre();
            case "finition": return market.getMoneyforcoefFinition();
            default: return 0;
        }
    }

    private static String getDisplayName(String coef) {
        switch (coef.toLowerCase()) {
            case "creativite": return "Créativité";
            case "architecture": return "Architecture";
            case "densite": return "Densité";
            case "equilibre": return "Équilibre";
            case "finition": return "Finition";
            default: return coef;
        }
    }
}
