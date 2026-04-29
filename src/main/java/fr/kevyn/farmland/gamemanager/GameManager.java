package fr.kevyn.farmland.gamemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.gamemaps.GameMapsCreator;
import fr.kevyn.farmland.gamemaps.Gamemaps;




public class GameManager {
	JavaPlugin plugin;
	ArrayList<Player> playerwait = new ArrayList<>();
	private static final GameManager INSTANCE = new GameManager();
	
	public void init(JavaPlugin plugin) {
	    this.plugin = plugin;
	}
	
	
	private Map<String, Game> games = new HashMap<>();
	
	
	public static GameManager getInstance() {
        return INSTANCE;
    }
	
	
	public void addplayerwait(Player player) {
		playerwait.add(player);
		List<Gamemaps> allmaps = GameMapsCreator.getallmaps();
		
		for(Game game : games.values()) {
			if(game.getStatue() == Statuegame.WAITPLAYER) {
				System.out.println("Game existante trouvée !");
				game.addplayerwait(player);
				
				return;
			}
		}
		for(Gamemaps gamemaps : allmaps) {
			if(!games.containsKey(gamemaps.getName())) {
				System.out.println("Nouvelle game créée sur: " + gamemaps.getName());
				Game newGame  = new Game(gamemaps, plugin);
				games.put(gamemaps.getName(), newGame);
				newGame.addplayerwait(player);
				newGame.startTask();
				return;

			}
		
			
			
		}
		player.sendMessage("desolé tout les maps sont prise");
		
	}
	
	public Game getGameByPlayer(Player player) {
	    for (Game game : games.values()) {
	        if (game.getplayer().contains(player)) {
	            return game;
	        }
	    }
	    return null;
	}
	
  public void removeplayerwait(Player player) {
		  Game game = getGameByPlayer(player);
		  if(game == null) {
			  player.sendMessage("tu nest pas dans une game");
		  }
		  if(game.getStatue() == Statuegame.WAITPLAYER) {
			  game.removeplayerwait(player);
			  Location location = new Location(Bukkit.getWorld("world"), 4, 85, 5);// hardcode ----------------
			  player.teleport(location);
			  player.sendMessage("tu tes bien desinscrit");
			  
		  }else {
			  player.sendMessage("la game a deja commencée");
		  }
		  
				  
			  
			  
		  }


  public void removeGame(Game game) {
	  games.values().remove(game);
	// TODO Auto-generated method stub
	
  }
	  
	  
	 
	  
	  
  }
	
	
	

	
	
	

