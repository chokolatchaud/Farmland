package fr.kevyn.farmland.boathub;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

/**
 * Detecte quand un joueur quitte manuellement son bateau de course
 * (touche shift) et le retire proprement de la partie en cours.
 * Fige aussi le bateau tant que le statut n'est pas "race" : setMaxSpeed(0)
 * seul ne suffit pas (les vagues/courant font quand meme deriver le bateau),
 * donc on reteleporte a la position precedente a chaque mouvement detecte.
 */
public class BoatRaceListener implements Listener {

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Boat)) return;

        for (ConfigStartEndZone game : BoatGameHashMap.getListgameboat()) {
            if (game.getStatus() != StatutBoatGame.waitplayer) continue;

            for (org.bukkit.entity.Entity passenger : event.getVehicle().getPassengers()) {
                if (passenger instanceof Player player && game.getPlayeringame().containsValue(player)) {
                    // on ne corrige que la VRAIE derive (courant/vagues), pas les micro-mouvements
                    // naturels de l'eau : sinon on re-teleporte a chaque tick meme pour un
                    // deplacement infime, ce qui donne un effet de "TP en continu" tres visible
                    double distance = event.getFrom().distanceSquared(event.getTo());
                    if (distance > 0.02) { // ~0.14 bloc de tolerance avant correction
                        event.getVehicle().teleport(event.getFrom());
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) return;
        if (!(event.getVehicle() instanceof Boat)) return;

        Player player = (Player) event.getExited();

        for (ConfigStartEndZone game : BoatGameHashMap.getListgameboat()) {
            if (game.getProgression().containsKey(player)) {
                boatgame.playerleftboat(player, event.getVehicle(), game);
                break;
            }
        }
    }
}
