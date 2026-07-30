package fr.kevyn.farmland.afk;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Met a jour l'activite du joueur (utilise par AfkManager pour decider
 * s'il doit etre traite comme hors ligne pour les revenus de structures).
 */
public class AfkListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AfkManager.updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        AfkManager.clear(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // ne compte que le VRAI mouvement (X/Z), pas juste tourner la camera
        // (evite qu'un joueur qui regarde juste autour de lui soit compte actif)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        AfkManager.updateActivity(event.getPlayer());
    }
}
