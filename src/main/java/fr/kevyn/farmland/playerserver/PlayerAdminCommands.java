package fr.kevyn.farmland.playerserver;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.save.Filesave;

/**
 * /psadmin - gestion admin des PlayerServer (permission farmland.admin)
 *   /psadmin info <joueur>                        -> infos complètes du joueur
 *   /psadmin money <joueur> <set|add|remove> <n>  -> gère l'argent
 *   /psadmin upgrade <joueur> set <n>             -> change le rang d'upgrade (ajuste la bordure)
 *   /psadmin saveall                              -> sauvegarde tous les joueurs maintenant
 */
public class PlayerAdminCommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public PlayerAdminCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/psadmin info <joueur> §7- infos du joueur");
            sender.sendMessage("§6/psadmin money <joueur> <set|add|remove> <montant> §7- gère l'argent");
            sender.sendMessage("§6/psadmin upgrade <joueur> set <rang> §7- change le rang d'upgrade");
            sender.sendMessage("§6/psadmin saveall §7- sauvegarde tous les joueurs");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info": return infoCommand(sender, args);
            case "money": return moneyCommand(sender, args);
            case "upgrade": return upgradeCommand(sender, args);
            case "saveall": return saveallCommand(sender);
            default:
                sender.sendMessage("§cSous-commande inconnue ! (/psadmin pour l'aide)");
                return true;
        }
    }

    /** Récupère un PlayerServer par nom, en ligne ou hors ligne (tant qu'il est en mémoire) */
    private PlayerServer getTarget(CommandSender sender, String name) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(name);
        if (ps == null) {
            sender.sendMessage("§cJoueur introuvable ! (il doit s'être connecté au moins une fois depuis le démarrage)");
        }
        return ps;
    }

    private boolean infoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /psadmin info <joueur>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        Player online = Bukkit.getPlayerExact(ps.getName());
        sender.sendMessage("§6═══ " + ps.getName() + (online != null ? " §a(en ligne)" : " §7(hors ligne)") + " §6═══");
        sender.sendMessage("§eArgent : §f" + ps.getMoney() + " $FB");
        sender.sendMessage("§eGains hors ligne en attente : §f" + ps.getMoneyoffline() + " $FB");
        sender.sendMessage("§eUpgrade plot : §f" + ps.getUpgrade());
        sender.sendMessage("§eBlocs posés (total) : §f" + ps.getBlocposetotal());
        sender.sendMessage("§eBordure du plot : §f" + (ps.getPlotdata() != null ? ps.getPlotdata().getWorldborder() : "aucun plot"));
        sender.sendMessage("§eDernière connexion : §f" + (ps.getLastjoin() != null ? ps.getLastjoin().toString() : "inconnue"));
        return true;
    }

    private boolean moneyCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage : /psadmin money <joueur> <set|add|remove> <montant>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        int montant;
        try {
            montant = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cMontant invalide !");
            return true;
        }
        if (montant < 0) {
            sender.sendMessage("§cLe montant doit être positif !");
            return true;
        }

        switch (args[2].toLowerCase()) {
            case "set":
                ps.setMoney(montant);
                break;
            case "add":
                ps.setMoney(ps.getMoney() + montant);
                break;
            case "remove":
                ps.setMoney(Math.max(0, ps.getMoney() - montant));
                break;
            default:
                sender.sendMessage("§cAction inconnue ! (set, add, remove)");
                return true;
        }

        Filesave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aArgent de " + ps.getName() + " : §f" + ps.getMoney() + " $FB");
        plugin.getLogger().info("[PsAdmin] " + sender.getName() + " -> money " + args[2] + " " + montant + " sur " + ps.getName() + " (nouveau solde : " + ps.getMoney() + ")");

        Player online = Bukkit.getPlayerExact(ps.getName());
        if (online != null) {
            online.sendMessage("§6✦ Un admin a modifié ton argent : tu as maintenant " + ps.getMoney() + " $FB");
        }
        return true;
    }

    private boolean upgradeCommand(CommandSender sender, String[] args) {
        if (args.length < 4 || !args[2].equalsIgnoreCase("set")) {
            sender.sendMessage("§cUsage : /psadmin upgrade <joueur> set <rang>");
            return true;
        }
        PlayerServer ps = getTarget(sender, args[1]);
        if (ps == null) return true;

        int rang;
        try {
            rang = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cRang invalide !");
            return true;
        }
        int max = fr.kevyn.farmland.menu.MenuPlotUpgrade.getMaxUpgrades();
        if (rang < 0 || rang > max) {
            sender.sendMessage("§cLe rang doit être entre 0 et " + max + " !");
            return true;
        }

        // ajuste la bordure du plot en fonction du changement de rang (+5 par upgrade)
        int ancienRang = ps.getUpgrade();
        ps.setUpgrade(rang);
        if (ps.getPlotdata() != null) {
            int delta = (rang - ancienRang) * 5;
            ps.getPlotdata().setWorldborder(ps.getPlotdata().getWorldborder() + delta);
        }

        Filesave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aUpgrade de " + ps.getName() + " : " + ancienRang + " → " + rang + " (bordure ajustée)");
        plugin.getLogger().info("[PsAdmin] " + sender.getName() + " -> upgrade " + ancienRang + " -> " + rang + " sur " + ps.getName());
        return true;
    }

    private boolean saveallCommand(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Filesave.SavePlayerserverFile(plugin);
        });
        sender.sendMessage("§aSauvegarde de tous les joueurs lancée !");
        plugin.getLogger().info("[PsAdmin] " + sender.getName() + " a lance une sauvegarde globale");
        return true;
    }
}
