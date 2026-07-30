package fr.kevyn.farmland.chat;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import discordwebhook.messagediscord;

/**
 * Diffuse une annonce aleatoire toutes les 5 minutes, en jeu ET sur Discord.
 * La liste des messages est configurable dans config.yml (annonces.messages).
 */
public class AnnouncementBroadcaster {

    private static final Random random = new Random();

    public static void broadcastRandom(JavaPlugin plugin) {
        List<String> messages = plugin.getConfig().getStringList("annonces.messages");
        if (messages.isEmpty()) return;

        String message = messages.get(random.nextInt(messages.size()));

        Bukkit.broadcastMessage("§d§l✦ ANNONCE ✦ §f" + message);
        messagediscord.sendmessage("📢 " + message, "message");
    }
}
