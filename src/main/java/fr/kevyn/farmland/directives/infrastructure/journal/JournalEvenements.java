package fr.kevyn.farmland.directives.infrastructure.journal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Journal des événements de jeu (plugins/Farmland/evenements.log).
 *
 * Les méthodes appelées par le jeu (enregistrer, enregistrerDetail, cumuler,
 * finJoueur) ne lèvent JAMAIS d'erreur et ne font qu'ajouter en mémoire :
 * l'écriture du fichier se fait toutes les 5 s, hors du fil principal.
 * Rotation chaque jour (evenements-AAAA-MM-JJ.log), suppression après N jours.
 */
public final class JournalEvenements {

    private static final long TICKS_ECRITURE = 100L; // 5 secondes
    private static final long INTERVALLE_ALERTE_MS = 60L * 60 * 1000;
    private static final int MAX_LIGNES_EN_ATTENTE = 10_000;
    /** Taille maximale de la file en mémoire : au-delà, les lignes les plus anciennes sont jetées. */
    static final int MAX_FILE = 10_000;

    private static final ConcurrentLinkedQueue<String> file = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger tailleFile = new AtomicInteger();
    private static final AtomicInteger jetees = new AtomicInteger();
    private static final Regroupement regroupement = new Regroupement();
    private static final AtomicInteger erreurs = new AtomicInteger();
    private static final Object VERROU = new Object();

    private static volatile boolean actif = true;
    private static volatile boolean detail = false;
    private static volatile JavaPlugin plugin;
    private static volatile long regroupementMs = 5L * 60 * 1000;
    private static volatile int retentionJours = 30;

    // accès sous VERROU uniquement
    private static File dossier;
    private static LocalDate dateFichier;
    private static long dernierRegroupement = System.currentTimeMillis();
    private static long derniereAlerte;
    private static long derniereAlerteFilePleine;
    private static final List<String> nonEcrites = new ArrayList<>();
    private static int perdues;

    private JournalEvenements() {
    }

    // ===== APPELS DEPUIS LE JEU (ne lèvent jamais d'erreur) =====

    /** Un événement, une ligne. */
    public static void enregistrer(String type, String joueur, Object valeur) {
        try {
            if (!actif) return;
            ajouterFile(FormatJournal.ligne(LocalDateTime.now(), type, joueur, valeur));
        } catch (Throwable t) {
            erreurs.incrementAndGet();
        }
    }

    /** Événement secondaire : écrit seulement si journal.niveau = detail. */
    public static void enregistrerDetail(String type, String joueur, Object valeur) {
        try {
            if (!detail) return;
            enregistrer(type, joueur, valeur);
        } catch (Throwable t) {
            erreurs.incrementAndGet();
        }
    }

    /** Événement fréquent : additionné puis écrit en une ligne par fenêtre de regroupement. */
    public static void cumuler(String type, String joueur, int montant, int quantite, String unite) {
        try {
            if (!actif) return;
            regroupement.ajouter(type, joueur, montant, quantite, unite);
        } catch (Throwable t) {
            erreurs.incrementAndGet();
        }
    }

    /** À la déconnexion : écrit tout de suite les cumuls du joueur. */
    public static void finJoueur(String joueur) {
        try {
            transferer(regroupement.viderJoueur(joueur));
        } catch (Throwable t) {
            erreurs.incrementAndGet();
        }
    }

    // ===== DÉMARRAGE / ARRÊT (appelés par JournalModule et FarmlandMain) =====

    public static void demarrer(JavaPlugin p) {
        plugin = p;
        actif = p.getConfig().getBoolean("journal.actif", true);
        detail = "detail".equalsIgnoreCase(p.getConfig().getString("journal.niveau", "normal"));
        regroupementMs = Math.max(1, p.getConfig().getInt("journal.regroupement-minutes", 5)) * 60_000L;
        retentionJours = Math.max(1, p.getConfig().getInt("journal.retention-jours", 30));

        synchronized (VERROU) {
            dossier = p.getDataFolder();
            dateFichier = null;
            if (!actif) {
                viderFile();
                regroupement.vider();
            }
        }
        if (!actif) return;

        Bukkit.getScheduler().runTaskTimerAsynchronously(p, JournalEvenements::tick, TICKS_ECRITURE, TICKS_ECRITURE);
        p.getLogger().info("[Journal] Journal des événements actif (niveau "
                + (detail ? "detail" : "normal") + ", conservation " + retentionJours + " jours)");
    }

    /** À l'arrêt du plugin : écrit tout ce qui reste. */
    public static void arreter() {
        try {
            if (!actif || plugin == null) return;
            synchronized (VERROU) {
                transferer(regroupement.vider());
                ecrire();
            }
        } catch (Throwable t) {
            erreurs.incrementAndGet();
        }
    }

    // ===== TÂCHE D'ÉCRITURE (hors fil principal) =====

    private static void tick() {
        try {
            synchronized (VERROU) {
                long maintenant = System.currentTimeMillis();
                if (maintenant - dernierRegroupement >= regroupementMs) {
                    transferer(regroupement.vider());
                    dernierRegroupement = maintenant;
                }
                ecrire();
                signalerErreurs(maintenant);
                String filePleine = messageFilePleine(maintenant);
                JavaPlugin p = plugin;
                if (filePleine != null && p != null) p.getLogger().warning(filePleine);
            }
        } catch (Throwable t) {
            erreurs.incrementAndGet();
        }
    }

    /** Ajoute une ligne ; si la file dépasse MAX_FILE, jette les plus anciennes. */
    private static void ajouterFile(String ligne) {
        file.add(ligne);
        if (tailleFile.incrementAndGet() > MAX_FILE && file.poll() != null) {
            tailleFile.decrementAndGet();
            jetees.incrementAndGet();
        }
    }

    private static String retirerFile() {
        String l = file.poll();
        if (l != null) tailleFile.decrementAndGet();
        return l;
    }

    private static void viderFile() {
        while (retirerFile() != null) {
            // vide
        }
    }

    /**
     * Message de WARN si des lignes ont été jetées, au plus une fois par heure ;
     * null sinon. Remet le compteur à zéro quand le message est produit.
     */
    static String messageFilePleine(long maintenant) {
        int nb = jetees.get();
        if (nb <= 0) return null;
        if (derniereAlerteFilePleine != 0 && maintenant - derniereAlerteFilePleine < INTERVALLE_ALERTE_MS) return null;
        derniereAlerteFilePleine = maintenant;
        jetees.addAndGet(-nb);
        return "[Journal] File pleine (" + MAX_FILE + " lignes) : " + nb + " ligne(s) les plus anciennes jetée(s)";
    }

    private static void transferer(List<Regroupement.Ligne> lignes) {
        LocalDateTime maintenant = LocalDateTime.now();
        for (Regroupement.Ligne l : lignes) {
            ajouterFile(FormatJournal.ligne(maintenant, l.type(), l.joueur(), l.valeur()));
        }
    }

    /** Appelé sous VERROU. */
    private static void ecrire() {
        if (dossier == null) return;
        tourner(LocalDate.now());

        List<String> lignes = new ArrayList<>(nonEcrites);
        nonEcrites.clear();
        String l;
        while ((l = retirerFile()) != null) lignes.add(l);
        if (lignes.isEmpty()) return;

        File courant = new File(dossier, FormatJournal.FICHIER_COURANT);
        try (BufferedWriter w = Files.newBufferedWriter(courant.toPath(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (String ligne : lignes) {
                w.write(ligne);
                w.write('\n');
            }
        } catch (IOException e) {
            // on garde les lignes pour la prochaine tentative (avec une limite)
            int place = MAX_LIGNES_EN_ATTENTE - nonEcrites.size();
            if (lignes.size() > place) {
                perdues += lignes.size() - Math.max(place, 0);
                lignes = lignes.subList(0, Math.max(place, 0));
            }
            nonEcrites.addAll(lignes);
            alerter("[Journal] Impossible d'écrire " + courant.getName() + " : " + e.getMessage(),
                    System.currentTimeMillis());
        }
    }

    /** Rotation quotidienne puis purge des vieilles archives. Appelé sous VERROU. */
    private static void tourner(LocalDate aujourdHui) {
        File courant = new File(dossier, FormatJournal.FICHIER_COURANT);
        if (dateFichier == null) {
            dateFichier = courant.exists()
                    ? Instant.ofEpochMilli(courant.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : aujourdHui;
            purger(dossier, aujourdHui, retentionJours);
        }
        if (!dateFichier.isBefore(aujourdHui)) return;

        if (courant.exists()) {
            File archive = new File(dossier, FormatJournal.nomArchive(dateFichier));
            try {
                if (archive.exists()) {
                    Files.write(archive.toPath(), Files.readAllBytes(courant.toPath()), StandardOpenOption.APPEND);
                    Files.delete(courant.toPath());
                } else {
                    Files.move(courant.toPath(), archive.toPath());
                }
            } catch (IOException e) {
                alerter("[Journal] Rotation impossible vers " + archive.getName() + " : " + e.getMessage(),
                        System.currentTimeMillis());
                return; // on réessaiera au prochain passage
            }
        }
        dateFichier = aujourdHui;
        purger(dossier, aujourdHui, retentionJours);
    }

    /**
     * Supprime uniquement les fichiers evenements-AAAA-MM-JJ.log du dossier donné
     * plus vieux que retention jours. Renvoie le nombre de fichiers supprimés.
     */
    static int purger(File dossier, LocalDate aujourdHui, int retention) {
        File[] fichiers = dossier == null ? null : dossier.listFiles();
        if (fichiers == null) return 0;
        int supprimes = 0;
        for (File f : fichiers) {
            if (f.isFile() && FormatJournal.archiveASupprimer(f.getName(), aujourdHui, retention) && f.delete()) {
                supprimes++;
            }
        }
        return supprimes;
    }

    private static void signalerErreurs(long maintenant) {
        int nb = erreurs.get();
        if (nb > 0 && alerter("[Journal] " + nb + " événement(s) n'ont pas pu être enregistrés"
                + (perdues > 0 ? ", " + perdues + " ligne(s) perdue(s)" : ""), maintenant)) {
            erreurs.addAndGet(-nb);
        }
    }

    /** WARN au plus une fois par heure. Renvoie vrai si le message a été écrit. */
    private static boolean alerter(String message, long maintenant) {
        if (derniereAlerte != 0 && maintenant - derniereAlerte < INTERVALLE_ALERTE_MS) return false;
        derniereAlerte = maintenant;
        JavaPlugin p = plugin;
        if (p != null) p.getLogger().warning(message);
        return true;
    }

    // ===== POUR LES TESTS =====

    static String prochaineLigneEnAttente() {
        return retirerFile();
    }

    static int tailleFilePourTest() {
        return tailleFile.get();
    }

    static void viderPourTest() {
        viderFile();
        regroupement.vider();
        erreurs.set(0);
        jetees.set(0);
        derniereAlerteFilePleine = 0;
        actif = true;
        detail = false;
    }

    static void definirNiveauPourTest(boolean actifTest, boolean detailTest) {
        actif = actifTest;
        detail = detailTest;
    }
}
