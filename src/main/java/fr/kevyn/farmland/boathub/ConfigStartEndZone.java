package fr.kevyn.farmland.boathub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;
import fr.kevyn.farmland.region.TypeRegion;

public class ConfigStartEndZone {
	World world = Bukkit.getWorld("world");

	Location zonespawn1 = new Location(world, 83, 34, -36);
	Location zonespawn2 = new Location(world, 83, 34, -39);
	Location zonespawn3 = new Location(world, 83, 34, -42);
	Location zonespawn4 = new Location(world, 83, 34, -45);

	GameRegion Waypoint1 = new GameRegion(45,33,-9, 55,40,1, 0,0,0,"Waypoint1",false,"world",TypeRegion.BoatraceWaypoint,null);
	GameRegion Waypoint2 = new GameRegion(74,33,20, 84,40,30, 0,0,0,"Waypoint2",false,"world",TypeRegion.BoatraceWaypoint,null);
	GameRegion Waypoint3 = new GameRegion(103,33,-11, 113,40,-1, 0,0,0,"Waypoint3",false,"world",TypeRegion.BoatraceWaypoint,null);

	GameRegion finishline = new GameRegion(80,33,-49, 80,39,-33, 0,0,0,"finishlineboat",false,"world",TypeRegion.Boatrace,null);

	// piste (1 a 4) -> joueur present sur cette piste.
	// C'EST la seule source de verite pour savoir si une piste est libre :
	// plus aucun bloc dans le monde ne stocke cette info (fini GOLD/DIAMOND).
	HashMap<Integer, Player> playeringame = new HashMap<Integer, Player>();

	// joueur -> nombre de waypoints valides dans l'ordre (0 = aucun, 3 = tous)
	HashMap<Player, Integer> progression = new HashMap<Player, Integer>();

	int timetolaunch = 30;
	int timegame = 0;
	StatutBoatGame status = StatutBoatGame.waitplayer;

	public ConfigStartEndZone(JavaPlugin plugin) {
		BoatGameHashMap.addListgameboat(this);
		finishline.setglass(plugin);
		starttime(plugin, this);
		plugin.getLogger().info("[BoatRace][DEBUG] Nouvelle partie creee");
	}

	public GameRegion getWaypoint1() { return Waypoint1; }
	public GameRegion getWaypoint2() { return Waypoint2; }
	public GameRegion getWaypoint3() { return Waypoint3; }
	public GameRegion getFinishline() { return finishline; }

	public Location getZonespawn1() { return zonespawn1; }
	public Location getZonespawn2() { return zonespawn2; }
	public Location getZonespawn3() { return zonespawn3; }
	public Location getZonespawn4() { return zonespawn4; }

	/** Emplacement de spawn du bateau pour une piste donnee (1 a 4) */
	public Location getZonespawnByPiste(int piste) {
		switch (piste) {
			case 1: return zonespawn1;
			case 2: return zonespawn2;
			case 3: return zonespawn3;
			case 4: return zonespawn4;
			default: return null;
		}
	}

	public World getWorld() { return world; }
	public int getTimetolaunch() { return timetolaunch; }
	public int getTimegame() { return timegame; }
	public void add1secondeTimegame() { this.timegame++; }

	public HashMap<Integer, Player> getPlayeringame() { return playeringame; }
	public HashMap<Player, Integer> getProgression() { return progression; }

	public StatutBoatGame getStatus() { return status; }
	public void setStatus(StatutBoatGame status) { this.status = status; }

	/** Est-ce que cette piste est libre ? (uniquement en memoire, aucun bloc lu) */
	public boolean isPisteLibre(int piste) {
		return !playeringame.containsKey(piste);
	}

	public void killgame(JavaPlugin plugin, ConfigStartEndZone game) {
		plugin.getLogger().info("[BoatRace][DEBUG] Fin de partie, nettoyage");
		Bukkit.getScheduler().cancelTasks(plugin);
		finishline.removeglass(plugin);
		BoatGameHashMap.removeListgameboat(game);
	}

	public void addplayeringame(ConfigStartEndZone game, Player player, int piste) {
		game.getPlayeringame().put(piste, player);
		game.getProgression().put(player, 0); // debute a 0 waypoint valide
		System.out.println("[BoatRace][DEBUG] " + player.getName() + " rejoint la piste " + piste + " (en memoire, aucun bloc modifie)");
	}

	/** Retire un joueur de la partie en cherchant sa piste (on ne la connait pas forcement a l'appel) */
	public void removeplayeringame(ConfigStartEndZone game, Player player) {
		HashMap<Integer, Player> map = game.getPlayeringame();
		Iterator<Map.Entry<Integer, Player>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Player> entry = it.next();
			if (entry.getValue().equals(player)) {
				System.out.println("[BoatRace][DEBUG] " + player.getName() + " retire de la piste " + entry.getKey() + " (piste liberee en memoire)");
				it.remove();
			}
		}
		game.getProgression().remove(player);
	}

	/** Retire un joueur directement par numero de piste (utilise quand on connait deja la piste) */
	public void removeplayeringame(ConfigStartEndZone game, int piste) {
		Player removed = game.getPlayeringame().remove(piste);
		if (removed != null) {
			game.getProgression().remove(removed);
			System.out.println("[BoatRace][DEBUG] piste " + piste + " liberee en memoire (" + removed.getName() + ")");
		}
	}

	public ArrayList<GameRegion> allgetWaypoint(ConfigStartEndZone waypoint) {
		ArrayList<GameRegion> allwaypoint = new ArrayList<GameRegion>();
		allwaypoint.add(waypoint.Waypoint1);
		allwaypoint.add(waypoint.Waypoint2);
		allwaypoint.add(waypoint.Waypoint3);
		return allwaypoint;
	}

	/** Waypoint1 -> 1, Waypoint2 -> 2, Waypoint3 -> 3, sinon -1 (pas un waypoint de course) */
	public static int getWaypointIndex(GameRegion waypoint) {
		switch (waypoint.getName()) {
			case "Waypoint1": return 1;
			case "Waypoint2": return 2;
			case "Waypoint3": return 3;
			default: return -1;
		}
	}

	public static void starttime(JavaPlugin plugin, ConfigStartEndZone game) {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			game.add1secondeTimegame();
			HashMap<Integer, Player> playeringame = game.getPlayeringame();

			if (playeringame.isEmpty()) {
				plugin.getLogger().info("[BoatRace][DEBUG] Plus aucun joueur, arret de la partie");
				game.killgame(plugin, game);
				return; // la partie n'existe plus, on stoppe ce tick
			}

			// retire les joueurs qui ont quitte leur bateau (via un iterator : safe pendant qu'on modifie)
			Iterator<Map.Entry<Integer, Player>> it = playeringame.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Player> entry = it.next();
				Player player = entry.getValue();
				if (!player.isInsideVehicle()) {
					System.out.println("[BoatRace][DEBUG] " + player.getName() + " n'est plus dans un bateau -> retire (piste " + entry.getKey() + ")");
					game.getProgression().remove(player);
					it.remove();
				}
			}

			if (game.getStatus() == StatutBoatGame.waitplayer) {
				game.timetolaunch -= 1;
				System.out.println("[BoatRace][DEBUG] Compte a rebours : " + game.timetolaunch);
				for (Player player : playeringame.values()) {
					player.sendMessage("§eDepart dans " + game.timetolaunch + " secondes");
					// le bateau est immobile depuis son spawn (setMaxSpeed(0)), rien a refaire ici a chaque tick
				}
				if (game.timetolaunch <= 0) {
					plugin.getLogger().info("[BoatRace][DEBUG] Depart de la course !");
					// on debloque les bateaux UNE SEULE FOIS
					for (Player player : playeringame.values()) {
						if (player.getVehicle() instanceof org.bukkit.entity.boat.AcaciaBoat boat) {
							boat.setMaxSpeed(0.4); // vitesse par defaut d'un bateau
						}
					}
					game.setStatus(StatutBoatGame.race);
				}
			}

			if (game.getStatus() == StatutBoatGame.race) {
				ArrayList<Player> vainqueurs = new ArrayList<>(); // traites APRES la boucle (pas de modif pendant l'iteration)

				for (Map.Entry<Integer, Player> entry : playeringame.entrySet()) {
					int piste = entry.getKey();
					Player player = entry.getValue();

					GameRegion region = GameRegionHashMap.getInstance().Playerwhatistregion(player);
					if (region == null) continue;

					// --- passage d'un waypoint, dans l'ordre uniquement ---
					if (region.gettype() == TypeRegion.BoatraceWaypoint) {
						int indexTouche = getWaypointIndex(region);
						int deja = game.getProgression().getOrDefault(player, 0);

						System.out.println("[BoatRace][DEBUG] " + player.getName() + " (piste " + piste + ") touche " + region.getName()
								+ " | deja valide=" + deja + " | attendu=" + (deja + 1));

						if (indexTouche == deja + 1) {
							game.getProgression().put(player, indexTouche);
							player.sendMessage("§aWaypoint " + indexTouche + "/3 validé !");
							plugin.getLogger().info("[BoatRace][DEBUG] " + player.getName() + " valide le waypoint " + indexTouche);
						}
						// sinon : deja valide, ou pas le bon (joueur a coupe le circuit) -> on ignore
					}

					// --- ligne d'arrivee : seulement si les 3 waypoints sont valides ---
					if (region.gettype() == TypeRegion.Boatrace) {
						int deja = game.getProgression().getOrDefault(player, 0);
						System.out.println("[BoatRace][DEBUG] " + player.getName() + " touche la ligne d'arrivee | waypoints valides=" + deja);

						if (deja >= 3) {
							player.sendMessage("§6§lVICTOIRE ! Tu as terminé la course !");
							plugin.getLogger().info("[BoatRace][DEBUG] " + player.getName() + " GAGNE la course (piste " + piste + ")");
							Bukkit.broadcastMessage("§6" + player.getName() + " §ea remporté la course de bateaux !");
							vainqueurs.add(player); // retire apres la boucle
						} else {
							player.sendMessage("§cTu dois passer tous les points de contrôle avant l'arrivée ! (" + deja + "/3)");
						}
					}
				}

				// on retire les vainqueurs maintenant : bateau supprime, slot libere (en memoire), teleporte au spawn1
				for (Player winner : vainqueurs) {
					if (winner.getVehicle() != null) {
						org.bukkit.entity.Entity boat = winner.getVehicle();
						winner.leaveVehicle();
						boat.remove();
					}
					game.removeplayeringame(game, winner);
					winner.teleport(game.getZonespawn1());
				}
			}
		}, 20L, 20L);
	}
}
