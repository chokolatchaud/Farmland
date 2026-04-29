package fr.kevyn.farmland.EventBuild;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class LuckpermGrade implements Listener {

    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    // 🔁 Méthode statique pour mise à jour du grade Farmland selon LuckPerms
    public static void updateGrade(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        String groupName = user.getPrimaryGroup();
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return;

        ps.setGrade(groupName);
        player.sendMessage("§aTon grade Farmland a été synchronisé §e" + groupName);
    }
}