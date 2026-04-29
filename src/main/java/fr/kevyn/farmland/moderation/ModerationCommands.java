package fr.kevyn.farmland.moderation;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.save.Filesave;

public class ModerationCommands implements CommandExecutor {

    private FarmlandMain plugin;

    public ModerationCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("farmland.moderation")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut faire cette commande.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("unbanf")) {
            // /unbanf <joueur>
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /unbanf <joueur>");
                return true;
            }

            PlayerServer targetPS = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[0]);
            if (targetPS == null) {
                player.sendMessage(ChatColor.RED + "Joueur inconnu, impossible de le débannir.");
                return true;
            }

            if (!targetPS.getBan()) {
                player.sendMessage(ChatColor.GREEN + "Le joueur n'est pas banni.");
                return true;
            }

            targetPS.setBan(false);
            targetPS.setRaison("");
            Filesave.saveOnePlayerServerFile(plugin, targetPS);

            Player targetPlayer = Bukkit.getPlayer(targetPS.getUuid());
            if (targetPlayer != null) {
                targetPlayer.sendMessage(ChatColor.GREEN + "Vous avez été débanni !");
            }

            player.sendMessage(ChatColor.GREEN + "Le joueur " + targetPS.getName() + " a été débanni avec succès.");
            messagediscord.sendmessage("🔓 **Débannissement** | " + targetPS.getName() + " a été débanni par " + player.getName(), "moderation");
            return true;
        }

        // Pour ban, kick et warn, il faut au moins 2 arguments
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <joueur> <raison>");
            return true;
        }

        PlayerServer targetPS = PlayerserverHashMap.getInstance().getplayerHaspMaps(args[0]);
        if (targetPS == null) {
            player.sendMessage(ChatColor.RED + "Joueur inconnu.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPS.getUuid());
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        switch (cmd) {
            case "banf":
                targetPS.setBan(true);
                targetPS.setRaison(reason);

                if (targetPlayer != null) {
                    targetPlayer.kickPlayer(ChatColor.RED + "Vous êtes banni !" +
                            "\nRaison : " + reason);
                }

                player.sendMessage(ChatColor.RED + "Le joueur a été banni définitivement.");
                messagediscord.sendmessage("🔨 **Bannissement** | " + targetPS.getName() + " a été banni définitivement\n" +
                        "**Raison :** " + reason + "\n" +
                        "**Modérateur :** " + player.getName(), "moderation");
                Filesave.saveOnePlayerServerFile(plugin, targetPS);
                return true;

            case "kickf":
                if (targetPlayer != null) targetPlayer.kickPlayer(reason);
                targetPS.setRaison(reason);
                player.sendMessage(ChatColor.YELLOW + "Le joueur a été expulsé.");
                messagediscord.sendmessage("👢 **Expulsion** | " + targetPS.getName() + " a été expulsé du serveur\n" +
                        "**Raison :** " + reason + "\n" +
                        "**Modérateur :** " + player.getName(), "moderation");
                Filesave.saveOnePlayerServerFile(plugin, targetPS);
                return true;

            case "warnf":
                if (targetPlayer != null) {
                    targetPlayer.sendMessage(ChatColor.RED + "Avertissement : " + reason);
                    targetPlayer.sendTitle(
                            ChatColor.RED + "Avertissement !",
                            ChatColor.YELLOW + reason,
                            10, 60, 10
                    );
                }
                player.sendMessage(ChatColor.GOLD + "Le joueur a été averti.");
                messagediscord.sendmessage("⚠️ **Avertissement** | " + targetPS.getName() + " a reçu un avertissement\n" +
                        "**Raison :** " + reason + "\n" +
                        "**Modérateur :** " + player.getName(), "moderation");
                return true;

            default:
                return false;
        }
    }

    public boolean playerIsOnline(PlayerServer targetPlayerServer) {
        return Bukkit.getPlayer(targetPlayerServer.getUuid()) != null;
    }
}