package fr.kevyn.farmland.boathub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.AcaciaBoat;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.game.HubCommand;

public class boatgame {

	/** Cherche une piste libre (en memoire, aucun bloc lu) et y place le joueur */
	public static boolean teleportplayertoboat(ConfigStartEndZone game, Player player, JavaPlugin plugin) {
		for (int piste = 1; piste <= 4; piste++) {
			if (game.isPisteLibre(piste)) {
				Location zonespawn = game.getZonespawnByPiste(piste);
				spawnBoatForPlayer(zonespawn, game.getWorld(), player, plugin);
				game.addplayeringame(game, player, piste);
				return true;
			}
		}
		return false;
	}

	private static void spawnBoatForPlayer(Location zonespawn, World world, Player player, JavaPlugin plugin) {
		AcaciaBoat bateau = world.spawn(zonespawn, AcaciaBoat.class);
		bateau.setInvulnerable(true); 
		player.teleport(zonespawn);

		// petit delai avant de monter le joueur : laisse le temps a la teleportation
		Bukkit.getScheduler().runTaskLater(plugin, () -> bateau.addPassenger(player), 2L);

		System.out.println("[BoatRace][DEBUG] Bateau spawn pour " + player.getName() + " a " + zonespawn);
	}

	public static void playerleftboat(Player player, Entity boat, ConfigStartEndZone game, JavaPlugin plugin) {
		game.removeplayeringame(game, player);
		boat.remove();
		teleportToHubOrDock(player, game, plugin);
		player.sendMessage("§7Vous avez quitté la partie");
	}

	/** Renvoie au hub si configure, sinon repli sur le ponton de depart (jamais laisser le joueur bloque) */
	private static void teleportToHubOrDock(Player player, ConfigStartEndZone game, JavaPlugin plugin) {
		Location hub = null;
		if (plugin instanceof FarmlandMain main) {
			hub = HubCommand.getHubLocation(main);
		}
		player.teleport(hub != null ? hub : game.getZonespawn1());
	}
}
