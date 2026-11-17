package fr.kevyn.farmland.directives.gameplay.commands;

import fr.kevyn.farmland.directives.marché.commands.BuyCommands;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * /vote — affiche les sites de vote (config.yml → vote.sites)
 * et rappelle la récompense.
 */
public class VoteCommand implements CommandExecutor {

    private final FarmlandMain plugin;

    public VoteCommand(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut exécuter cette commande !");
            return true;
        }
        Player player = (Player) sender;

        List<String> sites = plugin.getConfig().getStringList("vote.sites");

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD));
        player.sendMessage(Component.text("  FARMLAND — Vote pour le serveur", NamedTextColor.GOLD, TextDecoration.BOLD));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD));
        player.sendMessage(Component.text(""));

        if (sites.isEmpty()) {
            player.sendMessage(Component.text("  Aucun site de vote configuré pour l'instant.", NamedTextColor.GRAY));
        } else {
            int i = 1;
            for (String url : sites) {
                player.sendMessage(
                    Component.text("  " + i + ". ", NamedTextColor.GRAY)
                        .append(Component.text(url, NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl(url)))
                );
                i++;
            }
        }

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  Récompense : ", NamedTextColor.GRAY)
                .append(Component.text("+1 h de WorldEdit gratuit par vote", NamedTextColor.YELLOW)));
        player.sendMessage(Component.text("  (Ou /buy worldedit : 1 h de WorldEdit pour " + BuyCommands.WE_COST + " $FB)", NamedTextColor.DARK_GRAY));
        player.sendMessage(Component.text(""));
        return true;
    }
}
