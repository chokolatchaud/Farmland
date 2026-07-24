package fr.kevyn.farmland.boathub;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 * Detecte quand un joueur quitte manuellement son bateau de course
 * (touche shift) et le retire proprement de la partie en cours.
 *
 * Le figeage du bateau pendant le decompte (waitplayer) ne se fait PLUS ici
 * via VehicleMoveEvent + teleport() : cette approche "corrige apres coup"
 * envoyait un paquet de correction de position dur a chaque micro-mouvement,
 * ce qui donnait un effet de rebond/glitch tres visible sur les clients plus
 * anciens passant par ViaBackwards (le paquet de teleport se traduit mal).
 * Voir ConfigStartEndZone.starttime() : le gel se fait maintenant en
 * PREVENTIF, a chaque tick, via setVelocity(0,0,0) - plus doux a traduire.
 */
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
