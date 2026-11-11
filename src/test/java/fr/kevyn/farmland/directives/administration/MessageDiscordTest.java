package fr.kevyn.farmland.directives.administration;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageDiscordTest {

    // Fausse adresse au format webhook : ce n'est pas un vrai secret.
    private static final String FAUSSE_URL = "https://exemple.invalid/api/webhooks/123/jeton-secret-de-test";

    @Test
    void messageErreurNeContientJamaisLUrl() {
        String m1 = messagediscord.messageErreur("statut", new MalformedURLException("no protocol: " + FAUSSE_URL));
        String m2 = messagediscord.messageErreur("moderation", new UnknownHostException(FAUSSE_URL));
        for (String m : new String[] { m1, m2 }) {
            assertFalse(m.contains("jeton-secret-de-test"), m);
            assertFalse(m.contains("exemple.invalid"), m);
            assertFalse(m.contains("webhooks"), m);
        }
    }

    @Test
    void messageErreurDonneLeSalonEtLeTypeDErreur() {
        assertEquals("⚠ Erreur envoi Discord (salon \"statut\") : SocketTimeoutException",
                messagediscord.messageErreur("statut", new java.net.SocketTimeoutException("Read timed out")));
    }

    @Test
    void messageErreurSansException() {
        assertTrue(messagediscord.messageErreur("message", null).endsWith(": inconnue"));
    }

    @Test
    void delaisMaximaux() {
        assertTrue(messagediscord.DELAI_CONNEXION_MS > 0 && messagediscord.DELAI_CONNEXION_MS <= 5000);
        assertTrue(messagediscord.DELAI_LECTURE_MS > 0 && messagediscord.DELAI_LECTURE_MS <= 5000);
        assertTrue(messagediscord.DELAI_ARRET_MS > 0 && messagediscord.DELAI_ARRET_MS <= 3000);
    }
}
