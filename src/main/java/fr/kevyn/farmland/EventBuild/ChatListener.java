package fr.kevyn.farmland.EventBuild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;

public class ChatListener implements Listener {

    private final LuckPerms luckPerms;

    public ChatListener() {
        this.luckPerms = LuckPermsProvider.get();
        //test
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player playerevent = event.getPlayer();
        PlayerServer playerserver = PlayerserverHashMap.getInstance().getplayerHaspMaps(playerevent.getUniqueId());
        Player player = PlayerServer.getplayer(playerserver);
        if(!playerevent.equals(player)) {
        	playerevent.kickPlayer("Erreur 23 Rapprocher vous d'un modérateur");
        }


        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        CachedMetaData metaData = user.getCachedData().getMetaData();

        String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";
        String suffix = metaData.getSuffix() != null ? metaData.getSuffix() : "";

        // Nouveau format du message
        String format = ChatColor.translateAlternateColorCodes('&',
                prefix + player.getName() + suffix + " &7: " + event.getMessage());

        event.setFormat(format);
    }
    
    public static void updateTab(Player player) {
        LuckPerms luckPerms = Bukkit.getServicesManager().load(LuckPerms.class);

        if (luckPerms == null) return;

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";


        // ----------------------------
        // Tab list : prefix -> 1 lettre
        // ----------------------------
        String tabPrefix = " "; // par défaut espace
        if (prefix.contains("Admin")) tabPrefix = "&4" + "A-";
        else if (prefix.contains("Mod")) tabPrefix = "&b" + "M-";
        else if (prefix.contains("Buildeur")) tabPrefix = "&2" + "B-";
        else if (prefix.contains("Joueur")) tabPrefix = "&8" + "J-";

        // Nom complet pour le tab (pseudo entier + préfixe 1 lettre)
        String tabName = tabPrefix + player.getName();

        // Définir le nom dans la tab
        player.setPlayerListName(tabName);

        
    }
}