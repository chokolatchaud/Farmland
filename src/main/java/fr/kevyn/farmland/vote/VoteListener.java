package fr.kevyn.farmland.vote;

import com.vexsoftware.votifier.model.VotifierEvent;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;

public class VoteListener implements Listener {

    private final FarmlandMain plugin;
    private final File pendingFile;
    private YamlConfiguration pending;

    public VoteListener(FarmlandMain plugin) {
        this.plugin = plugin;
        this.pendingFile = new File(plugin.getDataFolder(), "pendingvotes.yml");
        this.pending = YamlConfiguration.loadConfiguration(pendingFile);
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        String username = event.getVote().getUsername();
        Player player = Bukkit.getPlayerExact(username);

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player != null && player.isOnline()) {
                giveReward(player, 1);
            } else {
                // Joueur hors ligne → on stocke le vote, il sera récompensé à sa connexion
                int count = pending.getInt(username.toLowerCase(), 0) + 1;
                pending.set(username.toLowerCase(), count);
                savePending();
                plugin.getLogger().info("[Vote] " + username + " a voté (hors ligne) — " + count + " récompense(s) en attente");
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String key = player.getName().toLowerCase();
        int count = pending.getInt(key, 0);
        if (count <= 0) return;

        pending.set(key, null);
        savePending();

        // Petit délai pour laisser le PlayerServer se charger
        Bukkit.getScheduler().runTaskLater(plugin, () -> giveReward(player, count), 40L);
    }

    private void giveReward(Player player, int votes) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) {
            plugin.getLogger().warning("[Vote] Impossible de récompenser " + player.getName() + " (données introuvables)");
            return;
        }

        int reward = 15 * votes;
        ps.setMoney(ps.getMoney() + reward);
        player.sendMessage(MessageColor.GOLD.apply("✦ Merci pour " + (votes > 1 ? "tes " + votes + " votes" : "ton vote") + " ! Tu reçois +" + reward + " $FB !"));
        player.sendMessage(MessageColor.GRAY.apply("Tape /buy worldedit pour acheter 1h de WorldEdit (15 $FB)"));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(MessageColor.YELLOW.apply("✦ " + player.getName() + " a voté pour le serveur ! Merci !"));
        }
        plugin.getLogger().info("[Vote] " + player.getName() + " → +" + reward + " $FB accordés (" + votes + " vote(s))");
    }

    private void savePending() {
        try {
            pending.save(pendingFile);
        } catch (IOException e) {
            plugin.getLogger().warning("[Vote] Impossible de sauvegarder pendingvotes.yml : " + e.getMessage());
        }
    }
}
