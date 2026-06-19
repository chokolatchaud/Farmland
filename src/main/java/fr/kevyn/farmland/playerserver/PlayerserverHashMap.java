package fr.kevyn.farmland.playerserver;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerserverHashMap {
    private static final PlayerserverHashMap INSTANCE = new PlayerserverHashMap();
    // ConcurrentHashMap au lieu de HashMap : la sauvegarde tourne en async (runTaskAsynchronously)
    // pendant qu'un joueur peut se connecter/déconnecter sur le thread principal en même temps
    private final Map<UUID, PlayerServer> players = new ConcurrentHashMap<>();
    // ✅ AJOUTÉ : Cache par nom pour performance
    private final Map<String, UUID> nameToUUID = new ConcurrentHashMap<>();
    
    public static PlayerserverHashMap getInstance() {
        return INSTANCE;
    }
    
    // ✅ CORRIGÉ : Mise à jour du cache
    public void AddplayerHaspMaps(UUID uuid, PlayerServer playerServer) {
        players.put(uuid, playerServer);
        if (playerServer.getName() != null) {
            nameToUUID.put(playerServer.getName().toLowerCase(), uuid);
        }
    }
    
    public PlayerServer getplayerHaspMaps(UUID uuid) {
        return players.get(uuid);
    }
    
    // ✅ CORRIGÉ : Recherche O(1) au lieu de O(n)
    public PlayerServer getplayerHaspMaps(String name) {
        UUID uuid = nameToUUID.get(name.toLowerCase());
        return uuid != null ? players.get(uuid) : null;
    }
    
    public Map<UUID, PlayerServer> getHashMapPlayer() {
        return players;
    }
    
    // ✅ AJOUTÉ : Méthode pour mettre à jour le cache quand un nom change
    public void updatePlayerName(UUID uuid, String oldName, String newName) {
        if (oldName != null) {
            nameToUUID.remove(oldName.toLowerCase());
        }
        if (newName != null) {
            nameToUUID.put(newName.toLowerCase(), uuid);
        }
    }
}