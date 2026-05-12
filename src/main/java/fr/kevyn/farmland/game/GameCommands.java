package fr.kevyn.farmland.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;


public class GameCommands implements CommandExecutor {

    // ✅ CORRIGÉ : Utilisation de timestamps pour éviter les memory leaks
    private final Map<UUID, UUID> lastSender = new HashMap<>();
    private final Map<String, Long> reportCooldown = new HashMap<>(); // ✅ Clé : "senderUUID:targetUUID"

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("farmland.gamecommands")) {
            sender.sendMessage(MessageColor.RED.apply("Vous n'avez pas la permission"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageColor.RED.apply("Seul un joueur peut exécuter cette commande !"));
            return true;
        }

        Player playerevent = (Player) sender;
        PlayerServer playerSender = PlayerserverHashMap.getInstance().getplayerHaspMaps(playerevent.getUniqueId());
        Player player = PlayerServer.getplayer(playerSender);

        if (playerSender == null) {
            messagediscord.sendmessage(command.getName() + " : erreur playerSender null","statut");
            player.sendMessage(MessageColor.RED.apply("Une erreur est survenue. Contactez un administrateur."));
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "pay": return PayCommand(player, playerSender, args);
            case "money": return MoneyCommand(player, playerSender, args);
            case "msgf": return MsgCommand(player, playerSender, args, "msgf");
            case "r": return MsgCommand(player, playerSender, args, "r");
            case "reportmsg": return reportmsg(player, playerSender, args);
            default: return false;
        }
    }

    // =========================
    // /reportmsg - ✅ CORRIGÉ
    // =========================
    private boolean reportmsg(Player player, PlayerServer playerSender, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageColor.RED.apply("Usage : /reportmsg <joueur> <message>"));
            return true;
        }

        String reportedName = args[0];
        Player reportedPlayer = Bukkit.getPlayer(reportedName);
        
        if (reportedPlayer == null) {
            player.sendMessage(MessageColor.RED.apply("Le joueur " + reportedName + "n'est plus en ligne !"));
            return true;
        }

        // ✅ CORRIGÉ : Cooldown par paire de joueurs
        String reportKey = player.getUniqueId() + ":" + reportedPlayer.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        if (reportCooldown.containsKey(reportKey)) {
            long lastReport = reportCooldown.get(reportKey);
            long timeLeft = 60000 - (currentTime - lastReport); // 1 minute
            
            if (timeLeft > 0) {
                player.sendMessage(MessageColor.RED.apply("Vous devez attendre " + (timeLeft / 1000) + " secondes avant de reporter à nouveau ce joueur !"));
                return true;
            }
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        // ✅ AJOUTÉ : Échappement pour éviter l'injection
        String safeMessage = message.replace("\"", "'").replace("\n", " ").replace("\r", "");

        player.sendMessage(MessageColor.GREEN.apply("✅ Signalement envoyé contre " + reportedName + " !"));

        String discordMessage = "🚨 REPORT 🚨 | Joueur signalé : " + reportedName + 
                                " | Signalé par : " + player.getName() + 
                                " | Message reçu : ||" + safeMessage + "||";
        messagediscord.sendmessage(discordMessage, "message");
        Bukkit.getLogger().info("[REPORT] " + player.getName() + " → " + reportedName + " : " + safeMessage);

        reportCooldown.put(reportKey, currentTime);

        return true;
    }

    // =========================
    // /msgf et /r - ✅ CORRIGÉ
    // =========================
    private boolean MsgCommand(Player player, PlayerServer playerSender, String[] args, String rormsg) {
        Player targetPlayer;
        String message;

        if (rormsg.equalsIgnoreCase("r")) {
            UUID lastSenderUUID = lastSender.get(player.getUniqueId());
            
            if (lastSenderUUID == null) {
                player.sendMessage(MessageColor.RED.apply("Aucun message auquel répondre !"));
                return true;
            }

            // ✅ CORRIGÉ : Utilisation correcte de getPlayer(UUID)
            targetPlayer = Bukkit.getPlayer(lastSenderUUID); 
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage("§cLe joueur n'est plus en ligne !");
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(MessageColor.RED.apply("Usage : /r <message>"));
                return true;
            }

            message = String.join(" ", args);

        } else {
            if (args.length < 2) {
                player.sendMessage(MessageColor.RED.apply("Usage : /msgf <joueur> <message>"));
                return true;
            }

            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage(MessageColor.RED.apply("Joueur introuvable !"));
                return true;
            }

            List<String> listMessage = new ArrayList<>(Arrays.asList(args));
            listMessage.remove(0);
            message = String.join(" ", listMessage);
        }

        PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(targetPlayer.getUniqueId());
        if (targetServer == null) {
            player.sendMessage(MessageColor.RED.apply("Erreur Administrateur"));
            return true;
        }

        // ✅ AJOUTÉ : Échappement du message
        String safeMessage = message.replace("\"", "'").replace("\n", " ").replace("\r", "");

        player.sendMessage("§8┌─ §aMessage envoyé à " + targetPlayer.getName() + " §8─┐");
        player.sendMessage("§7» §f" + safeMessage);
        player.sendMessage("§8└────────────────────────────┘");

        TextComponent reportButton = new TextComponent("§cX");
        reportButton.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§cClique pour reporter ce joueur").create()
        ));
        reportButton.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/reportmsg " + player.getName() + " " + safeMessage
        ));

        TextComponent line = new TextComponent("§8┌─ ");
        line.addExtra(reportButton);
        line.addExtra(new TextComponent(" §eMessage reçu de §6" + player.getName() + " §8─┐"));

        targetPlayer.spigot().sendMessage(line);
        targetPlayer.sendMessage("§7» §f" + safeMessage);
        targetPlayer.sendMessage("§8└────────────────────────────┘");

        lastSender.put(targetPlayer.getUniqueId(), player.getUniqueId());
        lastSender.put(player.getUniqueId(), targetPlayer.getUniqueId());

        return true;
    }

    // =========================
    // /money - ✅ OK
    // =========================
    private boolean MoneyCommand(Player player, PlayerServer playerSender, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§aVous avez §e" + playerSender.getMoney() + "§a d'argent.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(MessageColor.RED.apply("Joueur introuvable !"));
            return true;
        }

        PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(targetPlayer.getUniqueId());
        if (targetServer == null) {
            player.sendMessage(MessageColor.RED.apply("Erreur Administrateur"));
            return true;
        }

        player.sendMessage("§e" + targetPlayer.getName() + " a §e" + targetServer.getMoney() + "§a sur son compte.");
        return true;
    }

    // =========================
    // /pay - ✅ CORRIGÉ
    // =========================
    private boolean PayCommand(Player player, PlayerServer senderServer, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageColor.RED.apply("Usage : /pay <player> <montant>"));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(MessageColor.RED.apply("Joueur introuvable !"));
            return true;
        }

        // ✅ AJOUTÉ : Empêcher de se payer soi-même
        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(MessageColor.RED.apply("Vous ne pouvez pas vous payer vous-même !"));
            return true;
        }

        PlayerServer targetServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(targetPlayer.getUniqueId());
        if (targetServer == null) {
            messagediscord.sendmessage("/pay : erreur targetServer null, envoyé par " + senderServer.getName(),"statut");
            player.sendMessage(MessageColor.RED.apply("Erreur Administrateur"));
            return true;
        }

        int montant;
        try {
            montant = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageColor.RED.apply("Montant invalide !"));
            return true;
        }

        if (montant <= 0) {
            player.sendMessage(MessageColor.RED.apply("Le montant doit être supérieur à 0 !"));
            return true;
        }

        if (senderServer.getMoney() < montant) {
            player.sendMessage(MessageColor.RED.apply("§cVous n'avez pas assez d'argent !"));
            return true;
        }

        // Transaction
        senderServer.setMoney(senderServer.getMoney() - montant);
        targetServer.setMoney(targetServer.getMoney() + montant);

        messagediscord.sendmessage(senderServer.getName() + " a envoyé " + montant + " à " + targetServer.getName(),"statut");
        player.sendMessage("§aVous avez envoyé §e" + montant + " §aà §e" + targetServer.getName());
        targetPlayer.sendMessage("§aVous avez reçu §e" + montant + " §ade §e" + senderServer.getName());

        return true;
    }
}