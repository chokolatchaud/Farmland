package fr.kevyn.farmland.directives.gameplay.missions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import fr.kevyn.farmland.directives.infrastructure.Glyphes;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.persistance.EcritureAtomique;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Missions du jour : 3 par joueur et par jour, renouvelées à minuit (heure du serveur).
 * Les métiers, le marché et la course appellent avancer() ; la récompense est versée
 * dès qu'une mission est terminée. L'avancement est enregistré dans missions.json
 * (toutes les 2 minutes, à chaque mission terminée et à l'arrêt du serveur).
 *
 * Tout se passe sur le fil principal, sauf l'écriture du fichier.
 */
public final class Missions {

    private static final String FICHIER = "missions.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE_FICHIER = new TypeToken<Map<String, ProgressionJour>>() { }.getType();

    private static JavaPlugin plugin;
    private static MissionsCalc.Recompenses recompenses = MissionsCalc.Recompenses.PAR_DEFAUT;
    private static final Map<UUID, ProgressionJour> progressions = new HashMap<>();
    private static boolean modifie;

    private Missions() {
    }

    // ===== Démarrage et réglages =====

    public static void init(JavaPlugin p) {
        plugin = p;
        recompenses = lireRecompenses(p.getConfig().getConfigurationSection("missions.recompenses"));
        charger();
    }

    public static boolean actif() {
        return plugin != null;
    }

    public static MissionsCalc.Recompenses getRecompenses() {
        return recompenses;
    }

    private static MissionsCalc.Recompenses lireRecompenses(ConfigurationSection s) {
        MissionsCalc.Recompenses d = MissionsCalc.Recompenses.PAR_DEFAUT;
        if (s == null) return d;
        return new MissionsCalc.Recompenses(
                Math.max(0, s.getInt("facile", d.facile())),
                Math.max(0, s.getInt("moyen", d.moyen())),
                Math.max(0, s.getInt("difficile", d.difficile())),
                Math.max(0, s.getInt("bonus", d.bonus())));
    }

    // ===== Lecture pour l'affichage =====

    public static List<MissionsCalc.Mission> missionsDuJour(UUID joueur) {
        return MissionsCalc.tirer(joueur, LocalDate.now());
    }

    public static ProgressionJour progressionDuJour(UUID joueur) {
        ProgressionJour p = MissionsCalc.duJour(progressions.get(joueur), LocalDate.now());
        progressions.put(joueur, p);
        return p;
    }

    public static String tempsAvantNouvellesMissions() {
        return MissionsCalc.tempsAvantMinuit(LocalTime.now().toSecondOfDay());
    }

    // ===== Avancement =====

    /** Une action du joueur compte pour ses missions de ce type (ne lève jamais d'erreur). */
    public static void avancer(Player joueur, TypeMission type, int quantite) {
        if (plugin == null || joueur == null || quantite <= 0) return;
        try {
            UUID id = joueur.getUniqueId();
            List<MissionsCalc.Mission> missions = missionsDuJour(id);
            ProgressionJour p = progressionDuJour(id);
            MissionsCalc.Resultat r = MissionsCalc.avancer(p, missions, type, quantite);
            modifie = true;
            if (r.rien()) return;
            recompenser(joueur, missions, r);
            sauvegarderPlusTard();
        } catch (RuntimeException e) {
            plugin.getLogger().log(Level.WARNING, "[Missions] Erreur pendant l'avancement de " + joueur.getName(), e);
        }
    }

    private static void recompenser(Player joueur, List<MissionsCalc.Mission> missions, MissionsCalc.Resultat r) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
        for (int i : r.terminees()) {
            MissionsCalc.Mission m = missions.get(i);
            int montant = verser(joueur, ps, recompenses.de(m.difficulte()), "mission " + m.difficulte().name().toLowerCase() + " : " + m.description());
            joueur.sendMessage("§a✔ Mission du jour terminée : §f" + m.description() + " §a+" + Glyphes.argent(montant, "§6"));
        }
        if (r.bonus()) {
            int montant = verser(joueur, ps, recompenses.bonus(), "bonus 3 missions du jour");
            joueur.sendMessage("§6§l★ §eLes 3 missions du jour sont faites ! §6Bonus +" + Glyphes.argent(montant, "§6"));
        }
        joueur.playSound(joueur.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
    }

    /** Verse le montant (sans jamais faire déborder le solde) et le note dans le journal. */
    private static int verser(Player joueur, PlayerServer ps, int montant, String raison) {
        if (ps == null || montant <= 0) return 0;
        int verse = (int) Math.min(montant, (long) Integer.MAX_VALUE - Math.max(0, ps.getMoney()));
        ps.setMoney(ps.getMoney() + verse);
        JournalEvenements.enregistrer("mission", joueur.getName(), raison + " +" + verse + " $FB, solde " + ps.getMoney() + " $FB");
        return verse;
    }

    // ===== Fichier missions.json =====

    private static void charger() {
        progressions.clear();
        File fichier = new File(plugin.getDataFolder(), FICHIER);
        if (!fichier.exists()) return;
        try {
            Map<String, ProgressionJour> lu = GSON.fromJson(Files.readString(fichier.toPath(), StandardCharsets.UTF_8), TYPE_FICHIER);
            if (lu == null) return;
            String aujourdhui = LocalDate.now().toString();
            for (Map.Entry<String, ProgressionJour> e : lu.entrySet()) {
                ProgressionJour p = e.getValue();
                if (p == null || !p.estValide() || !aujourdhui.equals(p.date)) continue; // jour passé : missions renouvelées
                try {
                    progressions.put(UUID.fromString(e.getKey()), p);
                } catch (IllegalArgumentException ignore) {
                    // clé illisible : entrée ignorée
                }
            }
        } catch (IOException | JsonParseException e) {
            plugin.getLogger().log(Level.WARNING, "[Missions] Lecture de " + FICHIER + " impossible : les missions du jour repartent de zéro", e);
        }
    }

    /** Contenu du fichier, préparé sur le fil principal (seulement les progressions du jour). */
    private static String contenu() {
        String aujourdhui = LocalDate.now().toString();
        Map<String, ProgressionJour> aEcrire = new HashMap<>();
        progressions.entrySet().removeIf(e -> !aujourdhui.equals(e.getValue().date));
        for (Map.Entry<UUID, ProgressionJour> e : progressions.entrySet()) {
            aEcrire.put(e.getKey().toString(), e.getValue());
        }
        modifie = false;
        return GSON.toJson(aEcrire, TYPE_FICHIER);
    }

    /** Toutes les 2 minutes : n'écrit que s'il y a eu du changement. */
    public static void sauvegarderSiModifie() {
        if (plugin != null && modifie) sauvegarderPlusTard();
    }

    private static void sauvegarderPlusTard() {
        String json = contenu();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> ecrire(json));
    }

    /** À l'arrêt du serveur : écriture immédiate. */
    public static void sauvegarderMaintenant() {
        if (plugin == null) return;
        ecrire(contenu());
    }

    /** Écrit via un fichier temporaire renommé : un arrêt brutal ne laisse jamais un fichier à moitié écrit. */
    private static void ecrire(String json) {
        try {
            EcritureAtomique.ecrire(new File(plugin.getDataFolder(), FICHIER).toPath(), json);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[Missions] Écriture de " + FICHIER + " impossible", e);
        }
    }
}
