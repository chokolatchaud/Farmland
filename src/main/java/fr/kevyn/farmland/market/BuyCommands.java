package fr.kevyn.farmland.market;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuyCommands implements CommandExecutor {

    private static final int WE_COST = 15;            // coût en $FB
    private static final long WE_DURATION_MS = 60L * 60 * 1000; // 1 heure en ms
    private static final String WE_PERMISSION = "farmland.worldedit";
    private static final String[] WE_NATIVE_PERMISSIONS = {
        "worldedit.region.*",
        "worldedit.selection.*",
        "worldedit.wand.*",
        "worldedit.fill.*",
        "worldedit.brush.*",
        "worldedit.drain",
        "worldedit.fixwater",
        "worldedit.history.undo.*",
        "worldedit.analysis.count",
        "worldedit.biome.list",
        "worldedit.biome.set",
        "worldedit.calc",
        "worldedit.extinguish",
        "fawe.worldeditregion"
    };

    // Map pour stocker les joueurs en attente de confirmation
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
        if (args.length < 1) {
            player.sendMessage(MessageColor.RED.apply("Usage : /buy worldedit"));
            return true;
        }

        if (!args[0].equalsIgnoreCase("worldedit")) {
            player.sendMessage(MessageColor.RED.apply("Produit inconnu. Disponible : worldedit"));
            return true;
        }

        // Vérification du solde
        if (ps.getMoney() < WE_COST) {
            player.sendMessage(MessageColor.RED.apply("Solde insuffisant ! Il te faut " + WE_COST + " $FB."));
            player.sendMessage(MessageColor.GRAY.apply("Ton solde : " + ps.getMoney() + " $FB"));
            return true;
        }

        // Si déjà en attente de confirmation
        if (pendingConfirm.containsKey(player.getUniqueId())) {
            long asked = pendingConfirm.get(player.getUniqueId());
            // Expire après 30 secondes
            if (System.currentTimeMillis() - asked > 30_000) {
                pendingConfirm.remove(player.getUniqueId());
            } else {
                // Confirmation reçue
                pendingConfirm.remove(player.getUniqueId());
                activateWorldEdit(player, ps);
                return true;
            }
        }

        // Afficher le prix et demander confirmation
        String tempsActuel = ps.isWeActive() ? MessageColor.GREEN.apply("(déjà actif — le temps sera ajouté)") : "";
        player.sendMessage(MessageColor.GOLD.apply("=== /buy worldedit ==="));
        player.sendMessage(MessageColor.GRAY.apply("Prix : ") + MessageColor.WHITE.apply(WE_COST + " $FB"));
        player.sendMessage(MessageColor.GRAY.apply("Durée : ") + MessageColor.WHITE.apply("1 heure"));
        if (!tempsActuel.isEmpty()) player.sendMessage(tempsActuel);
        player.sendMessage(MessageColor.YELLOW.apply("Tape /buy worldedit à nouveau pour confirmer."));

        pendingConfirm.put(player.getUniqueId(), System.currentTimeMillis());

        // Annule la confirmation après 30s
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
        // Débiter le solde
        ps.setMoney(ps.getMoney() - WE_COST);

        // Calculer le nouveau timestamp d'expiration (ajoute au temps restant si déjà actif)
        long now = System.currentTimeMillis();
        long currentExpiry = ps.isWeActive() ? ps.getWeTimeExpiry() : now;
        long newExpiry = currentExpiry + WE_DURATION_MS;
        ps.setWeTimeExpiry(newExpiry);

        // Donner la permission farmland.worldedit + permissions FAWE natives
        LuckPerms lp = LuckPermsProvider.get();
        User user = lp.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            user.data().add(Node.builder(WE_PERMISSION).value(true).build());
            for (String perm : WE_NATIVE_PERMISSIONS) {
                user.data().add(Node.builder(perm).value(true).build());
            }
            lp.getUserManager().saveUser(user);
        }

        player.sendMessage(MessageColor.GREEN.apply("✔ WorldEdit activé pour 1 heure ! (-" + WE_COST + " $FB)"));
        player.sendMessage(MessageColor.GRAY.apply("Solde restant : " + ps.getMoney() + " $FB"));

        // Tâche qui retire la permission à expiration
        long ticksUntilExpiry = (newExpiry - now) / 50L;
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            // Vérifier que la permission n'a pas été renouvelée entre-temps
            PlayerServer psNow = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
            if (psNow != null && !psNow.isWeActive()) {
                User u = lp.getUserManager().getUser(player.getUniqueId());
                if (u != null) {
                    u.data().remove(Node.builder(WE_PERMISSION).value(true).build());
                    for (String perm : WE_NATIVE_PERMISSIONS) {
                        u.data().remove(Node.builder(perm).value(true).build());
                    }
                    lp.getUserManager().saveUser(u);
                }
                if (player.isOnline()) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(MessageColor.RED.apply("Ton WorldEdit a expiré. Tape /buy worldedit pour renouveler."))
                    );
                }
            }
        }, ticksUntilExpiry);
    }
}