package fr.kevyn.farmland.directives.metiers;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Donne leur apparence du resource pack aux outils de métier déjà possédés
 * (créés avant l'ajout des textures) : à la connexion pour tout l'inventaire,
 * puis quand le joueur prend un outil en main.
 */
public final class OutilsApparenceListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joueur = event.getPlayer();
        ItemStack[] contenu = joueur.getInventory().getContents();
        boolean change = false;
        for (ItemStack item : contenu) {
            if (item != null && Outils.appliquerModele(item)) change = true;
        }
        if (change) joueur.getInventory().setContents(contenu);
    }

    @EventHandler
    public void onPrendreEnMain(PlayerItemHeldEvent event) {
        Player joueur = event.getPlayer();
        ItemStack item = joueur.getInventory().getItem(event.getNewSlot());
        if (item != null && Outils.appliquerModele(item)) {
            joueur.getInventory().setItem(event.getNewSlot(), item);
        }
    }
}
