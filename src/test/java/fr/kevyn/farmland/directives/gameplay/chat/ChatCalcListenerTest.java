package fr.kevyn.farmland.directives.gameplay.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatCalcListenerTest {

    @AfterEach
    void reset() {
        ChatCalcListener.definirReponsePourTest(null);
    }

    /** Recalcule le résultat à partir du texte affiché ("a + b", "a - b", "a × (b)"). */
    private static int evaluer(String texte) {
        String t = texte.replace("(", "").replace(")", "");
        String[] p = t.split(" ");
        int a = Integer.parseInt(p[0]);
        int b = Integer.parseInt(p[2]);
        return switch (p[1]) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "×" -> a * b;
            default -> throw new IllegalStateException(texte);
        };
    }

    @Test
    void chaqueCalculAfficheCorrespondASonResultat() {
        Random r = new Random(42);
        for (int i = 0; i < 10_000; i++) {
            ChatCalcListener.Calcul c = ChatCalcListener.genererCalcul(r);
            assertEquals(evaluer(c.texte()), c.resultat(), c.texte());
        }
    }

    @Test
    void lesTroisOperationsEtLesNegatifsApparaissent() {
        Random r = new Random(7);
        boolean add = false, sous = false, mult = false, multNeg = false, sousNeg = false;
        for (int i = 0; i < 10_000; i++) {
            ChatCalcListener.Calcul c = ChatCalcListener.genererCalcul(r);
            if (c.texte().contains(" + ")) add = true;
            if (c.texte().contains(" - ")) { sous = true; if (c.resultat() < 0) sousNeg = true; }
            if (c.texte().contains(" × ")) { mult = true; if (c.resultat() < 0) multNeg = true; }
        }
        assertTrue(add && sous && mult, "les 3 opérations");
        assertTrue(sousNeg, "soustractions négatives (voulues)");
        assertTrue(multNeg, "multiplications négatives");
    }

    @Test
    void bornesDesNombres() {
        Random r = new Random(3);
        for (int i = 0; i < 10_000; i++) {
            ChatCalcListener.Calcul c = ChatCalcListener.genererCalcul(r);
            String[] p = c.texte().replace("(", "").replace(")", "").split(" ");
            int a = Math.abs(Integer.parseInt(p[0]));
            int b = Math.abs(Integer.parseInt(p[2]));
            int max = p[1].equals("×") ? 12 : 50;
            assertTrue(a >= 1 && a <= max && b >= 1 && b <= max, c.texte());
        }
    }

    @Test
    void mauvaiseReponseNeFermePasLeCalcul() {
        ChatCalcListener.definirReponsePourTest(-36);
        assertFalse(ChatCalcListener.tenterReponse(36));
        assertTrue(ChatCalcListener.tenterReponse(-36));
        assertFalse(ChatCalcListener.tenterReponse(-36), "déjà gagné : plus de gagnant");
    }

    @Test
    void aucunCalculEnCours() {
        assertFalse(ChatCalcListener.tenterReponse(0));
    }

    @Test
    void unSeulGagnantMemeEnMemeTemps() throws Exception {
        for (int essai = 0; essai < 200; essai++) {
            ChatCalcListener.definirReponsePourTest(42);
            int joueurs = 20;
            ExecutorService pool = Executors.newFixedThreadPool(joueurs);
            CountDownLatch depart = new CountDownLatch(1);
            AtomicInteger gagnants = new AtomicInteger();
            for (int j = 0; j < joueurs; j++) {
                pool.submit(() -> {
                    depart.await();
                    if (ChatCalcListener.tenterReponse(42)) gagnants.incrementAndGet();
                    return null;
                });
            }
            depart.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
            assertEquals(1, gagnants.get(), "essai " + essai);
        }
    }
}
