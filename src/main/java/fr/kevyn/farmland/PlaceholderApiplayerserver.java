package fr.kevyn.farmland;

import java.util.UUID;

import org.bukkit.entity.Player;
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

    /**
     * Called when a placeholder with the identifier %farmland_<something>% is requested
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) return "0";

        UUID uuid = player.getUniqueId();
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(uuid);

        if (ps == null) return "0";

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
