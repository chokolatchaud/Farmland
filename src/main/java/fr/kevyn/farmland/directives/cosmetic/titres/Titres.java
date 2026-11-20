package fr.kevyn.farmland.directives.cosmetic.titres;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.persistance.EcritureAtomique;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Titres des joueurs (titres.json) : achat, choix du titre porté, préfixe du chat.
 * Le chat est traité hors du fil principal : la table est concurrente.
 */
public final class Titres {

    private static final String FICHIER = "titres.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE_FICHIER = new TypeToken<Map<String, TitresCalc.Donnees>>() { }.getType();

    private static JavaPlugin plugin;
    private static final Map<UUID, TitresCalc.Donnees> joueurs = new ConcurrentHashMap<>();

    private Titres() {
    }

    public static void init(JavaPlugin p) {
        plugin = p;
        charger();
    }

    public static boolean actif() {
        return plugin != null;
    }

    private static TitresCalc.Donnees donnees(UUID id) {
        return joueurs.computeIfAbsent(id, k -> new TitresCalc.Donnees());
    }

    /** Niveau du métier d'un titre au mérite (0 pour un titre à acheter ou un joueur inconnu). */
    private static int niveau(UUID id, Titre titre) {
        if (titre == null || !titre.auMerite()) return 0;
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(id);
        return ps == null ? 0 : RecompenseUtil.getLevel(ps, titre.getMetier());
    }

    public static boolean possede(UUID id, Titre titre) {
        TitresCalc.Donnees d = joueurs.get(id);
        return TitresCalc.possede(d == null ? new TitresCalc.Donnees() : d, titre, niveau(id, titre));
    }

    public static Titre titrePorte(UUID id) {
        TitresCalc.Donnees d = joueurs.get(id);
        return d == null ? null : Titre.depuisNom(d.actif);
    }

    /** Préfixe du chat (« §6[Magnat]§r ») ou "" : appelé depuis le fil du chat. */
    public static String prefixeChat(UUID id) {
        if (plugin == null) return "";
        TitresCalc.Donnees d = joueurs.get(id);
        if (d == null) return "";
        return TitresCalc.prefixe(d, niveau(id, Titre.depuisNom(d.actif)));
    }

    /** Achète le titre (déjà confirmé par le joueur), le débite et le porte tout de suite. */
    public static void acheter(Player joueur, Titre titre) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
        if (plugin == null || ps == null) return;
        TitresCalc.Donnees d = donnees(joueur.getUniqueId());
        switch (TitresCalc.acheter(d, titre, ps.getMoney())) {
            case AU_MERITE -> joueur.sendMessage(MessageColor.YELLOW.apply("Ce titre ne s'achète pas : il se gagne au niveau "
                    + Titre.NIVEAU_MERITE + " du métier."));
            case DEJA_POSSEDE -> joueur.sendMessage(MessageColor.YELLOW.apply("Tu as déjà ce titre."));
            case SOLDE_INSUFFISANT -> joueur.sendMessage(MessageColor.RED.apply("Solde insuffisant : il te faut "
                    + titre.getPrix() + " $FB (tu as " + ps.getMoney() + " $FB)."));
            case OK -> {
                ps.setMoney(ps.getMoney() - titre.getPrix());
                d.actif = titre.name();
                JournalEvenements.enregistrer("achat_titre", joueur.getName(), titre.name() + " -" + titre.getPrix()
                        + " $FB, solde " + ps.getMoney() + " $FB");
                joueur.sendMessage(MessageColor.GREEN.apply("✔ Titre acheté et porté : ") + titre.getAffichage()
                        + MessageColor.GRAY.apply(" (-" + titre.getPrix() + " $FB)"));
                joueur.playSound(joueur.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.4f);
                sauvegarderPlusTard();
            }
        }
    }

    public static void equiper(Player joueur, Titre titre) {
        if (plugin == null) return;
        TitresCalc.Donnees d = donnees(joueur.getUniqueId());
        if (TitresCalc.equiper(d, titre, niveau(joueur.getUniqueId(), titre)) == TitresCalc.Equipement.NON_POSSEDE) {
            joueur.sendMessage(MessageColor.RED.apply("Tu n'as pas encore ce titre."));
            return;
        }
        joueur.sendMessage(MessageColor.GREEN.apply("✔ Tu portes maintenant le titre ") + titre.getAffichage());
        sauvegarderPlusTard();
    }

    public static void retirer(Player joueur) {
        TitresCalc.Donnees d = joueurs.get(joueur.getUniqueId());
        if (plugin == null || d == null || d.actif == null) return;
        d.actif = null;
        joueur.sendMessage(MessageColor.GRAY.apply("Tu ne portes plus de titre."));
        sauvegarderPlusTard();
    }

    // ===== Fichier titres.json =====

    private static void charger() {
        joueurs.clear();
        File fichier = new File(plugin.getDataFolder(), FICHIER);
        if (!fichier.exists()) return;
        try {
            Map<String, TitresCalc.Donnees> lu = GSON.fromJson(Files.readString(fichier.toPath(), StandardCharsets.UTF_8), TYPE_FICHIER);
            if (lu == null) return;
            for (Map.Entry<String, TitresCalc.Donnees> e : lu.entrySet()) {
                if (e.getValue() == null) continue;
                try {
                    joueurs.put(UUID.fromString(e.getKey()), e.getValue());
                } catch (IllegalArgumentException ignore) {
                    // clé illisible : entrée ignorée
                }
            }
        } catch (IOException | JsonParseException e) {
            // Fichier illisible : mis de côté (jamais écrasé), les achats restent récupérables à la main.
            joueurs.clear();
            File copie = new File(plugin.getDataFolder(), FICHIER + ".illisible-" + System.currentTimeMillis());
            boolean garde = fichier.renameTo(copie);
            plugin.getLogger().log(Level.SEVERE, "[Titres] Lecture de " + FICHIER + " impossible"
                    + (garde ? ", fichier gardé sous " + copie.getName() : ""), e);
        }
    }

    private static String contenu() {
        Map<String, TitresCalc.Donnees> aEcrire = new HashMap<>();
        for (Map.Entry<UUID, TitresCalc.Donnees> e : joueurs.entrySet()) {
            TitresCalc.Donnees d = e.getValue();
            boolean vide = (d.possedes == null || d.possedes.isEmpty()) && d.actif == null;
            if (!vide) aEcrire.put(e.getKey().toString(), d);
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
            plugin.getLogger().log(Level.WARNING, "[Titres] Écriture de " + FICHIER + " impossible", e);
        }
    }
}
