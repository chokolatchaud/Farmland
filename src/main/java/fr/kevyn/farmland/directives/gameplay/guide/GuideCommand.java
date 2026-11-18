package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * /guide        -> ouvre le menu du guide (personnalisé) ; depuis la console : le résumé
 * /guide livre  -> redonne l'objet Guide (un seul dans l'inventaire)
 */
public final class GuideCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public GuideCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player joueur)) {
            envoyerGuide(sender);
            return true;
        }
        if (args.length >= 1 && (args[0].equalsIgnoreCase("livre") || args[0].equalsIgnoreCase("objet"))) {
            if (GuideLivre.possede(joueur)) {
                joueur.sendMessage(MessageColor.YELLOW.apply("Tu as déjà ton Guide dans ton inventaire : clic droit pour l'ouvrir."));
            } else if (GuideLivre.donnerSiAbsent(joueur)) {
                joueur.sendMessage(MessageColor.GREEN.apply("Tu as reçu ton Guide : clic droit pour l'ouvrir !"));
            }
            return true;
        }
        GuideMenuListener.ouvrir(plugin, joueur, GuideMenu.Page.ACCUEIL, false);
        return true;
    }

    public static void envoyerGuide(CommandSender destinataire) {
        for (String ligne : GuideTextes.GUIDE_CHAT) {
            destinataire.sendMessage(ligne);
        }
    }
}
