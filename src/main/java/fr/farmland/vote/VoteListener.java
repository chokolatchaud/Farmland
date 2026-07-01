package fr.kevyn.farmland.vote;

import com.vexsoftware.votifier.model.VotifierEvent;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {

    private final FarmlandMain plugin;

    public VoteListener(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        String username = event.getVote().getUsername();
        Player player = Bukkit.getPlayerExact(username);

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player != null && player.isOnline()) {
                PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                if (ps != null) {
                    ps.setMoney(ps.getMoney() + 15);
                    player.sendMessage(MessageColor.GOLD.apply("✦ Merci pour ton vote ! Tu reçois +15 $FB !"));
                    player.sendMessage(MessageColor.GRAY.apply("Tape /buy worldedit pour acheter 1h de WorldEdit (15 $FB)"));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(MessageColor.YELLOW.apply("✦ " + username + " a voté pour le serveur ! Merci !"));
                    }
                    plugin.getLogger().info("[Vote] " + username + " a voté → +15 $FB accordés");
                }
            } else {
                plugin.getLogger().info("[Vote] " + username + " a voté (hors ligne) — récompense en attente");
            }
        });
    }
}
