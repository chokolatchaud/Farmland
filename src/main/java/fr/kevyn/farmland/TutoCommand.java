package fr.kevyn.farmland;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TutoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        Player player = (Player) sender;

        titre(player, "FARMLAND — Guide du joueur");

        section(player, "§e🏗 CONSTRUIS ET GAGNE", NamedTextColor.YELLOW,
            "Construis librement sur ton plot, puis //wand + /define <nom> pour",
            "noter ta structure (créativité, architecture, densité, finition,",
            "équilibre). Elle rapporte 150-500 $FB toutes les 30 min, même",
            "hors ligne (20% du taux). /market pour voir les prix en direct."
        );

        section(player, "§6🏠 AMÉLIORE TON PLOT", NamedTextColor.GOLD,
            "/plot buy agrandit ta bordure (+5 blocs par upgrade). Système",
            "infini : le prix monte de +20 $FB tous les 21 achats."
        );

        section(player, "§b🚤 COURSE DE BATEAUX", NamedTextColor.AQUA,
            "/joinboat depuis N'IMPORTE OÙ pour rejoindre la prochaine course.",
            "Passe les 3 points de contrôle dans l'ordre puis franchis l'arrivée.",
            "Podium du jour : 500 / 250 / 100 $FB pour le top 3 !"
        );

        section(player, "§d🗳 VOTE ET COSMÉTIQUES", NamedTextColor.LIGHT_PURPLE,
            "/vote affiche les sites de vote (+1h WorldEdit par vote).",
            "/buy cosmetic pour t'acheter un chapeau exclusif à porter."
        );

        section(player, "§c⏱ ATTENTION À L'AFK", NamedTextColor.RED,
            "20 min sans bouger = statut AFK. Tes structures rapportent alors",
            "seulement 20% le temps que tu sois inactif. Reste actif !"
        );

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  📖 Guide complet avec illustrations :", NamedTextColor.GRAY));
        player.sendMessage(
            Component.text("  ➜ https://farm-land.fr/guide", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://farm-land.fr/guide"))
        );

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD));

        return true;
    }

    private void titre(Player player, String texte) {
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD));
        player.sendMessage(Component.text("  " + texte, NamedTextColor.GOLD, TextDecoration.BOLD));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD));
    }

    private void section(Player player, String titre, NamedTextColor couleur, String... lignes) {
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text(titre, couleur, TextDecoration.BOLD));
        for (String ligne : lignes) {
            player.sendMessage(Component.text("  " + ligne, NamedTextColor.GRAY));
        }
    }
}
