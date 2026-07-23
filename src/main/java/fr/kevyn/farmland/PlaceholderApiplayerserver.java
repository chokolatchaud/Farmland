package fr.kevyn.farmland;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderApiplayerserver extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "farmland"; // L'identifiant que tu utiliseras dans les placeholders, ex: %farmland_money%
    }

    @Override
    public @NotNull String getAuthor() {
        return "Kevyn";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // reste actif meme sans joueur connecte (necessaire pour DiscordSRV)
    }

    /**
     * onRequest(OfflinePlayer, ...) au lieu de onPlaceholderRequest(Player, ...) :
     * quand DiscordSRV evalue ce placeholder pour un message venant de Discord,
     * il n'y a PAS de joueur Bukkit "en ligne" (Player) au sens strict - l'expediteur
     * ecrit depuis Discord, pas depuis Minecraft. DiscordSRV fournit alors le compte
     * Minecraft LIE au compte Discord sous forme d'OfflinePlayer (meme hors ligne).
     * getUniqueId() existe sur les deux, la HashMap fonctionne pareil dans les 2 cas.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        if (player == null) return "";

        UUID uuid = player.getUniqueId();
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(uuid);

        if (ps == null) return "";

        switch (identifier.toLowerCase()) {
            case "money":
                return String.valueOf(ps.getMoney());
            case "blocpose":
                return ps.getBlocpose() + "/150";
            case "blocposetotal":
                return String.valueOf(ps.getBlocposetotal());
            case "name":
                return ps.getName();
            case "lastjoin":
                return ps.getLastjoin() != null ? ps.getLastjoin().toString() : "false";
            case "ban":
                return String.valueOf(ps.getBan());
            case "raison":
                return ps.getRaison() != null ? ps.getRaison() : "";
            case "grade":
                return ps.getGrade() != null ? ps.getGrade() : "";
            default:
                return null; // retourne null si le placeholder n'est pas géré
        }
    }
}

