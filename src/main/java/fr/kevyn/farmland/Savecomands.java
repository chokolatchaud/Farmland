package fr.kevyn.farmland;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.Filesave;

public class Savecomands implements CommandExecutor {
    JavaPlugin plugin;

    public Savecomands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (command.getName().equalsIgnoreCase("saveplayer")) {
            if(!sender.hasPermission("farmland.saveplayer")) {
                sender.sendMessage(MessageColor.RED.apply("Vous n'avez pas la permission"));
                return true;
            }
            
            sender.sendMessage(MessageColor.YELLOW.apply("Sauvegarde en cours..."));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Filesave.SavePlayerserverFile(plugin);
                sender.sendMessage(MessageColor.GREEN.apply("Sauvegarde terminée !"));
            });
            
            return true;
            
        } else if(command.getName().equalsIgnoreCase("playerserver")) {
            if(!sender.hasPermission("farmland.playerserver")) {
                sender.sendMessage(MessageColor.RED.apply("Vous n'avez pas la permission"));
                return true;
            }
            
            if (args.length == 0) {
                sender.sendMessage(MessageColor.RED.apply("Usage: /playerserver <joueur|all>"));
                return true;
            }
            
            if(args[0].equalsIgnoreCase("all")) {
                for(PlayerServer playerserver : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
                    allplayertag(playerserver, sender);
                }
            } else {
                String nameplayer = args[0];
                PlayerServer playeridentificate = PlayerserverHashMap.getInstance().getplayerHaspMaps(nameplayer.toString());
                if(playeridentificate == null) {
                    sender.sendMessage(MessageColor.RED.apply("Joueur introuvable !"));
                    sender.sendMessage("Usage: /playerserver <joueur>");
                } else {
                    allplayertag(playeridentificate, sender);
                }
            }
        }
        return true;
    }
    
    public static void allplayertag(PlayerServer playerserver, CommandSender sender) {
        sender.sendMessage("------------------" + "Joueur: " + playerserver.getName() + "------------------------");
        sender.sendMessage("money: " + playerserver.getMoney());
        sender.sendMessage("raison: " + playerserver.getRaison());
        sender.sendMessage("ban: " + playerserver.getBan());
        sender.sendMessage("lastjoin: " + playerserver.getLastjoin());
        
        if (playerserver.getPlotdata() != null) {
            sender.sendMessage("Plotdata-NameWorld: " + playerserver.getPlotdata().getNameWorld());
            sender.sendMessage("Plotdata-AllplotAdd: " + playerserver.getPlotdata().getAllplotadd().toString());
            sender.sendMessage("Plotdata-AllplotTrust: " + playerserver.getPlotdata().getAllplottrust().toString());
            sender.sendMessage("Plotdata-Plotproprety: " + playerserver.getPlotdata().getPlotProprety());
            sender.sendMessage("Plotdata-Meteo: " + playerserver.getPlotdata().getMeteoActive());
            sender.sendMessage("Plotdata-Meteo: " + playerserver.getPlotdata().getMeteoRain());
            sender.sendMessage("Plotdata-Meteo: " + playerserver.getPlotdata().getMeteoTime());
        } else {
            sender.sendMessage(MessageColor.RED.apply("Plotdata: NULL"));
        }
        
        sender.sendMessage("grade: " + playerserver.getGrade());
    }
}