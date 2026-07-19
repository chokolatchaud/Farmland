package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.AcaciaBoat;

public class boatgame {
	
	public static boolean teleportplayertoboat(ConfigStartEndZone game,Player player) {
		
		for(Location startzone : game.allgetzonespawn(game)) {
			for(Location blocstatuszone : game.allgetzoneblocstatus(game)) {
				if(searchslotboat(blocstatuszone, startzone, game.getWorld(), player)) {
					return true;
				}
				
			}
			
		}
		return false;
			

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
	
	

}
