package fr.kevyn.farmland.boathub;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;


public class BoatRaceListener implements Listener {

    private final org.bukkit.plugin.java.JavaPlugin plugin;

    public BoatRaceListener(org.bukkit.plugin.java.JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) return;
        if (!(event.getVehicle() instanceof Boat)) return;

        Player player = (Player) event.getExited();

        for (ConfigStartEndZone game : BoatGameHashMap.getListgameboat()) {
            if (game.getProgression().containsKey(player)) {
                boatgame.playerleftboat(player, event.getVehicle(), game, plugin);
                break;
            }
        }
    }
}
