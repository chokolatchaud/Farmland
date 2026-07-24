package fr.kevyn.farmland.boathub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.AcaciaBoat;
import org.bukkit.plugin.java.JavaPlugin;

public class boatgame {

	/** Cherche une piste libre (en memoire, aucun bloc lu) et y place le joueur */
	public static boolean teleportplayertoboat(ConfigStartEndZone game, Player player, JavaPlugin plugin) {
		for (int piste = 1; piste <= 4; piste++) {
			if (game.isPisteLibre(piste)) {
				Location zonespawn = game.getZonespawnByPiste(piste);
				spawnBoatForPlayer(zonespawn, game.getWorld(), player, plugin);
				game.addplayeringame(game, player, piste);
				System.out.println("[BoatRace][DEBUG] " + player.getName() + " place sur la piste " + piste);
				return true;
			}
		}
		System.out.println("[BoatRace][DEBUG] Aucune place libre pour " + player.getName());
		return false;
	}

	private static void spawnBoatForPlayer(Location zonespawn, World world, Player player, JavaPlugin plugin) {
		AcaciaBoat bateau = world.spawn(zonespawn, AcaciaBoat.class);
		bateau.setInvulnerable(true);
		bateau.setMaxSpeed(0); // immobile tant que le statut n'est pas "race" (remis a la valeur normale au depart)
		player.teleport(zonespawn);

		// petit delai avant de monter le joueur : laisse le temps a la teleportation
		// de se synchroniser cote client, sinon le joueur peut apparaitre "a cote"
		// du bateau au lieu d'assis dedans pendant un court instant
		Bukkit.getScheduler().runTaskLater(plugin, () -> bateau.addPassenger(player), 2L);

		System.out.println("[BoatRace][DEBUG] Bateau spawn pour " + player.getName() + " a " + zonespawn);
	}

	public static void playerleftboat(Player player, Entity boat, ConfigStartEndZone game) {
		// on ne connait pas forcement la piste ici : on cherche par joueur
		game.removeplayeringame(game, player);
		boat.remove();
		player.teleport(game.getZonespawn1());
		player.sendMessage("§7Vous avez quitté la partie");
		System.out.println("[BoatRace][DEBUG] " + player.getName() + " a quitte le bateau (sortie manuelle)");
	}
}
