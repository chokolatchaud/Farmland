package fr.kevyn.plot;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.Filesave;

/**
 * /plotadmin - depannage des plots joueurs (permission farmland.admin)
 *   /plotadmin info <joueur>                  -> infos completes du plot
 *   /plotadmin tp <joueur>                    -> se teleporter sur le plot d'un joueur
 *   /plotadmin border <joueur> <valeur>       -> force la taille de bordure
 *   /plotadmin upgrade <joueur> <rang>        -> force le rang d'upgrade (ajuste la bordure en cascade)
 *   /plotadmin privacy <joueur> <true|false>  -> force la confidentialite du plot
 *   /plotadmin reload <joueur>                -> recharge/regenere le monde du plot s'il est bloque
 *   /plotadmin reset <joueur>                 -> remet le TERRAIN du plot a zero (garde bordure/upgrade)
 */
public class PlotAdminCommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public PlotAdminCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/plotadmin info <joueur> §7- infos du plot");
            sender.sendMessage("§6/plotadmin tp <joueur> §7- te teleporte sur son plot");
            sender.sendMessage("§6/plotadmin border <joueur> <valeur> §7- force la taille de bordure");
            sender.sendMessage("§6/plotadmin upgrade <joueur> <rang> §7- force le rang d'upgrade");
            sender.sendMessage("§6/plotadmin privacy <joueur> <true|false> §7- force la confidentialite");
            sender.sendMessage("§6/plotadmin reload <joueur> §7- recharge le monde du plot");
            sender.sendMessage("§6/plotadmin reset <joueur> §7- remet le TERRAIN a zero (garde bordure/upgrade)");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info": return infoCommand(sender, args);
            case "tp": return tpCommand(sender, args);
            case "border": return borderCommand(sender, args);
            case "upgrade": return upgradeCommand(sender, args);
            case "privacy": return privacyCommand(sender, args);
            case "reload": return reloadCommand(sender, args);
            case "reset": return resetCommand(sender, args);
            default:
                sender.sendMessage("§cSous-commande inconnue ! (/plotadmin pour l'aide)");
                return true;
        }
    }

    private PlayerServer getTarget(CommandSender sender, String name) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(name);
        if (ps == null) {
            sender.sendMessage("§cJoueur introuvable ! (il doit s'être connecté au moins une fois depuis le démarrage)");
            return null;
        }
        if (ps.getPlotdata() == null) {
            sender.sendMessage("§cCe joueur n'a pas encore de plot !");
            return null;
        }
        return ps;
    }

    private boolean infoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /plotadmin info <joueur>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        PlotData plot = ps.getPlotdata();
        sender.sendMessage("§6═══ Plot de " + ps.getName() + " ═══");
        sender.sendMessage("§eNom du plot : §f" + plot.getPlotProprety());
        sender.sendMessage("§eMonde : §f" + plot.getNameWorld());
        sender.sendMessage("§eBordure : §f" + plot.getWorldborder());
        sender.sendMessage("§eRang d'upgrade : §f" + ps.getUpgrade());
        sender.sendMessage("§ePrivé : §f" + plot.getPrivateplot());
        sender.sendMessage("§eBoost : §f" + plot.getBoost());
        sender.sendMessage("§eJoueurs ajoutés : §f" + plot.getAllplotadd().size());
        sender.sendMessage("§eJoueurs de confiance : §f" + plot.getAllplottrust().size());
        return true;
    }

    private boolean tpCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /plotadmin tp <joueur>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        Player admin = (Player) sender;
        String plotName = ps.getPlotdata().getPlotProprety();
        World plotWorld = Plot.getWorldforname(plotName);

        if (plotWorld == null) {
            admin.sendMessage("§7Chargement du plot en cours...");
            new Plot(java.util.UUID.fromString(plotName), plugin);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                World loaded = Plot.getWorldforname(plotName);
                if (loaded == null) { admin.sendMessage("§cImpossible de charger le plot !"); return; }
                admin.teleport(loaded.getSpawnLocation());
                admin.sendMessage("§aTéléporté sur le plot de " + ps.getName() + " !");
            }, 60L);
            return true;
        }

        admin.teleport(plotWorld.getSpawnLocation());
        admin.sendMessage("§aTéléporté sur le plot de " + ps.getName() + " !");
        return true;
    }

    private boolean borderCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage : /plotadmin border <joueur> <valeur>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        int value;
        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide !");
            return true;
        }
        if (value < 1) {
            sender.sendMessage("§cLa bordure doit être positive !");
            return true;
        }

        ps.getPlotdata().setWorldborder(value);
        Filesave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aBordure du plot de " + ps.getName() + " forcée à " + value + " !");
        plugin.getLogger().info("[PlotAdmin] " + sender.getName() + " -> border " + value + " sur " + ps.getName());
        return true;
    }

    private boolean upgradeCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage : /plotadmin upgrade <joueur> <rang>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        int rang;
        try {
            rang = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cRang invalide !");
            return true;
        }
        if (rang < 0) {
            sender.sendMessage("§cLe rang doit être positif !");
            return true;
        }

        int ancienRang = ps.getUpgrade();
        ps.setUpgrade(rang);
        int delta = (rang - ancienRang) * 5;
        ps.getPlotdata().setWorldborder(ps.getPlotdata().getWorldborder() + delta);

        Filesave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aRang d'upgrade de " + ps.getName() + " : " + ancienRang + " → " + rang + " (bordure ajustée)");
        plugin.getLogger().info("[PlotAdmin] " + sender.getName() + " -> upgrade " + ancienRang + " -> " + rang + " sur " + ps.getName());
        return true;
    }

    private boolean privacyCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage : /plotadmin privacy <joueur> <true|false>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        boolean value = Boolean.parseBoolean(args[2]);
        ps.getPlotdata().setPrivateplot(value);
        Filesave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aConfidentialité du plot de " + ps.getName() + " : " + value);
        plugin.getLogger().info("[PlotAdmin] " + sender.getName() + " -> privacy " + value + " sur " + ps.getName());
        return true;
    }

    private boolean reloadCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /plotadmin reload <joueur>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        String plotName = ps.getPlotdata().getPlotProprety();
        World existing = Plot.getWorldforname(plotName);
        if (existing != null) {
            sender.sendMessage("§7Le monde du plot est déjà chargé.");
            return true;
        }

        new Plot(java.util.UUID.fromString(plotName), plugin);
        sender.sendMessage("§aRechargement du plot de " + ps.getName() + " lancé !");
        plugin.getLogger().info("[PlotAdmin] " + sender.getName() + " a recharge le plot de " + ps.getName());
        return true;
    }

    /**
     * Remet le TERRAIN du plot a zero (monde regenere vierge) tout en
     * PRESERVANT la progression du joueur (bordure achetee, rang d'upgrade).
     * Utile pour nettoyer un plot completement sature/grieffe (ex: pile
     * massive de TNT) sans faire perdre au joueur ce qu'il a paye.
     *
     * ⚠️ La ligne "multivers.deleteWorld(...)" utilise l'API Multiverse-Core 5.x
     * (org.mvplugins.multiverse.core.world.options.DeleteWorldOptions) - le nom
     * exact du builder n'a pas pu etre verifie par compilation ici. Si Eclipse
     * signale une erreur sur cette ligne precise, tape "multivers.delete" et
     * laisse l'autocompletion Eclipse te montrer la vraie signature disponible
     * dans ta version exacte du jar Multiverse-Core.
     */
    private boolean resetCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /plotadmin reset <joueur>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        // on sauvegarde la progression AVANT de toucher au monde
        int bordureActuelle = ps.getPlotdata().getWorldborder();
        String plotName = ps.getPlotdata().getPlotProprety();

        sender.sendMessage("§7Reinitialisation du terrain en cours (le monde va etre recree)...");

        org.mvplugins.multiverse.core.MultiverseCoreApi.get().getWorldManager()
            .deleteWorld(org.mvplugins.multiverse.core.world.options.DeleteWorldOptions.world(plotName));

        // recreation d'un monde vierge, exactement comme a la toute premiere creation du plot
        new Plot(java.util.UUID.fromString(plotName), plugin);

        // une fois le nouveau monde genere, on reapplique la bordure (pas la valeur par
        // defaut 50 posee par Plot.initializeWorld, mais celle que le joueur avait deja payee)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World world = Plot.getWorldforname(plotName);
            if (world != null) {
                world.getWorldBorder().setSize(bordureActuelle);
                sender.sendMessage("§aTerrain de " + ps.getName() + " reinitialise ! Bordure (" + bordureActuelle + ") et upgrade (rang " + ps.getUpgrade() + ") conserves.");
                plugin.getLogger().info("[PlotAdmin] " + sender.getName() + " a reset le terrain de " + ps.getName() + " (bordure conservee : " + bordureActuelle + ")");
            } else {
                sender.sendMessage("§cLe monde ne s'est pas encore regenere, reessaie /plotadmin border " + ps.getName() + " " + bordureActuelle + " dans quelques secondes si besoin.");
            }
        }, 100L);

        return true;
    }
}
