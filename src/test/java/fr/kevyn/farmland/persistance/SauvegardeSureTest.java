package fr.kevyn.farmland.persistance;

import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.persistance.joueurs.PlayerSave;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SauvegardeSureTest {

    @TempDir
    Path dossier;

    @Test
    void ecritureCompleteEnUtf8SansFichierTemporaire() throws IOException {
        Path f = dossier.resolve("players").resolve("a.json");
        EcritureAtomique.ecrire(f, "{\"name\":\"Élodie ★\"}");
        EcritureAtomique.ecrire(f, "{\"name\":\"Kévin\"}");
        assertEquals("{\"name\":\"Kévin\"}", Files.readString(f, StandardCharsets.UTF_8));
        assertFalse(Files.exists(dossier.resolve("players").resolve("a.json.tmp")), "pas de fichier temporaire laissé");
        assertEquals("{\"name\":\"Kévin\"}", FileManager.Readfile(f.toFile()));
    }

    @Test
    void vieuxFichierMalEncodeToujoursLisible() throws IOException {
        Path f = dossier.resolve("ancien.json");
        // « é » écrit en Windows-1252 (octet 0xE9 seul) : invalide en UTF-8
        Files.write(f, new byte[] {'{', '"', 'n', '"', ':', '"', 'K', (byte) 0xE9, 'v', '"', '}'});
        String lu = FileManager.Readfile(f.toFile());
        assertTrue(lu.startsWith("{\"n\":\"K") && lu.endsWith("v\"}"), "lecture jamais vide : " + lu);
    }

    @Test
    void ancienContenuGardeSiLEcritureEchoue() throws IOException {
        Path f = dossier.resolve("b.json");
        EcritureAtomique.ecrire(f, "ancien");
        Files.createDirectory(dossier.resolve("b.json.tmp")); // le fichier temporaire ne peut pas être écrit
        assertThrows(IOException.class, () -> EcritureAtomique.ecrire(f, "nouveau"));
        assertEquals("ancien", Files.readString(f, StandardCharsets.UTF_8));
        assertFalse(FileManager.savefile(f.toFile(), "nouveau"), "savefile ne lève pas d'erreur, il signale l'échec");
        assertEquals("ancien", Files.readString(f, StandardCharsets.UTF_8));
    }

    @Test
    void uneEcritureEnRetardNeRemplacePasUnFichierPlusRecent() throws IOException {
        File players = dossier.resolve("players").toFile();
        PlayerServer ps = new PlayerServer();
        setUuid(ps, UUID.fromString("00000000-0000-0000-0000-0000000000aa"));

        setMoney(ps, 100);
        PlayerSave.Instantane ancien = PlayerSave.preparer(players, ps);   // ex. sauvegarde automatique
        setMoney(ps, 250);
        PlayerSave.Instantane recent = PlayerSave.preparer(players, ps);   // ex. déconnexion
        assertNotNull(ancien);
        assertNotNull(recent);

        assertTrue(PlayerSave.ecrire(recent));
        assertFalse(PlayerSave.ecrire(ancien), "l'ancien instantané arrive en retard : ignoré");
        String contenu = Files.readString(recent.fichier().toPath(), StandardCharsets.UTF_8);
        assertTrue(contenu.contains("\"money\": 250"), contenu);
    }

    private static void setUuid(PlayerServer ps, UUID id) {
        try {
            var champ = PlayerServer.class.getDeclaredField("uuid");
            champ.setAccessible(true);
            champ.set(ps, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static void setMoney(PlayerServer ps, int argent) {
        ps.setMoney(argent);
    }
}
