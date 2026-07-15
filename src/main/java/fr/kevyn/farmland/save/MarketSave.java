package fr.kevyn.farmland.save;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.kevyn.farmland.market.Market;

public class MarketSave {

    private static final String MARKET_HISTORY_FILE = "market_history.json";
    private static final int MAX_HISTORY = 100;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    
    

    // ===== CLASSE POUR L'HISTORIQUE (sans LocalDateTime) =====
    public static class MarketSnapshot {
        public Market market;
        public String timestamp;  // ✅ String au lieu de LocalDateTime

        public MarketSnapshot(Market market, String timestamp) {
            this.market = market;
            this.timestamp = timestamp;
        }
    }

    public static class MarketHistory {
        public LinkedList<MarketSnapshot> history;

        public MarketHistory() {
            this.history = new LinkedList<>();
        }
    }

    // ===== SAUVEGARDER LE MARKET (ajoute à l'historique) =====
    public static void saveMarket(JavaPlugin plugin,Market market) {
        if (market == null) return;

        // 1. Charger l'historique existant
        MarketHistory marketHistory = loadMarketHistory(plugin);
        if (marketHistory == null) {
            marketHistory = new MarketHistory();
        }

        // 2. Ajouter le nouveau market avec timestamp en String
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        marketHistory.history.add(new MarketSnapshot(market, timestamp));

        // 3. Garder seulement les 20 derniers
        while (marketHistory.history.size() > MAX_HISTORY) {
            marketHistory.history.removeFirst();
        }

        // 4. Sauvegarder l'historique
        saveMarketHistory(plugin,marketHistory);
        
        plugin.getLogger().info("Market sauvegardé ! (Historique: " + marketHistory.history.size() + "/20)");
    }

    // ===== SAUVEGARDER L'HISTORIQUE DANS LE FICHIER =====
    private static void saveMarketHistory(JavaPlugin plugin,MarketHistory marketHistory) {
        File file = new File(plugin.getDataFolder(), MARKET_HISTORY_FILE);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(marketHistory));
            writer.flush();
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde de l'historique !");
            e.printStackTrace();
        }
    }

    // ===== CHARGER L'HISTORIQUE DEPUIS LE FICHIER =====
    public static MarketHistory loadMarketHistory(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), MARKET_HISTORY_FILE);

        if (!file.exists()) {
            return null;
        }

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(file)) {
            MarketHistory marketHistory = gson.fromJson(reader, MarketHistory.class);
            if (marketHistory != null && marketHistory.history != null) {
                plugin.getLogger().info("Historique chargé ! (" + marketHistory.history.size() + " entrées)");
                return marketHistory;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors du chargement de l'historique !");
            e.printStackTrace();
        }
        
        return null;
    }

    // ===== CHARGER LE DERNIER MARKET =====
    public static Market loadMarket(JavaPlugin plugin) {
        MarketHistory history = loadMarketHistory(plugin);
        
        if (history == null || history.history.isEmpty()) {
            return null;
        }

        return history.history.getLast().market;
    }

    // ===== RÉCUPÉRER TOUT L'HISTORIQUE =====
    public static List<MarketSnapshot> getFullHistory(JavaPlugin plugin) {
        MarketHistory history = loadMarketHistory(plugin);
        
        if (history == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(history.history);
    }

    // ===== RÉCUPÉRER UN MARKET ANTÉRIEUR (0 = le plus ancien, 19 = le plus récent) =====
    public static Market getMarketAt(JavaPlugin plugin,int index) {
        List<MarketSnapshot> history = getFullHistory(plugin);
        
        if (index < 0 || index >= history.size()) {
            return null;
        }
        
        return history.get(index).market;
    }

    // ===== AFFICHER L'HISTORIQUE =====
    public static void displayHistory(JavaPlugin plugin) {
        List<MarketSnapshot> history = getFullHistory(plugin);
        
        System.out.println("=== HISTORIQUE DU MARKET ===");
        System.out.println("Total: " + history.size() + "/20");
        System.out.println();
        
        for (int i = 0; i < history.size(); i++) {
            MarketSnapshot snapshot = history.get(i);
            Market m = snapshot.market;
            System.out.println((i + 1) + ". " + snapshot.timestamp);
            System.out.println("   Créativité: " + m.getMoneyforcoefCréativité() + 
                             " | Architecture: " + m.getMoneyforcoefArchitecture() +
                             " | Densité: " + m.getMoneyforcoefDensité());
            System.out.println();
        }
    }

    // ===== RÉINITIALISER L'HISTORIQUE =====
    public static void resetHistory(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), MARKET_HISTORY_FILE);
        if (file.exists()) {
            file.delete();
            plugin.getLogger().info("Historique du market réinitialisé !");
        }
    }
}