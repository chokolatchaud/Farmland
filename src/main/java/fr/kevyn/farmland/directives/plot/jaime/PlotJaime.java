package fr.kevyn.farmland.directives.plot.jaime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.persistance.EcritureAtomique;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * « J'aime » sur les plots : /plot like sur le plot d'un autre joueur
 * (un par plot et par jour). Classement dans /plot visit et l'hologramme
 * « Top plots » (/classementadmin holo set plots). Aucun argent en jeu.
 * Enregistré dans plot_jaime.json à chaque j'aime et à l'arrêt du serveur.
 */
public final class PlotJaime {

    private static final String FICHIER = "plot_jaime.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE_FICHIER = new TypeToken<Map<String, JaimeCalc.Donnees>>() { }.getType();

    private static JavaPlugin plugin;
    private static final Map<UUID, JaimeCalc.Donnees> plots = new HashMap<>();

    private PlotJaime() {
    }

    public static void init(JavaPlugin p) {
        plugin = p;
        charger();
    }

    public static boolean actif() {
        return plugin != null;
    }

    public static int total(UUID proprietaire) {
        JaimeCalc.Donnees d = plots.get(proprietaire);
        return d == null ? 0 : d.total;
    }

    /** Propriétaires des plots les plus aimés, du premier au dernier. */
    public static List<UUID> classement() {
        return JaimeCalc.classement(plots);
    }

    /** Propriétaire du plot dans lequel se trouve ce monde, ou null (hub, autre monde). */
    public static PlayerServer proprietaireDuMonde(World monde) {
        if (monde == null) return null;
        String nom = monde.getName();
        for (PlayerServer ps : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
            if (ps.getPlotdata() != null && nom.equals(ps.getPlotdata().getPlotProprety())) return ps;
        }
        return null;
    }

    /** /plot like : aime le plot où se trouve le joueur. */
    public static void aimer(Player joueur) {
        if (plugin == null) {
            joueur.sendMessage(MessageColor.RED.apply("Les j'aime ne sont pas disponibles pour le moment."));
            return;
        }
        PlayerServer proprietaire = proprietaireDuMonde(joueur.getWorld());
        if (proprietaire == null || proprietaire.getUuid() == null) {
            joueur.sendMessage(MessageColor.RED.apply("Va sur le plot d'un joueur pour l'aimer (/plot visit)."));
            return;
        }
        if (joueur.getUniqueId().equals(proprietaire.getUuid())) {
            joueur.sendMessage(MessageColor.YELLOW.apply("Tu ne peux pas aimer ton propre plot !"));
            return; // pas d'entrée vide dans le fichier
        }
        JaimeCalc.Donnees donnees = plots.computeIfAbsent(proprietaire.getUuid(), id -> new JaimeCalc.Donnees());
        String nom = proprietaire.getName() != null ? proprietaire.getName() : "ce joueur";
        switch (JaimeCalc.aimer(donnees, joueur.getUniqueId(), proprietaire.getUuid(), LocalDate.now())) {
            case PROPRE_PLOT -> joueur.sendMessage(MessageColor.YELLOW.apply("Tu ne peux pas aimer ton propre plot !"));
            case DEJA_AUJOURDHUI -> joueur.sendMessage(MessageColor.YELLOW.apply(
                    "Tu as déjà aimé le plot de " + nom + " aujourd'hui. Reviens demain !"));
            case OK -> {
                joueur.sendMessage(MessageColor.GREEN.apply("❤ Tu aimes le plot de " + nom + " ! ("
                        + JaimeCalc.texte(donnees.total) + ")"));
                joueur.playSound(joueur.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.6f);
                Player enLigne = Bukkit.getPlayer(proprietaire.getUuid());
                if (enLigne != null) {
                    enLigne.sendMessage(MessageColor.LIGHT_PURPLE.apply("❤ " + joueur.getName() + " a aimé ton plot ! ("
                            + JaimeCalc.texte(donnees.total) + ")"));
                }
                JournalEvenements.enregistrer("plot_jaime", joueur.getName(), "plot de " + nom + ", total " + donnees.total);
                sauvegarderPlusTard();
            }
        }
    }

    /** Message cliquable envoyé en arrivant sur le plot d'un autre joueur. */
    public static void proposerJaime(Player visiteur, PlayerServer proprietaire) {
        if (plugin == null || proprietaire == null || visiteur.getUniqueId().equals(proprietaire.getUuid())) return;
        visiteur.sendMessage(Component.text("❤ Ce plot te plaît ? ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text("[J'aime]", NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/plot like"))
                        .hoverEvent(HoverEvent.showText(Component.text("Aimer ce plot (/plot like)", NamedTextColor.YELLOW))))
                .append(Component.text(" — " + JaimeCalc.texte(total(proprietaire.getUuid())), NamedTextColor.GRAY)));
    }

    // ===== Fichier plot_jaime.json =====

    private static void charger() {
        plots.clear();
        File fichier = new File(plugin.getDataFolder(), FICHIER);
        if (!fichier.exists()) return;
        try {
            Map<String, JaimeCalc.Donnees> lu = GSON.fromJson(Files.readString(fichier.toPath(), StandardCharsets.UTF_8), TYPE_FICHIER);
            if (lu == null) return;
            for (Map.Entry<String, JaimeCalc.Donnees> e : lu.entrySet()) {
                if (e.getValue() == null) continue;
                try {
                    plots.put(UUID.fromString(e.getKey()), e.getValue());
                } catch (IllegalArgumentException ignore) {
                    // clé illisible : entrée ignorée
                }
            }
        } catch (IOException | JsonParseException e) {
            // Fichier illisible : on le met de côté (jamais écrasé) et on repart de zéro.
            plots.clear();
            File copie = new File(plugin.getDataFolder(), FICHIER + ".illisible-" + System.currentTimeMillis());
            boolean garde = fichier.renameTo(copie);
            plugin.getLogger().log(Level.SEVERE, "[J'aime] Lecture de " + FICHIER + " impossible"
                    + (garde ? ", fichier gardé sous " + copie.getName() : ""), e);
        }
    }

    private static String contenu() {
        LocalDate aujourdhui = LocalDate.now();
        Map<String, JaimeCalc.Donnees> aEcrire = new HashMap<>();
        for (Map.Entry<UUID, JaimeCalc.Donnees> e : plots.entrySet()) {
            JaimeCalc.oublierVotesAnciens(e.getValue(), aujourdhui);
            aEcrire.put(e.getKey().toString(), e.getValue());
        }
        return GSON.toJson(aEcrire, TYPE_FICHIER);
    }

    private static void sauvegarderPlusTard() {
        String json = contenu();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> ecrire(json));
    }

    public static void sauvegarderMaintenant() {
        if (plugin == null) return;
        ecrire(contenu());
    }

    private static void ecrire(String json) {
        try {
            EcritureAtomique.ecrire(new File(plugin.getDataFolder(), FICHIER).toPath(), json);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[J'aime] Écriture de " + FICHIER + " impossible", e);
        }
    }
}
