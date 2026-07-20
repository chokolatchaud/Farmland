package fr.kevyn.farmland.boathub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * /raceadmin - commandes admin de la course de bateaux (permission farmland.admin)
 *   /raceadmin holo set     -> pose l'hologramme des meilleurs temps a ta position
 *   /raceadmin holo remove  -> le supprime
 *   /raceadmin reset        -> efface tous les temps enregistres
 */
public class RaceAdminCommands implements CommandExecutor {

    private final JavaPlugin plugin;

    public RaceAdminCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/raceadmin holo set §7- pose l'hologramme des meilleurs temps");
            sender.sendMessage("§6/raceadmin holo remove §7- le supprime");
            sender.sendMessage("§6/raceadmin reset §7- efface tous les temps enregistres");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "holo": return holoCommand(sender, args);
            case "reset": return resetCommand(sender);
            default:
                sender.sendMessage("§cSous-commande inconnue ! (/raceadmin pour l'aide)");
                return true;
        }
    }

    private boolean holoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /raceadmin holo <set|remove>");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "set": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cSeul un joueur peut poser un hologramme !");
                    return true;
                }
                Player player = (Player) sender;
                BoatRaceHologram.setHologram(plugin, player.getLocation());
                sender.sendMessage("§aHologramme des meilleurs temps posé à ta position !");
                plugin.getLogger().info("[RaceAdmin] " + sender.getName() + " a pose l'hologramme des temps");
                return true;
            }
            case "remove": {
                if (BoatRaceHologram.removeHologram(plugin)) {
                    sender.sendMessage("§aHologramme supprimé !");
                } else {
                    sender.sendMessage("§cAucun hologramme posé !");
                }
                return true;
            }
            default:
                sender.sendMessage("§cUsage : /raceadmin holo <set|remove>");
                return true;
        }
    }

    private boolean resetCommand(CommandSender sender) {
        java.io.File file = new java.io.File(plugin.getDataFolder(), "boat_times.json");
        if (file.exists()) file.delete();
        BoatRaceHologram.update(plugin);
        sender.sendMessage("§aTous les temps ont été effacés !");
        plugin.getLogger().info("[RaceAdmin] " + sender.getName() + " a reset les temps de course");
        return true;
    }
}
