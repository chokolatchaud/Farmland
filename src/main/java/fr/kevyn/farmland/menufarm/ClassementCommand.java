package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * /classement <metier> - top 10 des joueurs par niveau (puis XP en cas
 * d'egalite) pour le metier donne.
 *
 * ATTENTION : ne reflete que les joueurs actuellement connus en memoire
 * (deja connectes cette session) - pas un historique persistant apres
 * redemarrage.
 */
public class ClassementCommand implements CommandExecutor {

    private record Entree(String nom, int niveau, int xp) {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUsage : /classement <mineur|farmeur|pecheur|agriculteur|tueur>");
            return true;
        }

        String metier = args[0].toLowerCase();
        List<Entree> entrees = new ArrayList<>();

        for (Map.Entry<UUID, PlayerServer> entry : PlayerserverHashMap.getInstance().getHashMapPlayer().entrySet()) {
            PlayerServer ps = entry.getValue();
            int niveau = getNiveau(ps, metier);
            if (niveau < 0) {
                sender.sendMessage("§cMétier inconnu ! (mineur, farmeur, pecheur, agriculteur, tueur)");
                return true;
            }
            entrees.add(new Entree(ps.getName(), niveau, ps.getXp(capitalize(metier))));
        }

        entrees.sort(Comparator.comparingInt(Entree::niveau).reversed()
            .thenComparing(Comparator.comparingInt(Entree::xp).reversed()));

        sender.sendMessage("§6═══ Classement " + capitalize(metier) + " ═══");
        int rang = 1;
        for (Entree e : entrees.subList(0, Math.min(10, entrees.size()))) {
            sender.sendMessage("§e#" + rang + " §f" + e.nom() + " §7- niveau §b" + e.niveau() + " §7(" + e.xp() + " XP)");
            rang++;
        }
        if (entrees.isEmpty()) {
            sender.sendMessage("§7Aucun joueur connu pour l'instant.");
        }

        return true;
    }

    private int getNiveau(PlayerServer ps, String metier) {
        return switch (metier) {
            case "mineur" -> ps.getCobblestonegeneratorlevel();
            case "farmeur" -> ps.getHoueLevel();
            case "pecheur" -> ps.getCanneLevel();
            case "agriculteur" -> ps.getHacheLevel();
            case "tueur" -> ps.getEpeeLevel();
            default -> -1;
        };
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
