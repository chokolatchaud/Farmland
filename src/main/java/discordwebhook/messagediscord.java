package discordwebhook;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.kevyn.farmland.FarmlandMain;

public class messagediscord {
	
	private static FarmlandMain plugin;
	
	// a appeler une fois au demarrage du plugin (dans FarmlandMain.onEnable), avant tout sendmessage
	public static void init(FarmlandMain pluginInstance) {
		plugin = pluginInstance;
		plugin.saveDefaultConfig();
	}
	
	public static void sendmessage(String content, String salon) {
	    try {
	    	if (plugin == null) {
	    		System.out.println("⚠ messagediscord.init() pas appelé, message ignoré : " + content);
	    		return;
	    	}
	    	
	        // Récupération des URLs de webhook depuis config.yml (a configurer sur le serveur, JAMAIS dans le repo Git)
	        String webhookUrlStatue = plugin.getConfig().getString("discord.webhook-status", "");
	        String webhookUrlMSG = plugin.getConfig().getString("discord.webhook-message", "");
	        String webhookUrlModeration = plugin.getConfig().getString("discord.webhook-moderation", "");

	    
	        // Sélection du webhook selon le salon
	        String webhookUrl = webhookUrlStatue;
	        if (salon.equalsIgnoreCase("message")) {
	            webhookUrl = webhookUrlMSG;
	        } else if (salon.equalsIgnoreCase("statut") || salon.equalsIgnoreCase("status")) {
	            webhookUrl = webhookUrlStatue;
	        } else if (salon.equalsIgnoreCase("moderation")) {
	        	webhookUrl = webhookUrlModeration;
	        }
	        
	        if (webhookUrl == null || webhookUrl.isEmpty()) {
	        	System.out.println("⚠ Webhook Discord non configuré pour le salon \"" + salon + "\" (voir config.yml)");
	        	return;
	        }
	        
	        // Connexion HTTP
	        
	        URL url = new URL(webhookUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setDoOutput(true);
	        connection.setRequestProperty("Content-Type", "application/json");
	        
	        // Échappement correct pour JSON (guillemets + retours à la ligne)
	        String escapedContent = content
	            .replace("\\", "\\\\")  // Échappe les backslashes
	            .replace("\"", "\\\"")  // Échappe les guillemets
	            .replace("\n", "\\n")   // Échappe les retours à la ligne
	            .replace("\r", "\\r")   // Échappe les retours chariot
	            .replace("\t", "\\t");  // Échappe les tabulations
	        
	        // Format JSON du message
	        String jsonPayload = String.format("{\"content\":\"%s\"}", escapedContent);
	        
	        // Envoi du contenu
	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = jsonPayload.getBytes("utf-8");
	            os.write(input, 0, input.length);
	        }
	        
	        // Lecture du code de réponse (utile pour debug)
	        int responseCode = connection.getResponseCode();
	        if (responseCode != 204 && responseCode != 200) {
	            System.out.println("⚠ Erreur envoi Discord : code HTTP " + responseCode);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}