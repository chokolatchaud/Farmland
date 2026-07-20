package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.AcaciaBoat;

public class boatgame {
	
	public static boolean teleportplayertoboat(ConfigStartEndZone game,Player player) {
		boolean teleport = false;
		ArrayList<Location> spawns = game.allgetzonespawn(game);
		ArrayList<Location> status = game.allgetzoneblocstatus(game);
		for (int i = 0; i < spawns.size(); i++) {
		    if (searchslotboat(status.get(i), spawns.get(i), game.getWorld(), player)) teleport = true;
		    if(teleport) {
				game.addplayeringame(game, player,returnplaceplayer(spawns.get(i).blockZ()));
			}
		}
		
		
		
		
		
		
		return teleport;
	}
	
	
	
	
	public static boolean searchslotboat(Location Blocstatuszonespawn, Location zonespawn,World world,Player player) {
		if(Blocstatuszonespawn.getBlock().getType() == Material.GOLD_BLOCK) {
			AcaciaBoat bateau = world.spawn(zonespawn, AcaciaBoat.class);	
			bateau.setInvulnerable(true);
			Blocstatuszonespawn.getBlock().setType(Material.DIAMOND_BLOCK);
			player.teleport(zonespawn);
			bateau.addPassenger(player);
			return true;

		}
		return false;
		
		
		
	}
	
  public static void playerleftboat(Player player,Entity boat,ConfigStartEndZone game) {
	  game.removeplayeringame(game, player);
	  boat.remove();
	  player.teleport(game.getZonespawn1());
	  player.sendMessage("vous avez quittez la partie");
 
  }
  
  public static int returnplaceplayer(int blockz) {
	  if(blockz == -36) {
		  return 1;
	  }else if(blockz == -39) {
		  return 2;
	  }else if(blockz == -42) {
		  return 3;
	  }else if (blockz == -45) {
		  return 4;
  
	  }
	  return 0;
		  
	  
	  
	  
  }
	
	

}
