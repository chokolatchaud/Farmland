package fr.kevyn.farmland.directives.plot.une;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * « Plot à la une » : enchères hebdomadaires pour apparaître en tête de /plot visit.
 * L'argent misé est dépensé (jamais rendu) ; le classement repart à zéro chaque lundi.
 * Enregistré dans plot_une.json à chaque mise, au changement de semaine et à l'arrêt.
 */
public final class PlotUne {

    private static final String FICHIER = "plot_une.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final long CONFIRMATION_MS = 30_000L;

    private static JavaPlugin plugin;
    private static UneCalc.Donnees donnees = new UneCalc.Donnees();
    /** /plot miser : montant en attente de confirmation (retaper la même commande). */
    private static final Map<UUID, long[]> confirmations = new HashMap<>();

    private PlotUne() {
    }

    public static void init(JavaPlugin p) {
        plugin = p;
        charger();
        verifierSemaine();
    }

    public static boolean actif() {
        return plugin != null;
    }

    /** Toutes les 10 minutes et avant chaque lecture : remise à zéro le lundi. */
    public static void verifierSemaine() {
        if (plugin == null) return;
        String avant = donnees.semaine;
        UneCalc.FinDeSemaine fin = UneCalc.verifierSemaine(donnees, LocalDate.now());
        if (avant != null && avant.equals(donnees.semaine)) return;
        if (fin != null) {
            String nom = nom(fin.gagnant());
            JournalEvenements.enregistrer("plot_une_fin", nom, "semaine " + avant + ", mise " + fin.mise() + " $FB");
            Bukkit.broadcastMessage("§6★ Plot à la une de la semaine : §e" + nom + " §6avec §e"
                    + UneCalc.montant(fin.mise()) + " $FB§6 ! Nouvelles enchères ouvertes (/shop).");
        }
        sauvegarderPlusTard();
    }

    public static long mise(UUID proprietaire) {
        return UneCalc.mise(donnees, proprietaire);
    }

    public static int rang(UUID proprietaire) {
        return UneCalc.rang(donnees, proprietaire);
    }

    public static List<UUID> classement() {
        return UneCalc.classement(donnees);
    }

    public static String tempsAvantRemiseAZero() {
        return UneCalc.tempsAvantLundi(LocalDateTime.now());
    }

    /** /plot miser <montant> : demande une confirmation (même commande dans les 30 s), puis mise. */
    public static void miserAvecConfirmation(Player joueur, long montant) {
        if (montant < UneCalc.MISE_MINIMALE) {
            joueur.sendMessage(MessageColor.RED.apply("Mise minimale : " + UneCalc.montant(UneCalc.MISE_MINIMALE) + " $FB."));
            return;
        }
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
        if (ps != null && ps.getMoney() < montant) {
            joueur.sendMessage(MessageColor.RED.apply("Solde insuffisant : tu as " + UneCalc.montant(ps.getMoney()) + " $FB."));
            return;
        }
        long maintenant = System.currentTimeMillis();
        long[] attente = confirmations.get(joueur.getUniqueId());
        if (attente != null && attente[0] == montant && maintenant - attente[1] <= CONFIRMATION_MS) {
            confirmations.remove(joueur.getUniqueId());
            miser(joueur, montant);
            return;
        }
        confirmations.put(joueur.getUniqueId(), new long[] {montant, maintenant});
        joueur.sendMessage(MessageColor.GOLD.apply("★ Miser " + UneCalc.montant(montant) + " $FB sur ton plot ?"));
        joueur.sendMessage(MessageColor.GRAY.apply("L'argent misé est dépensé (jamais rendu). Classement remis à zéro lundi."));
        joueur.sendMessage(MessageColor.YELLOW.apply("Retape /plot miser " + montant + " dans les 30 s pour confirmer."));
    }

    /** Mise déjà confirmée par le joueur : vérifie, débite, classe. */
    public static void miser(Player joueur, long montant) {
        if (plugin == null) return;
        verifierSemaine();
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
        if (ps == null || ps.getPlotdata() == null) {
            joueur.sendMessage(MessageColor.RED.apply("Il te faut un plot pour miser."));
            return;
        }
        UUID id = joueur.getUniqueId();
        List<UUID> avant = classement();
        UUID premierAvant = avant.isEmpty() ? null : avant.get(0);
        switch (UneCalc.miser(donnees, id, montant, ps.getMoney())) {
            case MONTANT_TROP_PETIT -> joueur.sendMessage(MessageColor.RED.apply("Mise minimale : "
                    + UneCalc.montant(UneCalc.MISE_MINIMALE) + " $FB."));
            case SOLDE_INSUFFISANT -> joueur.sendMessage(MessageColor.RED.apply("Solde insuffisant : tu as "
                    + UneCalc.montant(ps.getMoney()) + " $FB."));
            case OK -> {
                ps.setMoney((int) (ps.getMoney() - montant)); // montant <= solde : jamais négatif
                long total = mise(id);
                int rang = rang(id);
                JournalEvenements.enregistrer("plot_une_mise", joueur.getName(), "-" + montant + " $FB, total " + total
                        + " $FB, rang " + rang + ", solde " + ps.getMoney() + " $FB");
                joueur.sendMessage(MessageColor.GREEN.apply("★ Mise de " + UneCalc.montant(montant) + " $FB enregistrée ! Total : "
                        + UneCalc.montant(total) + " $FB, tu es " + rang + (rang == 1 ? "er" : "e") + "."));
                joueur.playSound(joueur.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.0f);
                if (rang == 1 && !id.equals(premierAvant)) {
                    Bukkit.broadcastMessage("§6★ §e" + joueur.getName() + " §6passe en tête des plots à la une avec §e"
                            + UneCalc.montant(total) + " $FB §6! (/plot visit)");
                }
                sauvegarderPlusTard();
            }
        }
    }

    private static String nom(UUID id) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getHashMapPlayer().get(id);
        return ps != null && ps.getName() != null ? ps.getName() : "?";
    }

    // ===== Fichier plot_une.json =====

    private static void charger() {
        donnees = new UneCalc.Donnees();
        File fichier = new File(plugin.getDataFolder(), FICHIER);
        if (!fichier.exists()) return;
        try {
            UneCalc.Donnees lu = GSON.fromJson(Files.readString(fichier.toPath(), StandardCharsets.UTF_8), UneCalc.Donnees.class);
            if (lu != null) donnees = lu;
            if (donnees.mises == null) donnees.mises = new HashMap<>();
        } catch (IOException | JsonParseException e) {
            donnees = new UneCalc.Donnees();
            File copie = new File(plugin.getDataFolder(), FICHIER + ".illisible-" + System.currentTimeMillis());
            boolean garde = fichier.renameTo(copie);
            plugin.getLogger().log(Level.SEVERE, "[Plot à la une] Lecture de " + FICHIER + " impossible"
                    + (garde ? ", fichier gardé sous " + copie.getName() : ""), e);
        }
    }

    private static void sauvegarderPlusTard() {
        String json = GSON.toJson(donnees);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> ecrire(json));
    }

    public static void sauvegarderMaintenant() {
        if (plugin == null) return;
        ecrire(GSON.toJson(donnees));
    }

    private static void ecrire(String json) {
        try {
            EcritureAtomique.ecrire(new File(plugin.getDataFolder(), FICHIER).toPath(), json);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[Plot à la une] Écriture de " + FICHIER + " impossible", e);
        }
    }
}
