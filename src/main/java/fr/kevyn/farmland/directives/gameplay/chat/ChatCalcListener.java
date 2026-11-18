package fr.kevyn.farmland.directives.gameplay.chat;



import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;

/**
 * Poste un calcul simple dans le chat toutes les 5 minutes. Le premier
 * joueur a repondre la bonne reponse (juste le nombre, dans le chat)
 * gagne une recompense fixe en $FB. Les resultats negatifs sont voulus.
 */
public class ChatCalcListener implements Listener {

    private static final Random random = new Random();

    // null = aucun calcul en cours. Atomique : le chat est traite en parallele,
    // un seul joueur peut "prendre" la bonne reponse.
    private static final AtomicReference<Integer> reponseAttendue = new AtomicReference<>(null);
    private static final int recompenseActuelle = 75;
    private static JavaPlugin plugin;

    /** Un calcul : texte affiche et resultat attendu. */
    record Calcul(String texte, int resultat) {}

    /**
     * Tire un calcul : addition ou soustraction (1 a 50, resultat negatif possible),
     * ou multiplication (1 a 12, un des deux nombres peut etre negatif).
     */
    static Calcul genererCalcul(Random r) {
        int operation = r.nextInt(3); // 0=addition, 1=soustraction, 2=multiplication
        switch (operation) {
            case 0: {
                int a = r.nextInt(50) + 1;
                int b = r.nextInt(50) + 1;
                return new Calcul(a + " + " + b, a + b);
            }
            case 1: {
                int a = r.nextInt(50) + 1;
                int b = r.nextInt(50) + 1;
                return new Calcul(a + " - " + b, a - b);
            }
            default: {
                // multiplication : nombres plus petits pour rester simple, signe aleatoire
                int a = r.nextInt(12) + 1;
                int b = r.nextInt(12) + 1;
                if (r.nextBoolean()) a = -a;
                if (r.nextBoolean()) b = -b;
                return new Calcul(nombre(a) + " × " + nombre(b), a * b);
            }
        }
    }

    // Un nombre negatif est mis entre parentheses : "7 × (-8)"
    private static String nombre(int n) {
        return n < 0 ? "(" + n + ")" : String.valueOf(n);
    }

    public static void lancerNouveauCalcul(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        Calcul calcul = genererCalcul(random);
        reponseAttendue.set(calcul.resultat());

        Bukkit.broadcastMessage("§b§l✦ CALCUL ✦ §fCombien font §e" + calcul.texte() + " §f? §7(tape la réponse dans le chat, +" + recompenseActuelle + " $FB au premier !)");
    }

    /**
     * Vrai seulement pour le PREMIER joueur qui donne la bonne reponse :
     * la verification et la fermeture du calcul se font en une seule operation.
     */
    static boolean tenterReponse(int valeur) {
        Integer attendue = reponseAttendue.get();
        if (attendue == null || attendue != valeur) return false;
        return reponseAttendue.compareAndSet(attendue, null);
    }

    // Pour les tests
    static void definirReponsePourTest(Integer valeur) {
        reponseAttendue.set(valeur);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (reponseAttendue.get() == null) return;

        String message = event.getMessage().trim();
        int valeur;
        try {
            valeur = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            return; // pas un nombre, on ignore (message normal)
        }

        if (!tenterReponse(valeur)) return;

        event.setCancelled(true); // n'affiche pas la reponse brute dans le chat
        Player player = event.getPlayer();
        int recompense = recompenseActuelle;

        // Le chat tourne en parallele : l'argent est donne sur le fil principal du serveur
        Bukkit.getScheduler().runTask(plugin, () -> {
            PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (ps == null) return;
            ps.setMoney(ps.getMoney() + recompense);
            JournalEvenements.enregistrer("gain_calcul", player.getName(), "+" + recompense + " $FB, solde " + ps.getMoney() + " $FB");
            Bukkit.broadcastMessage("§a✔ " + player.getName() + " a trouvé la bonne réponse ! §e+" + recompense + " $FB");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        });
    }
}
