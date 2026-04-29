package fr.kevyn.farmland.region;

import java.util.Arrays;
import java.util.stream.Collectors;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
public class RegionCommands implements CommandExecutor {

public boolean onCommand(CommandSender sender,Command command,String label,
        String[] args) {
    
    Player player = (Player) sender;
    if(player == null) {
        sender.sendMessage("§cSeul un joueur peut exécuter cette commande !");
        return true;
    }

    if(command.getName().equalsIgnoreCase("createregion")) {
        boolean canbuild = false;
        TypeRegion type = null;
        
        if(args.length != 4) {
            player.sendMessage("veuiller nommer la region");
            player.sendMessage("/createregion <nom> <canbuild> <type> <categorie>");
            return true;
        }
        
        String name = args[0];
        try {
            canbuild = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            player.sendMessage("§cBoolean Invalide");
            return true;
        }
        
        try {
            type = TypeRegion.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            String types = Arrays.stream(TypeRegion.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            player.sendMessage("§cType invalide ! Valeurs possibles: §f" + types);
            return true;
        }
        
        String namecircuit = args[3];
        
        try {
            SessionManager manager = WorldEdit.getInstance().getSessionManager();
            LocalSession session = manager.get(BukkitAdapter.adapt(player));
            Region selection = session.getSelection(BukkitAdapter.adapt(player.getWorld()));

            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();
            
            GameRegion region = new GameRegion(min.getBlockX(), min.getBlockY(), min.getBlockZ(),
                    max.getBlockX(),max.getBlockY(),max.getBlockZ(),player.getX(),player.getY(),player.getZ(), name,canbuild,player.getWorld().getName(), type,namecircuit);
            player.sendMessage("§aRégion §f" + name + " §acréée !");
            return true;
        } catch (Exception e) {
            player.sendMessage("§cFais une sélection WorldEdit d'abord !");
            return true;
        }
        
    } else if(command.getName().equalsIgnoreCase("listregion")) {
        if (GameRegionHashMap.getInstance().getRegionhashmap().isEmpty()) {
            player.sendMessage("§cAucune région créée !");
            return true;
        }
        for(GameRegion gameregion : GameRegionHashMap.getInstance().getRegionhashmap()) {
            player.sendMessage("Zone: " + gameregion.getName() + " bien defini");
        }
        return true;
        
    } else if(command.getName().equalsIgnoreCase("region")) {
        if((args.length <= 1)) {
            player.sendMessage("§cVeuiller mettre un argument");
            return true;
        }
        
        String commandUse = args[0];
        GameRegion gameregion = GameRegionHashMap.getInstance().Playerwhatistregion(player);
        
        switch (commandUse) {
            case "setproprietaire" -> {
                if (gameregion == null) {
                    player.sendMessage("§cVous n'êtes pas dans une région !");
                    return true;
                }
                if(args.length == 2) {
                    Player playerproprio = Bukkit.getPlayer(args[1]);
                    if(playerproprio == null) {
                        player.sendMessage("§cLe nouveau proprietaire de la region est inconnu");
                        return true;
                    }
                    gameregion.setPropriétaire(playerproprio.getUniqueId());
                    playerproprio.sendMessage("Tu est maintenant proprietaire de la region : " + gameregion.getName());
                    return true;
                } else {
                    player.sendMessage("§cveuillez nommer le nouveau proprietaire");
                    return true;
                }
            }
            
            case "setcanbuild" -> {
                if(args.length == 2) {
                    try {
                        if (gameregion == null) {
                            player.sendMessage("§cVous n'êtes pas dans une région !");
                            return true;
                        }
                        boolean canbuildtruefalse = Boolean.parseBoolean(args[1]);
                        gameregion.setCanbuild(canbuildtruefalse);
                        player.sendMessage("la region est devenu canbuild" + gameregion.getCanbuild());
                        return true;
                    } catch (Exception e) {
                        player.sendMessage("§cBoolean Invalide");
                        return true;
                    }
                } else {
                    player.sendMessage("§cveuillez donnez une valeur True/False");
                    return true;
                }
            }
            
            case "tp" -> {
                if(args.length == 2) {
                    try {
                        String name = args[1];
                        GameRegion regionteleport = GameRegionHashMap.getInstance().getregionbyname(name);
                        if (regionteleport == null) {
                            player.sendMessage("§cRégion §f" + name + " §cintrouvable !");
                            return true;
                        }
                        Location location = new Location(Bukkit.getWorld(regionteleport.getWorldname()),regionteleport.getSpawnX(),regionteleport.getSpawnY(), regionteleport.getSpawnZ(), 0, 0);
                        player.teleport(location);
                        player.sendMessage("§aTéléporté à §f" + name);
                        return true;
                    } catch (Exception e) {
                        player.sendMessage("§cNom de Region Invalide");
                        return true;
                    }
                }
            }
            
            default -> {
                player.sendMessage("§cCommande inconnue ! setproprietaire / setcanbuild / tp");
                return true;
            }
        }
        return true;
    }
    
    return false;
}
}