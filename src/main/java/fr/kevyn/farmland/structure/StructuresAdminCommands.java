package fr.kevyn.farmland.structure;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;

/**
 * /structuresadmin - depannage des structures (permission farmland.admin)
 *   /structuresadmin list <joueur>            -> liste les structures d'un joueur avec leur score
 *   /structuresadmin info <nom>               -> details complets d'une structure
 *   /structuresadmin score <nom> <valeur>     -> force le score d'une structure
 *   /structuresadmin remove <nom>             -> supprime une structure (doublon/cassee)
 *   /structuresadmin tp <nom>                 -> se teleporter au spawn d'une structure
 */
public class StructuresAdminCommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public StructuresAdminCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/structuresadmin list <joueur> §7- liste ses structures");
            sender.sendMessage("§6/structuresadmin info <nom> §7- details d'une structure");
            sender.sendMessage("§6/structuresadmin score <nom> <valeur> §7- force le score");
            sender.sendMessage("§6/structuresadmin remove <nom> §7- supprime une structure");
            sender.sendMessage("§6/structuresadmin tp <nom> §7- teleporte au spawn d'une structure");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list": return listCommand(sender, args);
            case "info": return infoCommand(sender, args);
            case "score": return scoreCommand(sender, args);
            case "remove": return removeCommand(sender, args);
            case "tp": return tpCommand(sender, args);
            default:
                sender.sendMessage("§cSous-commande inconnue ! (/structuresadmin pour l'aide)");
                return true;
        }
    }

    private boolean listCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /structuresadmin list <joueur>");
            return true;
        }
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);
        if (ps == null) {
            sender.sendMessage("§cJoueur introuvable !");
            return true;
        }

        List<GameRegion> owned = new ArrayList<>();
        for (GameRegion r : GetStructure.getallStructure()) {
            if (r.getPropriétaire() != null && r.getPropriétaire().equals(ps.getUuid())) {
                owned.add(r);
            }
        }

        if (owned.isEmpty()) {
            sender.sendMessage("§7" + args[1] + " n'a aucune structure définie.");
            return true;
        }

        sender.sendMessage("§6═══ Structures de " + args[1] + " (" + owned.size() + ") ═══");
        for (GameRegion r : owned) {
            sender.sendMessage("§e" + r.getName() + " §7— score : §f" + r.getScore());
        }
        return true;
    }

    private GameRegion getTarget(CommandSender sender, String name) {
        GameRegion region = GameRegionHashMap.getInstance().getregionbyname(name);
        if (region == null) {
            sender.sendMessage("§cStructure introuvable ! (" + name + ")");
            return null;
        }
        return region;
    }

    private boolean infoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /structuresadmin info <nom>");
            return true;
        }
        GameRegion region = getTarget(sender, args[1]);
        if (region == null) return true;

        sender.sendMessage("§6═══ Structure " + region.getName() + " ═══");
        sender.sendMessage("§eType : §f" + region.gettype());
        sender.sendMessage("§eScore : §f" + region.getScore());
        sender.sendMessage("§eMonde : §f" + region.getWorldname());
        sender.sendMessage("§ePropriétaire : §f" + region.getPropriétaire());
        sender.sendMessage("§eCoordonnées : §f(" + region.getMinX() + "," + region.getMinY() + "," + region.getMinZ()
                + ") -> (" + region.getMaxX() + "," + region.getMaxY() + "," + region.getMaxZ() + ")");
        return true;
    }

    private boolean scoreCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage : /structuresadmin score <nom> <valeur>");
            return true;
        }
        GameRegion region = getTarget(sender, args[1]);
        if (region == null) return true;

        float valeur;
        try {
            valeur = Float.parseFloat(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide !");
            return true;
        }

        region.setScore(valeur);
        sender.sendMessage("§aScore de " + region.getName() + " forcé à " + valeur);
        plugin.getLogger().info("[StructuresAdmin] " + sender.getName() + " -> score " + valeur + " sur " + region.getName());
        return true;
    }

    private boolean removeCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /structuresadmin remove <nom>");
            return true;
        }
        GameRegion region = getTarget(sender, args[1]);
        if (region == null) return true;

        GameRegionHashMap.getInstance().removeregion(region);
        sender.sendMessage("§aStructure " + args[1] + " supprimée !");
        plugin.getLogger().info("[StructuresAdmin] " + sender.getName() + " a supprime la structure " + args[1]);
        return true;
    }

    private boolean tpCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /structuresadmin tp <nom>");
            return true;
        }
        GameRegion region = getTarget(sender, args[1]);
        if (region == null) return true;

        org.bukkit.World world = Bukkit.getWorld(region.getWorldname());
        if (world == null) {
            sender.sendMessage("§cMonde introuvable : " + region.getWorldname());
            return true;
        }

        Player player = (Player) sender;
        player.teleport(new org.bukkit.Location(world, region.getSpawnX(), region.getSpawnY(), region.getSpawnZ()));
        sender.sendMessage("§aTéléporté à la structure " + region.getName() + " !");
        return true;
    }
}
