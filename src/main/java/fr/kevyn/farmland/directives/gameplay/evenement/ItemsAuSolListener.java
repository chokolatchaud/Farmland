package fr.kevyn.farmland.directives.gameplay.evenement;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Serveur créatif à métiers : les objets n'ont pas de valeur (inventaire créatif),
 * seuls les jetons du /bag comptent. Aucune action de jeu ne doit faire tomber d'objet.
 *
 * - toute apparition d'objet au sol est annulée (blocs, cultures, mobs, contenu
 *   d'un coffre cassé, explosions, distributeurs...), SAUF les objets jetés par un
 *   joueur (touche Q, glisser hors de l'inventaire) : RoseStacker les regroupe au sol ;
 * - à la mort d'un joueur, rien ne tombe et il garde son inventaire ;
 * - les orbes d'XP n'apparaissent jamais.
 *
 * Les objets donnés par le plugin passent par ObjetsJoueur.donner (inventaire,
 * puis coffre de l'Ender) pour ne pas être perdus.
 */
public final class ItemsAuSolListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!jeteParUnJoueur(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /** Un objet jeté par un joueur garde le joueur comme lanceur (thrower). */
    static boolean jeteParUnJoueur(Item item) {
        return item.getThrower() != null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onOrbeXp(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.EXPERIENCE_ORB) {
            event.setCancelled(true);
        }
    }
}
