package fr.kevyn.plot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.menu.MenuPlotConfig;
import fr.kevyn.farmland.menu.MenuPlotUpgrade;
import fr.kevyn.farmland.menu.MenuPlotVisit;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

public class Plotcommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public Plotcommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("farmland.plotcommands")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut exécuter cette commande !");
            return true;
        }

        Player player = (Player) sender;
        PlayerServer playerserver = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (playerserver == null || playerserver.getPlotdata() == null) {
            player.sendMessage(ChatColor.RED + "Erreur : vos données joueur sont introuvables.");
            return true;
        }

        if (!command.getName().equalsIgnoreCase("plot") && !command.getName().equalsIgnoreCase("p"))
            return false;

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Sous-commande manquante. Utilise: /plot <add/unadd/trust/untrust/buy/visit/home/config/setspawnpoint>");
            return true;
        }

        // /plot add <joueur>
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /plot add <joueur>");
                return true;
            }
            String worldname = player.getWorld().getName();
            
            if(playercanaddtrust(player, playerserver, worldname, "")) {
                Player playertarget = Bukkit.getPlayer(args[1]);
                if(playertarget == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                
                if(playertarget.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Tu ne peux pas t'ajouter toi-même !");
                    return true;
                }
                
                PlayerServer playerservertarget = PlayerserverHashMap.getInstance().getplayerHaspMaps(playertarget.getUniqueId());
                
                if(playerservertarget == null || playerservertarget.getPlotdata() == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                
                if(isnotaddinplot(playertarget, playerservertarget, worldname) && 
                   isnottrustinplot(playertarget, playerservertarget, worldname)) {
                    playerservertarget.getPlotdata().AddAllplotadd(worldname);
                    playertarget.sendMessage(ChatColor.GREEN + "Tu viens d'être add sur le plot de " + player.getName());
                    player.sendMessage(ChatColor.GREEN + "Le joueur a été ajouté avec succès !");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Ce joueur est déjà Add/Trust");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Tu n'as pas le droit");
                return true;
            }
        }
        
        // /plot unadd <joueur>
        else if (args[0].equalsIgnoreCase("unadd")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /plot unadd <joueur>");
                return true;
            }
            String worldname = player.getWorld().getName();
            
            if(playercanaddtrust(player, playerserver, worldname, "")) {
                Player playertarget = Bukkit.getPlayer(args[1]);
                if(playertarget == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                PlayerServer playerservertarget = PlayerserverHashMap.getInstance().getplayerHaspMaps(playertarget.getUniqueId());
                
                if(playerservertarget == null || playerservertarget.getPlotdata() == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                
                if(!isnotaddinplot(playertarget, playerservertarget, worldname)) {
                    playerservertarget.getPlotdata().RemoveAllplotadd(worldname);
                    playertarget.sendMessage(ChatColor.RED + "Tu viens d'être unadd du plot de " + player.getName());
                    player.sendMessage(ChatColor.GREEN + "Le joueur a été unadd avec succès !");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Ce joueur n'est pas Add");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Tu n'as pas le droit");
                return true;
            }
        }
        
        // /plot trust <joueur>
        else if (args[0].equalsIgnoreCase("trust")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /plot trust <joueur>");
                return true;
            }
            String worldname = player.getWorld().getName();
            
            if(playercanaddtrust(player, playerserver, worldname,"trust")) {
                Player playertarget = Bukkit.getPlayer(args[1]);
                if(playertarget == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                
                if(playertarget.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Tu ne peux pas te trust toi-même !");
                    return true;
                }
                
                PlayerServer playerservertarget = PlayerserverHashMap.getInstance().getplayerHaspMaps(playertarget.getUniqueId());
                
                if(playerservertarget == null || playerservertarget.getPlotdata() == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                
                if(isnottrustinplot(playertarget, playerservertarget, worldname)) {
                    if(!isnotaddinplot(playertarget, playerservertarget, worldname)) {
                        playerservertarget.getPlotdata().RemoveAllplotadd(worldname);
                        player.sendMessage(ChatColor.YELLOW + "Le joueur a été automatiquement retiré de la liste ADD.");
                    }
                    
                    playerservertarget.getPlotdata().AddAllplottrust(worldname);
                    playertarget.sendMessage(ChatColor.GREEN + "Tu viens d'être trust sur le plot de " + player.getName());
                    player.sendMessage(ChatColor.GREEN + "Le joueur a été trust avec succès !");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Ce joueur est déjà Trust");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Tu n'as pas le droit");
                return true;
            }
        }
        
        // /plot untrust <joueur>
        else if (args[0].equalsIgnoreCase("untrust")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /plot untrust <joueur>");
                return true;
            }
            String worldname = player.getWorld().getName();
            
            if(playercanaddtrust(player, playerserver, worldname, "trust")) {
                Player playertarget = Bukkit.getPlayer(args[1]);
                if(playertarget == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                PlayerServer playerservertarget = PlayerserverHashMap.getInstance().getplayerHaspMaps(playertarget.getUniqueId());
                
                if(playerservertarget == null || playerservertarget.getPlotdata() == null) {
                    player.sendMessage(ChatColor.RED + "Le joueur n'existe pas ou n'est pas connecté");
                    return true;
                }
                
                if(!isnottrustinplot(playertarget, playerservertarget, worldname)) {
                    playerservertarget.getPlotdata().RemoveAllplottrust(worldname);
                    playertarget.sendMessage(ChatColor.RED + "Tu viens d'être untrust du plot de " + player.getName());
                    player.sendMessage(ChatColor.GREEN + "Le joueur a été untrust avec succès !");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Ce joueur n'est pas trust");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Tu n'as pas le droit");
                return true;
            }
        }

        // /plot home
        else if (args[0].equalsIgnoreCase("home") || args[0].equalsIgnoreCase("h")) {
            String plotplayer = playerserver.getPlotdata().PlotProprety;
            World plotworld = Plot.getWorldforname(plotplayer);
            
            if (plotworld == null) {
                player.sendMessage(ChatColor.RED + "Erreur : monde introuvable");
                return true;
            }
            
            Location location = new Location(plotworld,
                    playerserver.getPlotdata().getLocationspawnX(),
                    playerserver.getPlotdata().getLocationspawnY(),
                    playerserver.getPlotdata().getLocationspawnZ());
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "Téléportation vers votre plot !");
            return true;
        }

        // /plot visit
        else if (args[0].equalsIgnoreCase("visit") || args[0].equalsIgnoreCase("v")) {
            if (args.length > 1) {
                PlayerServer target = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);
                if (target == null || target.getPlotdata() == null) {
                    player.sendMessage(ChatColor.RED + "Joueur introuvable.");
                    return true;
                }
                String plotplayer = target.getPlotdata().PlotProprety;
                World plottarget = Plot.getWorldforname(plotplayer);
                
                if (plottarget == null) {
                    player.sendMessage(ChatColor.RED + "Erreur : monde introuvable");
                    return true;
                }
                
                Location location = new Location(plottarget, 
                    target.getPlotdata().getLocationspawnX(),
                    target.getPlotdata().getLocationspawnY(),
                    target.getPlotdata().getLocationspawnZ());
                player.teleport(location);
                player.sendMessage(ChatColor.GREEN + "Téléportation vers le plot de " + target.getName());
                return true;
            }

            Inventory inventaire0 = MenuPlotVisit.createmenuplotvisit("plotvisit", 0);
            if (inventaire0 == null) {
                player.sendMessage(ChatColor.RED + "Erreur du côté serveur.");
                return true;
            }
            player.openInventory(inventaire0);
            return true;
        }

        // /plot buy
        else if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("b")) {
            Inventory inventaire0 = MenuPlotUpgrade.createmenuplotUpgrade("plotupgrade", 0, playerserver);
            if (inventaire0 == null) {
                player.sendMessage(ChatColor.RED + "Erreur du côté serveur.");
                return true;
            }
            player.openInventory(inventaire0);
            return true;
        }

        // /plot config
        else if (args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("c")) {
            Inventory inventaireconfig = MenuPlotConfig.createmenuplotconfig("plotconfig", playerserver);
            if (inventaireconfig == null) {
                player.sendMessage(ChatColor.RED + "Erreur du côté serveur");
                return true;
            }
            player.openInventory(inventaireconfig);
            return true;
        }

        // /plot setspawnpoint
        else if (args[0].equalsIgnoreCase("setspawnpoint") || args[0].equalsIgnoreCase("setspawn")) {
            int locatex = player.getLocation().getBlockX();
            int locatey = player.getLocation().getBlockY();
            int locatez = player.getLocation().getBlockZ();
            playerserver.getPlotdata().setLocationspawnX(locatex);
            playerserver.getPlotdata().setLocationspawnY(locatey);
            playerserver.getPlotdata().setLocationspawnZ(locatez);
            player.sendMessage(ChatColor.GREEN + "Zone de spawn définie !");
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Sous-commande inconnue. Utilise: /plot <add/unadd/trust/untrust/buy/visit/home/config/setspawnpoint>");
        return true;
    }
    
    public boolean playercanaddtrust(Player player, PlayerServer playerserver, String Worldname, String trust) {
        String ownerplot = playerserver.getPlotdata().getPlotProprety();
        ArrayList<String> listtrust = playerserver.getPlotdata().getAllplottrust();
        if(ownerplot.equalsIgnoreCase(Worldname)) {
            return true;
        } else if(listtrust.contains(Worldname) && !trust.equalsIgnoreCase("trust")){
            return true;
        } else
            return false;
    }
    
    public boolean isnotaddinplot(Player player, PlayerServer playerserver, String plotwantaddtrust) {
        ArrayList<String> listadd = playerserver.getPlotdata().getAllplotadd();
        
        if(playerserver.getPlotdata().getPlotProprety().equals(plotwantaddtrust)) {
            return false;
        }
        
        if(listadd.contains(plotwantaddtrust)) {
            return false;
        }
        return true;
    }

    public boolean isnottrustinplot(Player player, PlayerServer playerserver, String plotwantaddtrust) {
        ArrayList<String> listtrust = playerserver.getPlotdata().getAllplottrust();
        
        if(playerserver.getPlotdata().getPlotProprety().equals(plotwantaddtrust)) {
            return false;
        }
        
        if(listtrust.contains(plotwantaddtrust)) {
            return false;
        }
        return true;
    }
}