package fr.kevyn.farmland.directives.metiers.farmeur;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Remet la vitesse de casse du joueur à sa valeur normale à la connexion.
 * Annule la vitesse donnée par les montées de niveau Farmeur, qui est enregistrée
 * sur le joueur et s'appliquait à tous les blocs.
 */
public final class VitesseCasseListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AttributeInstance vitesse = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (vitesse == null) {
            return;
        }
        if (vitesse.getBaseValue() != vitesse.getDefaultValue()) {
            vitesse.setBaseValue(vitesse.getDefaultValue());
        }
    }
}
