package fr.kevyn.farmland.directives.marché.commands;

import fr.kevyn.farmland.directives.marché.affichage.MarketHolograms;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;
import fr.kevyn.farmland.directives.marché.calcul.MarketCalcTask;
import fr.kevyn.farmland.doonees.marche.Market;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.persistance.marché.MarketSave;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;

/**
 * /marketadmin - commandes admin du marché par métier (permission farmland.moderation)
 *   /marketadmin info                    -> affiche les 5 coefficients actuels
 *   /marketadmin holo set <metier>       -> pose l'hologramme de ce métier à ta position
 *   /marketadmin holo remove <metier>    -> le supprime
 *   /marketadmin holo list               -> liste les hologrammes posés
 *   /marketadmin set <metier> <valeur>   -> force manuellement un coefficient (11-189), via le surplus du marché
 *   /marketadmin reset                   -> remet tous les surplus à 0 (coefficients à 100)
 *   /marketadmin recalc                  -> force un cycle immédiat (demande + bulletin)
 *
 * Métiers valides : Mineur, Farmeur, Agriculteur, Pecheur, Tueur
 */
public class MarketAdminCommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public MarketAdminCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/marketadmin info §7- affiche les coefficients actuels");
            sender.sendMessage("§6/marketadmin holo set <metier> §7- pose l'hologramme à ta position");
            sender.sendMessage("§6/marketadmin holo remove <metier> §7- le supprime");
            sender.sendMessage("§6/marketadmin holo list §7- liste les hologrammes posés");
            sender.sendMessage("§6/marketadmin set <metier> <valeur> §7- force un coefficient (11-189)");
            sender.sendMessage("§6/marketadmin reset §7- remet tout à 100");
            sender.sendMessage("§6/marketadmin recalc §7- force un recalcul immédiat");
            sender.sendMessage("§7Métiers : Mineur, Farmeur, Agriculteur, Pecheur, Tueur");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info": return infoCommand(sender);
            case "holo": return holoCommand(sender, args);
            case "set": return setCommand(sender, args);
            case "reset": return resetCommand(sender);
            case "recalc": return recalcCommand(sender);
            default:
                sender.sendMessage("§cSous-commande inconnue ! (/marketadmin pour l'aide)");
                return true;
        }
    }

    private boolean infoCommand(CommandSender sender) {
        Market market = MarketHolder.get();
        sender.sendMessage("§6═══ Marché actuel ═══");
        for (String metier : MarketCalc.METIERS) {
            sender.sendMessage("§7" + metier + " : §f" + market.getCoefficient(metier) + "%"
                    + " §7(prix " + String.format(java.util.Locale.FRANCE, "%.2f", MarketCalc.getPrixActuel(metier, market))
                    + " / base " + MarketCalc.getPrixDeBase(metier) + " $FB, surplus "
                    + Math.round(MarketCalc.getSurplus(metier, market)) + " jetons, volume normal "
                    + Math.round(MarketCalc.getVolume(metier, market)) + " jetons/h)");
        }
        return true;
    }

    private boolean holoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage : /marketadmin holo <set|remove|list> [metier]");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "set": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cSeul un joueur peut poser un hologramme !");
                    return true;
                }
                if (args.length < 3 || !MarketHolograms.isValidCoef(args[2])) {
                    sender.sendMessage("§cMétier inconnu ! (Mineur, Farmeur, Agriculteur, Pecheur, Tueur)");
                    return true;
                }
                Player player = (Player) sender;
                MarketHolograms.setHologram(plugin, args[2], player.getLocation());
                sender.sendMessage("§aHologramme §e" + args[2] + "§a posé à ta position !");
                return true;
            }
            case "remove": {
                if (args.length < 3 || !MarketHolograms.isValidCoef(args[2])) {
                    sender.sendMessage("§cMétier inconnu !");
                    return true;
                }
                if (MarketHolograms.removeHologram(plugin, args[2])) {
                    sender.sendMessage("§aHologramme §e" + args[2] + "§a supprimé !");
                } else {
                    sender.sendMessage("§cAucun hologramme posé pour ce métier !");
                }
                return true;
            }
            case "list": {
                if (MarketHolograms.getEmplacements().isEmpty()) {
                    sender.sendMessage("§7Aucun hologramme posé. (/marketadmin holo set <metier>)");
                    return true;
                }
                sender.sendMessage("§6═══ Hologrammes du marché ═══");
                for (var entry : MarketHolograms.getEmplacements().entrySet()) {
                    MarketHolograms.HoloLoc loc = entry.getValue();
                    sender.sendMessage("§e" + entry.getKey() + " §7→ §f" + loc.world);
                }
                return true;
            }
            default:
                sender.sendMessage("§cUsage : /marketadmin holo <set|remove|list> [metier]");
                return true;
        }
    }

    private boolean setCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage : /marketadmin set <metier> <valeur>");
            return true;
        }
        if (!MarketHolograms.isValidCoef(args[1])) {
            sender.sendMessage("§cMétier inconnu ! (Mineur, Farmeur, Agriculteur, Pecheur, Tueur)");
            return true;
        }

        int valeur;
        try {
            valeur = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide, entre un nombre entier !");
            return true;
        }
        valeur = Math.max(11, Math.min(189, valeur));

        Market market = MarketHolder.get();
        String metier = null;
        for (String m : MarketCalc.METIERS) {
            if (m.equalsIgnoreCase(args[1])) metier = m;
        }
        if (metier == null) {
            sender.sendMessage("§cMétier inconnu ! (Mineur, Farmeur, Agriculteur, Pecheur, Tueur)");
            return true;
        }
        // le coefficient découle du surplus : on fixe le surplus qui donne cette valeur
        MarketCalc.setSurplus(metier, market, MarketCalc.surplusPourCoefficient(valeur, MarketCalc.echelle(metier, market)));
        valeur = market.getCoefficient(metier);

        MarketSave.saveMarket(plugin, market);
        MarketHolograms.updateAll(plugin);
        sender.sendMessage("§a" + args[1] + " fixé à " + valeur + "% !");
        plugin.getLogger().info("[MarketAdmin] " + sender.getName() + " -> " + args[1].toLowerCase() + " fixé à " + valeur);
        JournalEvenements.enregistrer("admin_marche", sender.getName(), args[1].toLowerCase() + " fixé à " + valeur);
        return true;
    }

    private boolean resetCommand(CommandSender sender) {
        Market market = MarketHolder.get();
        for (String metier : MarketCalc.METIERS) {
            MarketCalc.setSurplus(metier, market, 0.0);
        }

        MarketCalc.resetVentes();
        MarketSave.saveMarket(plugin, market);
        MarketHolograms.updateAll(plugin);
        sender.sendMessage("§aTous les coefficients remis à 100% !");
        plugin.getLogger().info("[MarketAdmin] " + sender.getName() + " -> coefficients remis à 100");
        JournalEvenements.enregistrer("admin_marche", sender.getName(), "coefficients remis à 100");
        return true;
    }

    private boolean recalcCommand(CommandSender sender) {
        MarketCalcTask.forcerRecalcul(plugin);
        sender.sendMessage("§aRecalcul forcé effectué !");
        return true;
    }
}
