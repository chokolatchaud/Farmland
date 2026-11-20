package fr.kevyn.farmland.persistance;

import fr.kevyn.farmland.FarmlandMain;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Lecture et écriture des fichiers des joueurs, toujours en UTF-8.
 * L'écriture passe par un fichier temporaire renommé ensuite (EcritureAtomique) :
 * un arrêt brutal pendant une sauvegarde ne peut plus vider ni couper le fichier,
 * l'ancien contenu reste en place tant que le nouveau n'est pas complet.
 */
public class FileManager {

    public static void createfile(File file) throws IOException {
        if (!file.exists()) {

            file.getParentFile().mkdirs();

            file.createNewFile();
        }
    }

    /**
     * Écrit le fichier en entier ou pas du tout. Ne lève jamais d'erreur :
     * en cas d'échec, l'ancien fichier est gardé et l'erreur est journalisée.
     * @return vrai si le fichier a été écrit
     */
    public static boolean savefile(File file, String text) {
        try {
            EcritureAtomique.ecrire(file.toPath(), text);
            return true;
        } catch (IOException | RuntimeException e) {
            journaliser(Level.SEVERE, "Impossible d'écrire " + file.getName() + " (ancien fichier conservé)", e);
            return false;
        }
    }

    /**
     * Lit le fichier en UTF-8. Un octet mal encodé (vieux fichier) est remplacé au lieu
     * de faire échouer la lecture : un fichier illisible ferait passer le joueur pour
     * un nouveau joueur et son fichier serait remplacé (même comportement qu'avant).
     */
    public static String Readfile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            journaliser(Level.SEVERE, "Impossible de lire " + file.getName(), e);
        }
        return "";
    }

    private static void journaliser(Level niveau, String message, Exception e) {
        try {
            JavaPlugin.getPlugin(FarmlandMain.class).getLogger().log(niveau, message, e);
        } catch (IllegalStateException | IllegalArgumentException | NoClassDefFoundError horsServeur) {
            // tests JUnit : pas de plugin chargé, rien à journaliser
        }
    }
}
