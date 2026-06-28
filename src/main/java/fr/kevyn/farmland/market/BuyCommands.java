package fr.kevyn.farmland.market;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuyCommands implements CommandExecutor {

    private static final int WE_COST = 15;
    private static final long WE_DURATION_MS = 60L * 60 * 1000; // 1 heure

    // Stocke les attachments actifs par joueur
    private static final Map<UUID, PermissionAttachment> attachments = new HashMap<>();
    private final Map<UUID, Long> pendingConfirm = new HashMap<>();
    private final FarmlandMain plugin;

    public BuyCommands(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        Player player = (Player) sender;
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return true;

        if (!command.getName().equalsIgnoreCase("buy")) return true;
        if (args.length < 1 || !args[0].equalsIgnoreCase("worldedit")) {
            player.sendMessage(MessageColor.RED.apply("Usage : /buy worldedit"));
            return true;
        }

        if (ps.getMoney() < WE_COST) {
            player.sendMessage(MessageColor.RED.apply("Solde insuffisant ! Il te faut " + WE_COST + " $FB."));
            player.sendMessage(MessageColor.GRAY.apply("Ton solde : " + ps.getMoney() + " $FB"));
            return true;
        }

        // Confirmation
        if (pendingConfirm.containsKey(player.getUniqueId())) {
            long asked = pendingConfirm.get(player.getUniqueId());
            if (System.currentTimeMillis() - asked > 30_000) {
                pendingConfirm.remove(player.getUniqueId());
            } else {
                pendingConfirm.remove(player.getUniqueId());
                activateWorldEdit(player, ps);
                return true;
            }
        }

        String tempsActuel = ps.isWeActive() ? " §a(déjà actif — le temps sera ajouté)" : "";
        player.sendMessage(MessageColor.GOLD.apply("=== /buy worldedit ==="));
        player.sendMessage(MessageColor.GRAY.apply("Prix : ") + MessageColor.WHITE.apply(WE_COST + " $FB"));
        player.sendMessage(MessageColor.GRAY.apply("Durée : ") + MessageColor.WHITE.apply("1 heure" + tempsActuel));
        player.sendMessage(MessageColor.YELLOW.apply("Tape /buy worldedit à nouveau pour confirmer."));

        pendingConfirm.put(player.getUniqueId(), System.currentTimeMillis());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingConfirm.containsKey(player.getUniqueId())) {
                pendingConfirm.remove(player.getUniqueId());
                if (player.isOnline()) {
                    player.sendMessage(MessageColor.RED.apply("Confirmation expirée."));
                }
            }
        }, 30 * 20L);

        return true;
    }

    private void activateWorldEdit(Player player, PlayerServer ps) {
        ps.setMoney(ps.getMoney() - WE_COST);

        long now = System.currentTimeMillis();
        long currentExpiry = ps.isWeActive() ? ps.getWeTimeExpiry() : now;
        long newExpiry = currentExpiry + WE_DURATION_MS;
        ps.setWeTimeExpiry(newExpiry);

        // Supprimer l'ancien attachment s'il existe
        if (attachments.containsKey(player.getUniqueId())) {
            attachments.get(player.getUniqueId()).remove();
            attachments.remove(player.getUniqueId());
        }

        // Créer un nouveau PermissionAttachment avec toutes les permissions WorldEdit
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("farmland.worldedit", true);
        attachment.setPermission("worldedit.region.*", true);
        attachment.setPermission("worldedit.selection.*", true);
        attachment.setPermission("worldedit.wand.*", true);
        attachment.setPermission("worldedit.fill.*", true);
        attachment.setPermission("worldedit.brush.*", true);
        attachment.setPermission("worldedit.drain", true);
        attachment.setPermission("worldedit.fixwater", true);
        attachment.setPermission("worldedit.history.undo.*", true);
        attachment.setPermission("worldedit.analysis.count", true);
        attachment.setPermission("worldedit.biome.list", true);
        attachment.setPermission("worldedit.biome.set", true);
        attachment.setPermission("worldedit.calc", true);
        attachment.setPermission("worldedit.extinguish", true);
        attachment.setPermission("fawe.worldeditregion", true);
        attachments.put(player.getUniqueId(), attachment);

        player.sendMessage(MessageColor.GREEN.apply("✔ WorldEdit activé pour 1 heure ! (-" + WE_COST + " $FB)"));
        player.sendMessage(MessageColor.GRAY.apply("Solde restant : " + ps.getMoney() + " $FB"));

        // Retirer les permissions à expiration
        long ticksUntilExpiry = (newExpiry - now) / 50L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PlayerServer psNow = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (psNow != null && !psNow.isWeActive()) {
                if (attachments.containsKey(player.getUniqueId())) {
                    attachments.get(player.getUniqueId()).remove();
                    attachments.remove(player.getUniqueId());
                }
                if (player.isOnline()) {
                    player.sendMessage(MessageColor.RED.apply("Ton WorldEdit a expiré. Tape /buy worldedit pour renouveler."));
                }
            }
        }, ticksUntilExpiry);
    }

    // Appelé à la déconnexion pour nettoyer les attachments
    public static void removeAttachment(UUID uuid) {
        if (attachments.containsKey(uuid)) {
            attachments.get(uuid).remove();
            attachments.remove(uuid);
        }
    }

    // Appelé à la reconnexion pour restaurer les permissions si WE encore actif
    public static void restoreAttachment(Player player, PlayerServer ps, FarmlandMain plugin) {
        if (!ps.isWeActive()) return;

        // Supprimer l'ancien attachment si existe
        removeAttachment(player.getUniqueId());

        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("farmland.worldedit", true);
        attachment.setPermission("worldedit.region.*", true);
        attachment.setPermission("worldedit.selection.*", true);
        attachment.setPermission("worldedit.wand.*", true);
        attachment.setPermission("worldedit.fill.*", true);
        attachment.setPermission("worldedit.brush.*", true);
        attachment.setPermission("worldedit.drain", true);
        attachment.setPermission("worldedit.fixwater", true);
        attachment.setPermission("worldedit.history.undo.*", true);
        attachment.setPermission("worldedit.analysis.count", true);
        attachment.setPermission("worldedit.biome.list", true);
        attachment.setPermission("worldedit.biome.set", true);
        attachment.setPermission("worldedit.calc", true);
        attachment.setPermission("worldedit.extinguish", true);
        attachment.setPermission("fawe.worldeditregion", true);
        attachments.put(player.getUniqueId(), attachment);

        player.sendMessage(MessageColor.GREEN.apply("✔ WorldEdit restauré — temps restant : "
            + fr.kevyn.farmland.scoreboard.CreativePlotScoreboard.formatWE(ps)));

        // Planifier l'expiration
        long ticksUntilExpiry = ps.getWeTimeRemaining() / 50L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PlayerServer psNow = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (psNow != null && !psNow.isWeActive()) {
                removeAttachment(player.getUniqueId());
                if (player.isOnline()) {
                    player.sendMessage(MessageColor.RED.apply("Ton WorldEdit a expiré. Tape /buy worldedit pour renouveler."));
                }
            }
        }, ticksUntilExpiry);
    }
}