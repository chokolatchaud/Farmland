package fr.kevyn.farmland.EventBuild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.kevyn.farmland.MessageColor;
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
                prefix + "%1$s" + suffix + " &7: %2$s");

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
        String tabPrefix = MessageColor.WHITE.apply(""); // par défaut espace
        if (prefix.contains("Admin")) tabPrefix = MessageColor.RED.apply("A-");
        else if (prefix.contains("Mod")) tabPrefix = MessageColor.YELLOW.apply("M-");
        else if (prefix.contains("Buildeur")) tabPrefix = MessageColor.GREEN.apply("B-");
        else if (prefix.contains("Joueur")) tabPrefix = MessageColor.GRAY.apply("J-");

        // Nom complet pour le tab (pseudo entier + préfixe 1 lettre)
        String tabName = tabPrefix + player.getName();

        // Définir le nom dans la tab
        player.setPlayerListName(tabName);

        
    }
}