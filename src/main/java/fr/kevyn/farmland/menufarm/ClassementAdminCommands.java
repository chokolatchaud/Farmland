package fr.kevyn.farmland.menufarm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * /classementadmin holo set/remove/list <metier> - gere les hologrammes
 * de classement (top 5 par metier), meme pattern que /marketadmin holo.
 */
public class ClassementAdminCommands implements CommandExecutor {

    private final JavaPlugin plugin;

    public ClassementAdminCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("holo")) {
            sender.sendMessage("§6/classementadmin holo <set|remove|list> [metier]");
            sender.sendMessage("§7Métiers : Mineur, Farmeur, Agriculteur, Pecheur, Tueur");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /classementadmin holo <set|remove|list> [metier]");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "set": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cSeul un joueur peut poser un hologramme !");
                    return true;
                }
                if (args.length < 3 || !LeaderboardHolograms.isValidMetier(args[2])) {
                    sender.sendMessage("§cMétier inconnu ! (Mineur, Farmeur, Agriculteur, Pecheur, Tueur)");
                    return true;
                }
                LeaderboardHolograms.setHologram(plugin, args[2], player.getLocation());
                sender.sendMessage("§aHologramme de classement §e" + args[2] + "§a posé à ta position !");
                return true;
            }
            case "remove": {
                if (args.length < 3 || !LeaderboardHolograms.isValidMetier(args[2])) {
                    sender.sendMessage("§cMétier inconnu !");
                    return true;
                }
                if (LeaderboardHolograms.removeHologram(plugin, args[2])) {
                    sender.sendMessage("§aHologramme supprimé !");
                } else {
                    sender.sendMessage("§cAucun hologramme posé pour ce métier !");
                }
                return true;
            }
            case "list": {
                if (LeaderboardHolograms.getEmplacements().isEmpty()) {
                    sender.sendMessage("§7Aucun hologramme posé. (/classementadmin holo set <metier>)");
                    return true;
                }
                sender.sendMessage("§6═══ Hologrammes de classement ═══");
                for (var entry : LeaderboardHolograms.getEmplacements().entrySet()) {
                    LeaderboardHolograms.HoloLoc loc = entry.getValue();
                    sender.sendMessage("§e" + entry.getKey() + " §7→ §f" + loc.world);
                }
                return true;
            }
            default:
                sender.sendMessage("§cUsage : /classementadmin holo <set|remove|list> [metier]");
                return true;
        }
    }
}
