package fr.kevyn.farmland.persistance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Écriture d'un fichier en UTF-8 via un fichier temporaire renommé ensuite :
 * un arrêt brutal ne laisse jamais un fichier à moitié écrit.
 * Utilisé par les nouveaux fichiers (missions.json, plot_jaime.json).
 */
public final class EcritureAtomique {

    private EcritureAtomique() {
    }

    public static synchronized void ecrire(Path cible, String contenu) throws IOException {
        Path dossier = cible.toAbsolutePath().getParent();
        if (dossier != null) Files.createDirectories(dossier);
        Path temporaire = cible.resolveSibling(cible.getFileName() + ".tmp");
        Files.writeString(temporaire, contenu, StandardCharsets.UTF_8);
        try {
            Files.move(temporaire, cible, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaire, cible, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
