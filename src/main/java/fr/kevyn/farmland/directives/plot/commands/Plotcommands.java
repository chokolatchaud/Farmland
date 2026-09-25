package fr.kevyn.farmland.directives.plot.commands;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotConfig;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotUpgrade;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotVisit;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.directives.plot.gestion.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Commandes joueur liées aux plots.
 */
public final class Plotcommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public Plotcommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("farmland.plotcommands")) {
            sender.sendMessage(MessageColor.RED.apply("Vous n'avez pas la permission."));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut exécuter cette commande !");
            return true;
        }

        Player player = (Player) sender;
        PlayerServer playerServer = PlayerserverHashMap.getInstance()
                .getplayerHaspMaps(player.getUniqueId());
        if (playerServer == null) {
            player.sendMessage(MessageColor.RED.apply("Erreur : vos données joueur sont introuvables."));
            return true;
        }

        if (!command.getName().equalsIgnoreCase("plot")
                && !command.getName().equalsIgnoreCase("p")) {
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(MessageColor.YELLOW.apply(
                    "Sous-commande manquante. Utilise: /plot <add/unadd/trust/untrust/buy/visit/home/config/setspawnpoint>"));
            return true;
        }

        // /plot add <joueur>
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                player.sendMessage(MessageColor.RED.apply("Usage: /plot add <joueur>"));
                return true;
            }
            String worldName = player.getWorld().getName();

            if (requireOwnPlot(player, playerServer) && canManagePlotAccess(player, playerServer, worldName, "")) {
                PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);

                if (targetServer == null || targetServer.getPlotdata() == null) {
                    player.sendMessage(MessageColor.RED.apply(
                            "Le joueur n'existe pas ou n'a jamais rejoint le serveur"));
                    return true;
                }

                if (targetServer.getUuid().equals(player.getUniqueId())) {
                    player.sendMessage(MessageColor.RED.apply("Tu ne peux pas t'ajouter toi-même !"));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(targetServer.getUuid());

                if (isNotAddedToPlot(targetPlayer, targetServer, worldName)
                        && isNotTrustedOnPlot(targetPlayer, targetServer, worldName)) {
                    targetServer.getPlotdata().AddAllplotadd(worldName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.sendMessage(MessageColor.GREEN.apply(
                                "Tu viens d'être add sur le plot de " + player.getName()));
                    }
                    player.sendMessage(MessageColor.GREEN.apply("Le joueur a été ajouté avec succès !"));
                } else {
                    player.sendMessage(MessageColor.RED.apply("Ce joueur est déjà Add/Trust"));
                }
                return true;
            }

            player.sendMessage(MessageColor.RED.apply("Tu n'as pas le droit"));
            return true;
        }

        // /plot unadd <joueur>
        if (args[0].equalsIgnoreCase("unadd")) {
            if (args.length < 2) {
                player.sendMessage(MessageColor.RED.apply("Usage: /plot unadd <joueur>"));
                return true;
            }
            String worldName = player.getWorld().getName();

            if (requireOwnPlot(player, playerServer) && canManagePlotAccess(player, playerServer, worldName, "")) {
                PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);
                if (targetServer == null || targetServer.getPlotdata() == null) {
                    player.sendMessage(MessageColor.RED.apply(
                            "Le joueur n'existe pas ou n'a jamais rejoint le serveur"));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(targetServer.getUuid());
                if (!isNotAddedToPlot(targetPlayer, targetServer, worldName)) {
                    targetServer.getPlotdata().RemoveAllplotadd(worldName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.sendMessage(MessageColor.RED.apply(
                                "Tu viens d'être unadd du plot de " + player.getName()));
                    }
                    player.sendMessage(MessageColor.GREEN.apply("Le joueur a été unadd avec succès !"));
                } else {
                    player.sendMessage(MessageColor.RED.apply("Ce joueur n'est pas Add"));
                }
                return true;
            }

            player.sendMessage(MessageColor.RED.apply("Tu n'as pas le droit"));
            return true;
        }

        // /plot trust <joueur>
        if (args[0].equalsIgnoreCase("trust")) {
            if (args.length < 2) {
                player.sendMessage(MessageColor.RED.apply("Usage: /plot trust <joueur>"));
                return true;
            }
            String worldName = player.getWorld().getName();

            if (requireOwnPlot(player, playerServer) && canManagePlotAccess(player, playerServer, worldName, "trust")) {
                PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);
                if (targetServer == null || targetServer.getPlotdata() == null) {
                    player.sendMessage(MessageColor.RED.apply(
                            "Le joueur n'existe pas ou n'a jamais rejoint le serveur"));
                    return true;
                }

                if (targetServer.getUuid().equals(player.getUniqueId())) {
                    player.sendMessage(MessageColor.RED.apply("Tu ne peux pas te trust toi-même !"));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(targetServer.getUuid());
                if (isNotTrustedOnPlot(targetPlayer, targetServer, worldName)) {
                    if (!isNotAddedToPlot(targetPlayer, targetServer, worldName)) {
                        targetServer.getPlotdata().RemoveAllplotadd(worldName);
                        player.sendMessage(MessageColor.YELLOW.apply(
                                "Le joueur a été automatiquement retiré de la liste ADD."));
                    }

                    targetServer.getPlotdata().AddAllplottrust(worldName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.sendMessage(MessageColor.GREEN.apply(
                                "Tu viens d'être trust sur le plot de " + player.getName()));
                    }
                    player.sendMessage(MessageColor.GREEN.apply("Le joueur a été trust avec succès !"));
                } else {
                    player.sendMessage(MessageColor.RED.apply("Ce joueur est déjà Trust"));
                }
                return true;
            }

            player.sendMessage(MessageColor.RED.apply("Tu n'as pas le droit"));
            return true;
        }

        // /plot untrust <joueur>
        if (args[0].equalsIgnoreCase("untrust")) {
            if (args.length < 2) {
                player.sendMessage(MessageColor.RED.apply("Usage: /plot untrust <joueur>"));
                return true;
            }
            String worldName = player.getWorld().getName();

            if (requireOwnPlot(player, playerServer) && canManagePlotAccess(player, playerServer, worldName, "trust")) {
                PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);
                if (targetServer == null || targetServer.getPlotdata() == null) {
                    player.sendMessage(MessageColor.RED.apply(
                            "Le joueur n'existe pas ou n'a jamais rejoint le serveur"));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(targetServer.getUuid());
                if (!isNotTrustedOnPlot(targetPlayer, targetServer, worldName)) {
                    targetServer.getPlotdata().RemoveAllplottrust(worldName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.sendMessage(MessageColor.RED.apply(
                                "Tu viens d'être untrust du plot de " + player.getName()));
                    }
                    player.sendMessage(MessageColor.GREEN.apply("Le joueur a été untrust avec succès !"));
                } else {
                    player.sendMessage(MessageColor.GREEN.apply("Ce joueur n'est pas trust"));
                }
                return true;
            }

            player.sendMessage(MessageColor.RED.apply("Tu n'as pas le droit"));
            return true;
        }

        // /plot home
        if (args[0].equalsIgnoreCase("home") || args[0].equalsIgnoreCase("h")) {
            if (!requireOwnPlot(player, playerServer)) {
                return true;
            }

            String plotName = getOwnPlotName(playerServer);
            if (plotName == null || plotName.isBlank()) {
                player.sendMessage(MessageColor.RED.apply("Erreur : nom du plot introuvable."));
                return true;
            }

            World plotWorld = Plot.getWorldforname(plotName);
            if (plotWorld == null) {
                player.sendMessage(MessageColor.RED.apply("Erreur : monde introuvable"));
                return true;
            }

            Location location = new Location(plotWorld,
                    playerServer.getPlotdata().getLocationspawnX(),
                    playerServer.getPlotdata().getLocationspawnY(),
                    playerServer.getPlotdata().getLocationspawnZ());
            player.teleport(location);
            player.sendMessage(MessageColor.GREEN.apply("Téléportation vers votre plot !"));
            return true;
        }

        // /plot visit
        if (args[0].equalsIgnoreCase("visit") || args[0].equalsIgnoreCase("v")) {
            if (args.length > 1) {
                PlayerServer target = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[1]);
                if (target == null || target.getPlotdata() == null) {
                    player.sendMessage(MessageColor.RED.apply("Joueur introuvable."));
                    return true;
                }

                String plotName = target.getPlotdata().getPlotProprety();
                if (plotName == null || plotName.isBlank()) {
                    player.sendMessage(MessageColor.RED.apply("Erreur : nom du plot introuvable."));
                    return true;
                }

                if (target.getPlotdata().getPrivateplot()
                        && !target.getUuid().equals(player.getUniqueId())) {
                    player.sendMessage(MessageColor.RED.apply("Ce plot est privé !"));
                    return true;
                }

                World targetPlotWorld = Plot.getWorldforname(plotName);

                if (targetPlotWorld == null) {
                    player.sendMessage(MessageColor.GRAY.apply("Chargement du plot en cours..."));
                    try {
                        new Plot(UUID.fromString(plotName), plugin);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(MessageColor.RED.apply("Erreur : identifiant du plot invalide."));
                        return true;
                    }

                    final PlayerServer finalTarget = target;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        World loaded = Plot.getWorldforname(plotName);
                        if (loaded == null) {
                            player.sendMessage(MessageColor.RED.apply(
                                    "Erreur : impossible de charger le plot !"));
                            return;
                        }
                        teleportSafe(player, finalTarget, loaded);
                    }, 60L);
                    return true;
                }

                teleportSafe(player, target, targetPlotWorld);
                return true;
            }

            Inventory inventaire0 = MenuPlotVisit.createmenuplotvisit("plotvisit", 0);
            if (inventaire0 == null) {
                player.sendMessage(MessageColor.RED.apply("Erreur du côté serveur."));
                return true;
            }
            player.openInventory(inventaire0);
            return true;
        }


        // /plot buy
        if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("b")) {
            if (!requireOwnPlot(player, playerServer)) {
                return true;
            }

            String plotWorldName = playerServer.getPlotdata().getNameWorld();
            if (plotWorldName == null || !plotWorldName.equalsIgnoreCase(player.getWorld().getName())) {
                player.sendMessage(MessageColor.RED.apply(
                        "Merci d'étre sur votre Plot pour effectuée cette commande"));
                return true;
            }

            Inventory inventaire0 = MenuPlotUpgrade.createmenuplotUpgrade("plotupgrade", 0, playerServer);
            if (inventaire0 == null) {
                player.sendMessage(MessageColor.RED.apply("Erreur du côté serveur."));
                return true;
            }

            player.openInventory(inventaire0);
            return true;
        }

        // /plot config
        if (args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("c")) {
            if (!requireOwnPlot(player, playerServer)) {
                return true;
            }

            String plotWorldName = playerServer.getPlotdata().getNameWorld();
            if (plotWorldName == null || !plotWorldName.equalsIgnoreCase(player.getWorld().getName())) {
                player.sendMessage(MessageColor.RED.apply(
                        "Merci d'étre sur votre Plot pour effectuée cette commande"));
                return true;
            }

            Inventory inventaireconfig = MenuPlotConfig.createmenuplotconfig("plotconfig", playerServer);
            if (inventaireconfig == null) {
                player.sendMessage(MessageColor.RED.apply("Erreur du côté serveur"));
                return true;
            }

            player.openInventory(inventaireconfig);
            return true;
        }

        // /plot setspawnpoint
        if (args[0].equalsIgnoreCase("setspawnpoint")
                || args[0].equalsIgnoreCase("setspawn")) {
            if (!requireOwnPlot(player, playerServer)) {
                return true;
            }

            String plotWorldName = playerServer.getPlotdata().getNameWorld();
            if (plotWorldName == null || !plotWorldName.equalsIgnoreCase(player.getWorld().getName())) {
                player.sendMessage(MessageColor.RED.apply(
                        "Merci d'être sur votre Plot pour effectuer cette commande"));
                return true;
            }

            playerServer.getPlotdata().setLocationspawnX(player.getLocation().getBlockX());
            playerServer.getPlotdata().setLocationspawnY(player.getLocation().getBlockY());
            playerServer.getPlotdata().setLocationspawnZ(player.getLocation().getBlockZ());

            player.sendMessage(MessageColor.GREEN.apply("Zone de spawn définie !"));
            return true;
        }

        player.sendMessage(MessageColor.YELLOW.apply(
                "Sous-commande inconnue. Utilise: /plot <add/unadd/trust/untrust/buy/visit/home/config/setspawnpoint>"));
        return true;
    }

    private boolean requireOwnPlot(Player player, PlayerServer playerServer) {
        if (playerServer.getPlotdata() != null) {
            return true;
        }

        player.sendMessage(MessageColor.RED.apply("Vous ne possédez pas encore de plot."));
        return false;
    }

    private String getOwnPlotName(PlayerServer playerServer) {
        String plotName = playerServer.getPlotdata().getPlotProprety();
        if (plotName == null || plotName.isBlank()) {
            plotName = playerServer.getPlotdata().getNameWorld();
        }
        return plotName;
    }

    public boolean canManagePlotAccess(Player player, PlayerServer playerServer,
            String worldName, String trust) {
        if (playerServer == null || playerServer.getPlotdata() == null
                || worldName == null || worldName.isBlank()) {
            return false;
        }

        String ownerplot = playerServer.getPlotdata().getPlotProprety();
        ArrayList<String> listtrust = playerServer.getPlotdata().getAllplottrust();

        if (ownerplot != null && ownerplot.equalsIgnoreCase(worldName)) {
            return true;
        }

        return !trust.equalsIgnoreCase("trust")
                && listtrust != null
                && listtrust.contains(worldName);
    }

    private void teleportSafe(Player player, PlayerServer target, World world) {
        if (target == null || target.getPlotdata() == null || world == null) {
            player.sendMessage(MessageColor.RED.apply("Erreur : données du plot introuvables."));
            return;
        }

        int spawnX = target.getPlotdata().getLocationspawnX();
        int spawnY = target.getPlotdata().getLocationspawnY();
        int spawnZ = target.getPlotdata().getLocationspawnZ();

        int tx = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 0 : spawnX;
        int tz = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 0 : spawnZ;
        int ty = (spawnX == 0 && spawnY == 0 && spawnZ == 0) ? 64 : spawnY;

        world.loadChunk(tx >> 4, tz >> 4, true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int highY = world.getHighestBlockYAt(tx, tz);
            if (highY < world.getMinHeight() + 5) {
                highY = 64;
            }

            int startY = Math.max(ty, highY);
            Location safe = null;

            for (int y = startY; y <= world.getMaxHeight() - 2; y++) {
                if (!world.getBlockAt(tx, y, tz).getType().isSolid()
                        && !world.getBlockAt(tx, y + 1, tz).getType().isSolid()
                        && world.getBlockAt(tx, y - 1, tz).getType().isSolid()) {
                    safe = new Location(world, tx + 0.5, y, tz + 0.5);
                    break;
                }
            }

            if (safe == null) {
                safe = new Location(world, tx + 0.5, highY + 1, tz + 0.5);
            }

            player.teleport(safe);

            String targetName = target.getName() != null ? target.getName() : "ce joueur";
            player.sendMessage(MessageColor.GREEN.apply(
                    "Téléportation vers le plot de " + targetName));
        }, 10L);
    }

    public boolean isNotAddedToPlot(Player player, PlayerServer playerServer, String plotName) {
        if (playerServer == null || playerServer.getPlotdata() == null || plotName == null) {
            return false;
        }

        String ownerPlot = playerServer.getPlotdata().getPlotProprety();
        ArrayList<String> listadd = playerServer.getPlotdata().getAllplotadd();

        if (ownerPlot != null && ownerPlot.equals(plotName)) {
            return false;
        }

        return listadd == null || !listadd.contains(plotName);
    }

    public boolean isNotTrustedOnPlot(Player player, PlayerServer playerServer, String plotName) {
        if (playerServer == null || playerServer.getPlotdata() == null || plotName == null) {
            return false;
        }

        String ownerPlot = playerServer.getPlotdata().getPlotProprety();
        ArrayList<String> listtrust = playerServer.getPlotdata().getAllplottrust();

        if (ownerPlot != null && ownerPlot.equals(plotName)) {
            return false;
        }

        return listtrust == null || !listtrust.contains(plotName);
    }
}
