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

	Location zonespawn1 = new Location(world, 83, 34, -36, 90f, 0f);
	Location zonespawn2 = new Location(world, 83, 34, -39, 90f, 0f);
	Location zonespawn3 = new Location(world, 83, 34, -42, 90f, 0f);
	Location zonespawn4 = new Location(world, 83, 34, -45, 90f, 0f);

	static final GameRegion Waypoint1 = new GameRegion(35,33,-5, 49,36,-3, 0,0,0,"Waypoint1",false,"world",TypeRegion.BoatraceWaypoint,null);
	static final GameRegion Waypoint2 = new GameRegion(78,33,26, 80,36,40, 0,0,0,"Waypoint2",false,"world",TypeRegion.BoatraceWaypoint,null);
	static final GameRegion Waypoint3 = new GameRegion(111,33,-8, 123,38,-6, 0,0,0,"Waypoint3",false,"world",TypeRegion.BoatraceWaypoint,null);


	static final GameRegion finishline = new GameRegion(86,33,-47, 88,38,-33, 0,0,0,"finishlineboat",false,"world",TypeRegion.Boatrace,null);


	HashMap<Integer, Player> playeringame = new HashMap<Integer, Player>();

	HashMap<Player, Integer> progression = new HashMap<Player, Integer>();

	int timetolaunch = 30;
	int timegame = 0;
	int racestarttime = 0; 
	StatutBoatGame status = StatutBoatGame.waitplayer;

	org.bukkit.scheduler.BukkitTask mainTask;
	org.bukkit.scheduler.BukkitTask raceDetectionTask;

	public ConfigStartEndZone(JavaPlugin plugin) {
		BoatGameHashMap.addListgameboat(this);
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

	public boolean isPisteLibre(int piste) {
		return !playeringame.containsKey(piste);
	}

	public void killgame(JavaPlugin plugin, ConfigStartEndZone game) {
		plugin.getLogger().info("[BoatRace][DEBUG] Fin de partie, nettoyage");

		if (game.mainTask != null) game.mainTask.cancel();
		if (game.raceDetectionTask != null) game.raceDetectionTask.cancel();
		BoatGameHashMap.removeListgameboat(game);
	}

	public void addplayeringame(ConfigStartEndZone game, Player player, int piste) {
		game.getPlayeringame().put(piste, player);
		game.getProgression().put(player, 0); 
		System.out.println("[BoatRace][DEBUG] " + player.getName() + " rejoint la piste " + piste + " (en memoire, aucun bloc modifie)");
	}


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


	public void removeplayeringame(ConfigStartEndZone game, int piste) {
		Player removed = game.getPlayeringame().remove(piste);
		if (removed != null) {
			game.getProgression().remove(removed);
			System.out.println("[BoatRace][DEBUG] piste " + piste + " liberee en memoire (" + removed.getName() + ")");
		}
	}

	public ArrayList<GameRegion> allgetWaypoint(ConfigStartEndZone waypoint) {
		ArrayList<GameRegion> allwaypoint = new ArrayList<GameRegion>();
		allwaypoint.add(Waypoint1);
		allwaypoint.add(Waypoint2);
		allwaypoint.add(Waypoint3);
		return allwaypoint;
	}

	public static int getWaypointIndex(GameRegion waypoint) {
		switch (waypoint.getName()) {
			case "Waypoint1": return 1;
			case "Waypoint2": return 2;
			case "Waypoint3": return 3;
			default: return -1;
		}
	}

	public static void starttime(JavaPlugin plugin, ConfigStartEndZone game) {
		// boucle "minuteur" : 1x/seconde, gere le decompte, le depart et la sortie de bateau
		game.mainTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
					game.racestarttime = game.getTimegame(); // depart reel, exclut les secondes du decompte
				}
			}
		}, 20L, 20L);

		// boucle "detection" : 5x/seconde, dediee aux waypoints/arrivee.
		// Un bateau peut traverser une petite zone en moins d'1 seconde : verifier
		// seulement 1x/s (comme avant) pouvait rater le passage. 5x/s reduit fortement ce risque.
		game.raceDetectionTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (game.getStatus() != StatutBoatGame.race) return;

			HashMap<Integer, Player> playeringame = game.getPlayeringame();
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

					if (deja >= 3) {
						int tempsCourse = game.getTimegame() - game.racestarttime;
						boolean record = BoatTimeSave.recordTime(plugin, player.getName(), tempsCourse);

						player.sendMessage("§6§lVICTOIRE ! §fTemps : §b" + tempsCourse + "s");
						if (record) {
							player.sendMessage("§d✦ Nouveau record personnel !");
						}
						plugin.getLogger().info("[BoatRace][DEBUG] " + player.getName() + " GAGNE la course en " + tempsCourse + "s (piste " + piste + ")");
						Bukkit.broadcastMessage("§6" + player.getName() + " §ea remporté la course de bateaux en §b" + tempsCourse + "s§e !");

						BoatRaceHologram.update(plugin);

						// pousse le classement complet vers le site (si le module WebAPI est actif)
						if (plugin instanceof fr.kevyn.farmland.FarmlandMain) {
							fr.kevyn.farmland.FarmlandMain main = (fr.kevyn.farmland.FarmlandMain) plugin;
							if (main.getWebApi() != null) {
								main.getWebApi().pushBoatTimes(BoatTimeSave.getTop(plugin, 10));
								plugin.getLogger().info("[BoatRace][DEBUG] Classement pousse vers le site");
							} else {
								plugin.getLogger().warning("[BoatRace][DEBUG] WebApi non initialise, classement PAS pousse vers le site");
							}
						}

						vainqueurs.add(player);
					} else {
						// evite le spam : uniquement si le joueur n'est pas dans cette zone depuis le dernier check
						player.sendMessage("§cTu dois passer tous les points de contrôle avant l'arrivée ! (" + deja + "/3)");
					}
				}
			}

			for (Player winner : vainqueurs) {
				if (winner.getVehicle() != null) {
					org.bukkit.entity.Entity boat = winner.getVehicle();
					winner.leaveVehicle();
					boat.remove();
				}
				game.removeplayeringame(game, winner);
				winner.teleport(game.getZonespawn1());
			}
		}, 20L, 4L);
	}
}
