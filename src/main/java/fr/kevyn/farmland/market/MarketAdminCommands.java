package fr.kevyn.farmland.market;

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
 *   /marketadmin set <metier> <valeur>   -> force manuellement un coefficient (50-150)
 *   /marketadmin reset                   -> remet tous les coefficients à 100
 *   /marketadmin recalc                  -> force un recalcul immédiat (sans attendre 30 min)
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
            sender.sendMessage("§6/marketadmin set <metier> <valeur> §7- force un coefficient (50-150)");
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
        sender.sendMessage("§7Mineur : §f" + market.getMoneyforcoefMineur() + "%");
        sender.sendMessage("§7Farmeur : §f" + market.getMoneyforcoefFarmeur() + "%");
        sender.sendMessage("§7Agriculteur : §f" + market.getMoneyforcoefAgriculteur() + "%");
        sender.sendMessage("§7Pecheur : §f" + market.getMoneyforcoefPecheur() + "%");
        sender.sendMessage("§7Tueur : §f" + market.getMoneyforcoefTueur() + "%");
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
        valeur = Math.max(50, Math.min(150, valeur));

        Market market = MarketHolder.get();
        switch (args[1].toLowerCase()) {
            case "mineur": market.setMoneyforcoefMineur(valeur); break;
            case "farmeur": market.setMoneyforcoefFarmeur(valeur); break;
            case "agriculteur": market.setMoneyforcoefAgriculteur(valeur); break;
            case "pecheur": market.setMoneyforcoefPecheur(valeur); break;
            case "tueur": market.setMoneyforcoefTueur(valeur); break;
        }

        fr.kevyn.farmland.save.MarketSave.saveMarket(plugin, market);
        MarketHolograms.updateAll(plugin);
        sender.sendMessage("§a" + args[1] + " fixé à " + valeur + "% !");
        return true;
    }

    private boolean resetCommand(CommandSender sender) {
        Market market = MarketHolder.get();
        market.setMoneyforcoefMineur(100);
        market.setMoneyforcoefFarmeur(100);
        market.setMoneyforcoefAgriculteur(100);
        market.setMoneyforcoefPecheur(100);
        market.setMoneyforcoefTueur(100);

        MarketCalc.resetVentes();
        fr.kevyn.farmland.save.MarketSave.saveMarket(plugin, market);
        MarketHolograms.updateAll(plugin);
        sender.sendMessage("§aTous les coefficients remis à 100% !");
        return true;
    }

    private boolean recalcCommand(CommandSender sender) {
        MarketCalcTask.forcerRecalcul(plugin);
        sender.sendMessage("§aRecalcul forcé effectué !");
        return true;
    }
}
