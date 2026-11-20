package fr.kevyn.farmland.directives.administration;

import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotUpgrade;
import fr.kevyn.farmland.directives.plot.gestion.PlotBordure;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.persistance.joueurs.PlayerSave;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;

import java.io.IOException;


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
        sender.sendMessage("§eUpgrade plot : §f" + ps.getUpgrade());
        sender.sendMessage("§eBlocs posés (total) : §f" + ps.getBlocposetotal());
        sender.sendMessage("§eBordure du plot : §f" + (ps.getPlotdata() != null ? ps.getPlotdata().getWorldborder() : "aucun plot"));
        // lastjoin n'est qu'un drapeau (vrai/faux) : la date vient du serveur (Paper)
        long vuLe = ps.getUuid() == null ? 0 : Bukkit.getOfflinePlayer(ps.getUuid()).getLastSeen();
        sender.sendMessage("§eDernière connexion : §f" + (online != null ? "en ligne" : derniereConnexion(vuLe)));
        return true;
    }

    /** « 20/11/2026 à 14:51 », ou « inconnue » si le serveur ne l'a jamais vu. */
    static String derniereConnexion(long epochMs) {
        if (epochMs <= 0) return "inconnue";
        return java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm")
                .format(java.time.Instant.ofEpochMilli(epochMs).atZone(java.time.ZoneId.systemDefault()));
    }

    private boolean moneyCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("farmland.admin")) { // argent : réservé aux admins (avant : farmland.moderation suffisait)
            sender.sendMessage("§cCette commande est réservée aux administrateurs.");
            return true;
        }
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

        PlayerSave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aArgent de " + ps.getName() + " : §f" + ps.getMoney() + " $FB");
        plugin.getLogger().info("[PsAdmin] " + sender.getName() + " -> money " + args[2] + " " + montant + " sur " + ps.getName() + " (nouveau solde : " + ps.getMoney() + ")");
        JournalEvenements.enregistrer("admin_argent", ps.getName(), args[2].toLowerCase() + " " + montant + " $FB par " + sender.getName() + ", solde " + ps.getMoney() + " $FB");

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
        int max = MenuPlotUpgrade.getMaxUpgrades();
        if (rang < 0 || rang > max) {
            sender.sendMessage("§cLe rang doit être entre 0 et " + max + " !");
            return true;
        }

        // ajuste la bordure du plot en fonction du changement de rang (+5 par upgrade)
        int ancienRang = ps.getUpgrade();
        if (ps.getPlotdata() != null && ps.getPlotdata().getWorldborder() + (rang - ancienRang) * 5 < 1) {
            sender.sendMessage("§cCe rang donnerait une bordure de " + (ps.getPlotdata().getWorldborder() + (rang - ancienRang) * 5) + " : refusé (minimum 1).");
            return true;
        }
        ps.setUpgrade(rang);
        if (ps.getPlotdata() != null) {
            int delta = (rang - ancienRang) * 5;
            ps.getPlotdata().setWorldborder(ps.getPlotdata().getWorldborder() + delta);
            PlotBordure.appliquer(plugin, ps); // bordure réelle mise à jour tout de suite
        }

        PlayerSave.saveOnePlayerServerFile(plugin, ps);
        sender.sendMessage("§aUpgrade de " + ps.getName() + " : " + ancienRang + " → " + rang + " (bordure ajustée)");
        plugin.getLogger().info("[PsAdmin] " + sender.getName() + " -> upgrade " + ancienRang + " -> " + rang + " sur " + ps.getName());
        JournalEvenements.enregistrer("admin_rang", ps.getName(), "rang " + ancienRang + " -> " + rang + " par " + sender.getName());
        return true;
    }

    private boolean saveallCommand(CommandSender sender) {
        // JSON préparés sur le fil principal, fichiers écrits en arrière-plan
        PlayerSave.saveAllPlayerServerFileAsync(plugin);
        sender.sendMessage("§aSauvegarde de tous les joueurs lancée !");
        plugin.getLogger().info("[PsAdmin] " + sender.getName() + " a lance une sauvegarde globale");
        return true;
    }
}
