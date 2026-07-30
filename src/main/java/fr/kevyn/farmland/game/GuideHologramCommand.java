package fr.kevyn.farmland.game;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * /guideholo - gestion des hologrammes du guide, un par section
 * (permission farmland.moderation)
 *   /guideholo set <section>     -> pose l'hologramme de cette section a ta position
 *   /guideholo remove <section>  -> le supprime
 *   /guideholo list              -> liste les hologrammes poses
 *
 * Sections disponibles : construction, plot, bateau, vote, afk
 */
public class GuideHologramCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public GuideHologramCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/guideholo set <section> §7- pose l'hologramme de cette section a ta position");
            sender.sendMessage("§6/guideholo remove <section> §7- le supprime");
            sender.sendMessage("§6/guideholo list §7- liste les hologrammes poses");
            sender.sendMessage("§7Sections : construction, plot, bateau, vote, afk");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cSeul un joueur peut poser un hologramme !");
                    return true;
                }
                if (args.length < 2 || !GuideHologram.isValidSection(args[1])) {
                    sender.sendMessage("§cSection inconnue ! (construction, plot, bateau, vote, afk)");
                    return true;
                }
                Player player = (Player) sender;
                GuideHologram.setHologram(plugin, args[1], player.getLocation());
                sender.sendMessage("§aHologramme §e" + args[1].toLowerCase() + "§a posé à ta position !");
                plugin.getLogger().info("[GuideHolo] " + sender.getName() + " a pose l'hologramme " + args[1].toLowerCase());
                return true;
            }
            case "remove": {
                if (args.length < 2 || !GuideHologram.isValidSection(args[1])) {
                    sender.sendMessage("§cSection inconnue !");
                    return true;
                }
                if (GuideHologram.removeHologram(plugin, args[1])) {
                    sender.sendMessage("§aHologramme §e" + args[1].toLowerCase() + "§a supprimé !");
                } else {
                    sender.sendMessage("§cAucun hologramme posé pour cette section !");
                }
                return true;
            }
            case "list": {
                if (GuideHologram.getEmplacements().isEmpty()) {
                    sender.sendMessage("§7Aucun hologramme posé. (/guideholo set <section>)");
                    return true;
                }
                sender.sendMessage("§6═══ Hologrammes du guide ═══");
                for (java.util.Map.Entry<String, GuideHologram.HoloLoc> e : GuideHologram.getEmplacements().entrySet()) {
                    GuideHologram.HoloLoc loc = e.getValue();
                    sender.sendMessage("§e" + e.getKey() + " §7→ §f" + loc.world + " §7(" + (int) loc.x + ", " + (int) loc.y + ", " + (int) loc.z + ")");
                }
                return true;
            }
            default:
                sender.sendMessage("§cUsage : /guideholo <set|remove|list>");
                return true;
        }
    }
}
