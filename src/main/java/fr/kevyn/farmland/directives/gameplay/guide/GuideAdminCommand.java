package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * /guideadmin holo set <thème>     -> pose l'hologramme de ce thème à ta position
 * /guideadmin holo remove <thème>  -> le supprime
 * /guideadmin holo list            -> liste les hologrammes posés
 * Thèmes : bienvenue, plot, metiers, vendre, depenser, activites. Permission farmland.admin.
 */
public final class GuideAdminCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public GuideAdminCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        String themes = String.join(", ", GuideTextes.HOLOGRAMMES.keySet());
        if (args.length < 2 || !args[0].equalsIgnoreCase("holo")) {
            sender.sendMessage(MessageColor.GOLD.apply("/guideadmin holo set <thème>") + MessageColor.GRAY.apply(" pose l'hologramme à ta position"));
            sender.sendMessage(MessageColor.GOLD.apply("/guideadmin holo remove <thème>") + MessageColor.GRAY.apply(" le supprime"));
            sender.sendMessage(MessageColor.GOLD.apply("/guideadmin holo list") + MessageColor.GRAY.apply(" liste les hologrammes posés"));
            sender.sendMessage(MessageColor.GRAY.apply("Thèmes : " + themes));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "set" -> {
                if (!(sender instanceof Player joueur)) {
                    sender.sendMessage(MessageColor.RED.apply("Seul un joueur peut poser un hologramme."));
                } else if (args.length < 3 || !GuideTextes.themeExiste(args[2])) {
                    sender.sendMessage(MessageColor.RED.apply("Thème inconnu ! (" + themes + ")"));
                } else {
                    GuideHolograms.poser(plugin, args[2], joueur.getLocation());
                    sender.sendMessage(MessageColor.GREEN.apply("Hologramme du guide « " + args[2].toLowerCase() + " » posé ici."));
                }
            }
            case "remove" -> {
                if (args.length < 3 || !GuideTextes.themeExiste(args[2])) {
                    sender.sendMessage(MessageColor.RED.apply("Thème inconnu ! (" + themes + ")"));
                } else if (GuideHolograms.retirer(plugin, args[2])) {
                    sender.sendMessage(MessageColor.GREEN.apply("Hologramme « " + args[2].toLowerCase() + " » supprimé."));
                } else {
                    sender.sendMessage(MessageColor.RED.apply("Aucun hologramme posé pour ce thème."));
                }
            }
            case "list" -> {
                if (GuideHolograms.getEmplacements().isEmpty()) {
                    sender.sendMessage(MessageColor.GRAY.apply("Aucun hologramme du guide posé."));
                } else {
                    for (Map.Entry<String, GuideHolograms.HoloLoc> e : GuideHolograms.getEmplacements().entrySet()) {
                        sender.sendMessage(MessageColor.YELLOW.apply(e.getKey()) + MessageColor.GRAY.apply(" → " + e.getValue().world));
                    }
                }
            }
            default -> sender.sendMessage(MessageColor.RED.apply("Usage : /guideadmin holo <set|remove|list> [thème]"));
        }
        return true;
    }
}
