package fr.kevyn.farmland.chat;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * Poste un calcul simple dans le chat toutes les 5 minutes. Le premier
 * joueur a repondre la bonne reponse (juste le nombre, dans le chat)
 * gagne une recompense aleatoire en $FB.
 */
public class ChatCalcListener implements Listener {

    private static final Random random = new Random();

    private static Integer reponseAttendue = null; // null = aucun calcul en cours
    private static int recompenseActuelle = 0;

    public static void lancerNouveauCalcul(JavaPlugin plugin) {
        int a = random.nextInt(50) + 1;
        int b = random.nextInt(50) + 1;
        int operation = random.nextInt(3); // 0=addition, 1=soustraction, 2=multiplication

        String symbole;
        int resultat;
        switch (operation) {
            case 0: symbole = "+"; resultat = a + b; break;
            case 1: symbole = "-"; resultat = a - b; break;
            default:
                // multiplication : nombres plus petits pour rester simple
                a = random.nextInt(12) + 1;
                b = random.nextInt(12) + 1;
                symbole = "×"; resultat = a * b;
        }

        reponseAttendue = resultat;
        recompenseActuelle = random.nextInt(16) * 5 + 5; // entre 5 et 80 $FB, multiple de 5

        Bukkit.broadcastMessage("§b§l✦ CALCUL ✦ §fCombien font §e" + a + " " + symbole + " " + b + " §f? §7(tape la réponse dans le chat, +" + recompenseActuelle + " $FB au premier !)");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (reponseAttendue == null) return;

        String message = event.getMessage().trim();
        int valeur;
        try {
            valeur = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            return; // pas un nombre, on ignore (message normal)
        }

        if (valeur != reponseAttendue) return;

        Player player = event.getPlayer();
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return;

        int recompense = recompenseActuelle;
        reponseAttendue = null; // ferme le calcul immediatement (evite plusieurs gagnants)

        ps.setMoney(ps.getMoney() + recompense);
        event.setCancelled(true); // n'affiche pas la reponse brute dans le chat
        Bukkit.broadcastMessage("§a✔ " + player.getName() + " a trouvé la bonne réponse ! §e+" + recompense + " $FB");
    }
}
