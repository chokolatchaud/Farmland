package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /guide        -> l'essentiel dans le chat (court)
 * /guide livre  -> donne le livre guide (tout de A à Z), un seul dans l'inventaire
 */
public final class GuideCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("livre")) {
            if (!(sender instanceof Player joueur)) {
                sender.sendMessage(MessageColor.RED.apply("Seul un joueur peut recevoir le livre."));
                return true;
            }
            if (GuideLivre.possede(joueur)) {
                joueur.sendMessage(MessageColor.YELLOW.apply("Tu as déjà le livre guide dans ton inventaire."));
            } else if (GuideLivre.donnerSiAbsent(joueur)) {
                joueur.sendMessage(MessageColor.GREEN.apply("Tu as reçu le livre guide : tout FarmLand de A à Z !"));
            }
            return true;
        }
        envoyerGuide(sender);
        return true;
    }

    public static void envoyerGuide(CommandSender destinataire) {
        for (String ligne : GuideTextes.GUIDE_CHAT) {
            destinataire.sendMessage(ligne);
        }
    }
}
