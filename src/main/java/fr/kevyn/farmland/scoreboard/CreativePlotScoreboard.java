package fr.kevyn.farmland.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.structure.GetStructure;

public class CreativePlotScoreboard {

    public static void setscoreboardplot(Player player) {
        Scoreboard scoreboard = boardManager.create();
        if (scoreboard == null) return;

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return;

        int structureCount = 0;
        for (GameRegion region : GetStructure.getallStructure()) {
            if (region.getPropriétaire().equals(player.getUniqueId())) {
                structureCount++;
            }
        }

        Objective obj = scoreboard.registerNewObjective("farmland", "dummy",
                ChatColor.GOLD + "" + ChatColor.BOLD + "FARM & BUILD");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 9;
        setLine(scoreboard, obj, "line_space1", " ",                                                                                    line--);
        setLine(scoreboard, obj, "line_pseudo", ChatColor.GRAY + " Pseudo : "      + ChatColor.WHITE + ps.getName(),                   line--);
        setLine(scoreboard, obj, "line_grade",  ChatColor.GRAY + " Grade : "       + ChatColor.GREEN + ps.getGrade(),                  line--);
        setLine(scoreboard, obj, "line_space2", "  ",                                                                                   line--);
        setLine(scoreboard, obj, "line_money",  ChatColor.GRAY + " Argent : "      + ChatColor.WHITE + format(ps.getMoney()) + " $FB", line--);
        setLine(scoreboard, obj, "line_struct", ChatColor.GRAY + " Structures : "  + ChatColor.WHITE + structureCount + "/5",          line--);
        setLine(scoreboard, obj, "line_blocs",  ChatColor.GRAY + " Blocs posés : " + ChatColor.WHITE + ps.getBlocpose() + "/150",      line--);
        setLine(scoreboard, obj, "line_we",     ChatColor.GRAY + " WorldEdit : "   + formatWE(ps),                                    line--);
        setLine(scoreboard, obj, "line_space3", "   ",                                                                                  line--);
        setLine(scoreboard, obj, "line_ip",     ChatColor.YELLOW + " mine.farm-land.fr",                                               line--);

        player.setScoreboard(scoreboard);
    }

    // Formate le temps WorldEdit restant
    public static String formatWE(fr.kevyn.farmland.playerserver.PlayerServer ps) {
        if (!ps.isWeActive()) {
            return ChatColor.RED + "Inactif" + ChatColor.GRAY + " (/buy worldedit)";
        }
        long ms = ps.getWeTimeRemaining();
        long minutes = ms / 60_000;
        long hours   = minutes / 60;
        long days    = hours / 24;
        long months  = days / 30;

        String time;
        if (months > 0)       time = months + "m";
        else if (days > 0)    time = days + "d";
        else if (hours > 0)   time = hours + "h" + (minutes % 60 > 0 ? (minutes % 60) + "min" : "");
        else                  time = minutes + "min";

        return ChatColor.GREEN + time;
    }

    public static String format(int value) {
        if (value >= 1_000_000) {
            double m = value / 1_000_000.0;
            return (m == (long) m ? String.valueOf((long) m) : String.format("%.1f", m)) + "M";
        } else if (value >= 1_000) {
            double k = value / 1_000.0;
            return (k == (long) k ? String.valueOf((long) k) : String.format("%.1f", k)) + "k";
        }
        return String.valueOf(value);
    }

    private static void setLine(Scoreboard sb, Objective obj, String teamName, String text, int score) {
        String entry = ChatColor.values()[score % ChatColor.values().length] + "" + ChatColor.RESET;
        Team team = sb.registerNewTeam(teamName);
        team.addEntry(entry);
        team.setPrefix(text);
        obj.getScore(entry).setScore(score);
    }
}
