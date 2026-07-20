package fr.kevyn.farmland.boathub;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.AcaciaBoat;

public class boatgame {

	public static boolean teleportplayertoboat(ConfigStartEndZone game, Player player) {
		ArrayList<Location> spawns = game.allgetzonespawn(game);
		ArrayList<Location> status = game.allgetzoneblocstatus(game);

		for (int i = 0; i < spawns.size(); i++) {
			if (searchslotboat(status.get(i), spawns.get(i), game.getWorld(), player)) {
				int piste = returnplaceplayer(spawns.get(i).blockZ());
				game.addplayeringame(game, player, piste);
				System.out.println("[BoatRace][DEBUG] " + player.getName() + " place sur la piste " + piste);
				return true;
			}
		}
		System.out.println("[BoatRace][DEBUG] Aucune place libre pour " + player.getName());
		return false;
	}

	public static boolean searchslotboat(Location Blocstatuszonespawn, Location zonespawn, World world, Player player) {
		if (Blocstatuszonespawn.getBlock().getType() == Material.GOLD_BLOCK) {
			AcaciaBoat bateau = world.spawn(zonespawn, AcaciaBoat.class);
			bateau.setInvulnerable(true);
			Blocstatuszonespawn.getBlock().setType(Material.DIAMOND_BLOCK);
			player.teleport(zonespawn);
			bateau.addPassenger(player);
			System.out.println("[BoatRace][DEBUG] Bateau spawn pour " + player.getName() + " a " + zonespawn);
			return true;
		}
		return false;
	}

	public static void playerleftboat(Player player, Entity boat, ConfigStartEndZone game) {
		// on ne connait pas forcement la piste ici : on cherche par joueur
		game.removeplayeringame(game, player);
		boat.remove();
		player.teleport(game.getZonespawn1());
		player.sendMessage("§7Vous avez quitté la partie");
		System.out.println("[BoatRace][DEBUG] " + player.getName() + " a quitte le bateau (sortie manuelle)");
	}

	public static int returnplaceplayer(int blockz) {
		if (blockz == -36) {
			return 1;
		} else if (blockz == -39) {
			return 2;
		} else if (blockz == -42) {
			return 3;
		} else if (blockz == -45) {
			return 4;
		}
		return 0;
	}
}
