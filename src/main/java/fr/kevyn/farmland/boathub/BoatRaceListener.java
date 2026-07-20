package fr.kevyn.farmland.boathub;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 * Detecte quand un joueur quitte manuellement son bateau de course
 * (touche shift) et le retire proprement de la partie en cours.
 */
public class BoatRaceListener implements Listener {

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
