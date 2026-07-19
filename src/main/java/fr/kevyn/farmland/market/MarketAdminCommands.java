package fr.kevyn.farmland.market;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.save.MarketSave;

/**
 * /marketadmin - commandes admin du marché (permission farmland.admin)
 *   /marketadmin info                      -> valeurs actuelles + taille historique
 *   /marketadmin set <coef> <valeur>       -> modifie un coefficient (creativite, architecture, densite, equilibre, finition)
 *   /marketadmin reset                     -> remet tous les coefficients à 50
 *   /marketadmin recalc                    -> force un recalcul complet du marché
 */
public class MarketAdminCommands implements CommandExecutor {

    private final FarmlandMain plugin;

    public MarketAdminCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6/marketadmin info §7- valeurs actuelles du marché");
            sender.sendMessage("§6/marketadmin set <coef> <valeur> §7- modifie un coefficient");
            sender.sendMessage("§6/marketadmin reset §7- remet tout à 50");
            sender.sendMessage("§6/marketadmin recalc §7- force un recalcul du marché");
            sender.sendMessage("§6/marketadmin holo §7- gère les hologrammes du marché");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info": return infoCommand(sender);
            case "set": return setCommand(sender, args);
            case "reset": return resetCommand(sender);
            case "recalc": return recalcCommand(sender);
            case "holo": return holoCommand(sender, args);
            default:
                sender.sendMessage("§cSous-commande inconnue ! (/marketadmin pour l'aide)");
                return true;
        }
    }

    private boolean holoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6/marketadmin holo set <coef> §7- pose l'hologramme à ta position");
            sender.sendMessage("§6/marketadmin holo remove <coef> §7- supprime l'hologramme");
            sender.sendMessage("§6/marketadmin holo list §7- liste les hologrammes posés");
            sender.sendMessage("§7Coefs : creativite, architecture, densite, equilibre, finition");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "set": {
                if (!(sender instanceof org.bukkit.entity.Player)) {
                    sender.sendMessage("§cSeul un joueur peut poser un hologramme !");
                    return true;
                }
                if (args.length < 3 || !MarketHolograms.isValidCoef(args[2])) {
                    sender.sendMessage("§cCoef inconnu ! (creativite, architecture, densite, equilibre, finition)");
                    return true;
                }
                org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
                MarketHolograms.setHologram(plugin, args[2], player.getLocation());
                sender.sendMessage("§aHologramme §e" + args[2].toLowerCase() + "§a posé à ta position !");
                plugin.getLogger().info("[MarketAdmin] " + sender.getName() + " a pose l'hologramme " + args[2].toLowerCase());
                return true;
            }
            case "remove": {
                if (args.length < 3 || !MarketHolograms.isValidCoef(args[2])) {
                    sender.sendMessage("§cCoef inconnu !");
                    return true;
                }
                if (MarketHolograms.removeHologram(plugin, args[2])) {
                    sender.sendMessage("§aHologramme §e" + args[2].toLowerCase() + "§a supprimé !");
                } else {
                    sender.sendMessage("§cAucun hologramme posé pour ce coef !");
                }
                return true;
            }
            case "list": {
                if (MarketHolograms.getEmplacements().isEmpty()) {
                    sender.sendMessage("§7Aucun hologramme posé. (/marketadmin holo set <coef>)");
                    return true;
                }
                sender.sendMessage("§6═══ Hologrammes du marché ═══");
                for (java.util.Map.Entry<String, MarketHolograms.HoloLoc> e : MarketHolograms.getEmplacements().entrySet()) {
                    sender.sendMessage("§e" + e.getKey() + " §7→ " + holoPosition(e.getValue()));
                }
                return true;
            }
            default:
                sender.sendMessage("§cUsage : /marketadmin holo <set|remove|list>");
                return true;
        }
    }

    private String holoPosition(MarketHolograms.HoloLoc loc) {
        return "§f" + loc.world + " §7(" + (int) loc.x + ", " + (int) loc.y + ", " + (int) loc.z + ")";
    }

    private boolean infoCommand(CommandSender sender) {
        Market market = MarketSave.loadMarket(plugin);
        if (market == null) {
            sender.sendMessage("§cAucun marché sauvegardé !");
            return true;
        }
        int taille = MarketSave.getFullHistory(plugin).size();
        sender.sendMessage("§6═══ Marché actuel ═══");
        sender.sendMessage("§eCréativité : §f" + market.getMoneyforcoefCréativité() + "$");
        sender.sendMessage("§eArchitecture : §f" + market.getMoneyforcoefArchitecture() + "$");
        sender.sendMessage("§eDensité : §f" + market.getMoneyforcoefDensité() + "$");
        sender.sendMessage("§eÉquilibre : §f" + market.getMoneyforcoefÉquilibre() + "$");
        sender.sendMessage("§eFinition : §f" + market.getMoneyforcoefFinition() + "$");
        sender.sendMessage("§7Historique : " + taille + " entrée(s)");
        return true;
    }

    private boolean setCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage : /marketadmin set <coef> <valeur>");
            return true;
        }

        Market market = MarketSave.loadMarket(plugin);
        if (market == null) {
            sender.sendMessage("§cAucun marché sauvegardé ! Fais /marketadmin reset pour l'initialiser.");
            return true;
        }

        int valeur;
        try {
            valeur = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide !");
            return true;
        }
        if (valeur < 1 || valeur > 500) {
            sender.sendMessage("§cLa valeur doit être entre 1 et 500 !");
            return true;
        }

        int creativite   = market.getMoneyforcoefCréativité();
        int architecture = market.getMoneyforcoefArchitecture();
        int densite      = market.getMoneyforcoefDensité();
        int equilibre    = market.getMoneyforcoefÉquilibre();
        int finition     = market.getMoneyforcoefFinition();

        switch (args[1].toLowerCase()) {
            case "creativite": creativite = valeur; break;
            case "architecture": architecture = valeur; break;
            case "densite": densite = valeur; break;
            case "equilibre": equilibre = valeur; break;
            case "finition": finition = valeur; break;
            default:
                sender.sendMessage("§cCoef inconnu ! (creativite, architecture, densite, equilibre, finition)");
                return true;
        }

        Market nouveau = new Market(creativite, architecture, densite, equilibre, finition);
        MarketSave.saveMarket(plugin, nouveau);
        MarketCalc.pushMarketToWebApi(plugin);
        sender.sendMessage("§aCoef " + args[1].toLowerCase() + " mis à " + valeur + "$ et poussé vers le site !");
        plugin.getLogger().info("[MarketAdmin] " + sender.getName() + " a mis " + args[1].toLowerCase() + " a " + valeur);
        return true;
    }

    private boolean resetCommand(CommandSender sender) {
        Market nouveau = new Market(50, 50, 50, 50, 50);
        MarketSave.saveMarket(plugin, nouveau);
        MarketCalc.pushMarketToWebApi(plugin);
        sender.sendMessage("§aMarché remis à 50$ sur tous les coefficients !");
        plugin.getLogger().info("[MarketAdmin] " + sender.getName() + " a reset le marche");
        return true;
    }

    private boolean recalcCommand(CommandSender sender) {
        MarketCalc.Calcforcoef(plugin);
        sender.sendMessage("§aRecalcul du marché effectué ! (/marketadmin info pour voir le résultat)");
        plugin.getLogger().info("[MarketAdmin] " + sender.getName() + " a force un recalcul");
        return true;
    }
}
