package fr.kevyn.farmland.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class boardManager {

	
	public static  Scoreboard create() {
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		if(manager == null) {
			return null;
		}
		Scoreboard scoreboard = manager.getNewScoreboard();
		if(scoreboard == null) {
			return null;
		}
		return scoreboard;

	}
}
