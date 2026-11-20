package fr.kevyn.farmland.directives.marché.commands;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /shop : ouvre la boutique (BoutiqueMenu) pour un joueur ;
 * depuis la console, affiche la liste des commandes d'achat et de vente.
 */
public final class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (sender instanceof org.bukkit.entity.Player joueur) {
            fr.kevyn.farmland.directives.marché.boutique.BoutiqueListener.ouvrir(
                    org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(ShopCommand.class), joueur,
                    fr.kevyn.farmland.directives.marché.boutique.BoutiqueMenu.Page.ACCUEIL, false);
            return true;
        }
        sender.sendMessage(MessageColor.GOLD.apply("═══ Boutique FarmLand ═══"));
        sender.sendMessage(MessageColor.YELLOW.apply("Acheter :"));
        sender.sendMessage(MessageColor.WHITE.apply(" /buy worldedit ") + MessageColor.GRAY.apply("- 1 h de WorldEdit pour "
                + BuyCommands.WE_COST + " $FB"));
        sender.sendMessage(MessageColor.WHITE.apply(" /buy cosmetic ") + MessageColor.GRAY.apply("- chapeaux et cosmétiques"));
        sender.sendMessage(MessageColor.WHITE.apply(" /plot buy ") + MessageColor.GRAY.apply("- agrandir ton plot"));
        sender.sendMessage(MessageColor.YELLOW.apply("Vendre :"));
        sender.sendMessage(MessageColor.WHITE.apply(" /bag ") + MessageColor.GRAY.apply("- vends les jetons de tes métiers"));
        sender.sendMessage(MessageColor.WHITE.apply(" /market ") + MessageColor.GRAY.apply("- prix actuels du marché"));
        return true;
    }
}
