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

	// yaw 90 = ouest (vers X negatif), face au circuit qui part vers les waypoints
	Location zonespawn1 = new Location(world, 83, 34, -36, 90f, 0f);
	Location zonespawn2 = new Location(world, 83, 34, -39, 90f, 0f);
	Location zonespawn3 = new Location(world, 83, 34, -42, 90f, 0f);
	Location zonespawn4 = new Location(world, 83, 34, -45, 90f, 0f);

	// STATIC : crees et enregistres UNE SEULE FOIS pour tout le plugin.
	// Avant, ces GameRegion etaient des champs d'instance : chaque nouvelle course
	// (chaque nouveau ConfigStartEndZone) en recreait 4 nouveaux et les empilait
	// pour toujours dans GameRegionHashMap (jamais nettoyes), qui grossissait
	// sans fin et ralentissait TOUTES les verifications de zone du serveur.
	static final GameRegion Waypoint1 = new GameRegion(45,33,-9, 55,40,1, 0,0,0,"Waypoint1",false,"world",TypeRegion.BoatraceWaypoint,null);
	static final GameRegion Waypoint2 = new GameRegion(74,33,20, 84,40,30, 0,0,0,"Waypoint2",false,"world",TypeRegion.BoatraceWaypoint,null);
	static final GameRegion Waypoint3 = new GameRegion(103,33,-11, 113,40,-1, 0,0,0,"Waypoint3",false,"world",TypeRegion.BoatraceWaypoint,null);

	// eloignee des spawns (x=83) pour ne plus les chevaucher : avant maxX=83
	// faisait que les joueurs etaient DEJA dans la zone d'arrivee a leur spawn,
	// d'ou le message "passe tous les waypoints" recu des le depart.
	static final GameRegion finishline = new GameRegion(70,33,-49, 76,39,-33, 0,0,0,"finishlineboat",false,"world",TypeRegion.Boatrace,null);

	// piste (1 a 4) -> joueur present sur cette piste.
	// C'EST la seule source de verite pour savoir si une piste est libre :
	// plus aucun bloc dans le monde ne stocke cette info (fini GOLD/DIAMOND).
	HashMap<Integer, Player> playeringame = new HashMap<Integer, Player>();

	// joueur -> nombre de waypoints valides dans l'ordre (0 = aucun, 3 = tous)
	HashMap<Player, Integer> progression = new HashMap<Player, Integer>();

	int timetolaunch = 30;
	int timegame = 0;
	int racestarttime = 0; // valeur de timegame au moment ou la course demarre reellement (exclut le decompte)
	StatutBoatGame status = StatutBoatGame.waitplayer;

	// references des taches planifiees, pour pouvoir les annuler UNE PAR UNE
	// (jamais Bukkit.getScheduler().cancelTasks(plugin) qui annulerait TOUT le plugin : scoreboard, tab, market...)
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

	/** Est-ce que cette piste est libre ? (uniquement en memoire, aucun bloc lu) */
	public boolean isPisteLibre(int piste) {
		return !playeringame.containsKey(piste);
	}

	public void killgame(JavaPlugin plugin, ConfigStartEndZone game) {
		plugin.getLogger().info("[BoatRace][DEBUG] Fin de partie, nettoyage");
		// on annule UNIQUEMENT les taches de cette partie, jamais cancelTasks(plugin)
		// qui annulerait TOUT le plugin (scoreboard, tab, market, autosave...)
		if (game.mainTask != null) game.mainTask.cancel();
		if (game.raceDetectionTask != null) game.raceDetectionTask.cancel();
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
		allwaypoint.add(Waypoint1);
		allwaypoint.add(Waypoint2);
		allwaypoint.add(Waypoint3);
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
