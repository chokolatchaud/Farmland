package fr.kevyn.farmland.directives.gameplay.objet;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Map;

/**
 * Donne des objets à un joueur sans jamais les faire tomber au sol
 * (les objets au sol sont bloqués : voir ItemsAuSolListener).
 * Ordre : inventaire, puis coffre de l'Ender. S'il ne reste vraiment aucune
 * place, le joueur est prévenu et l'objet perdu est écrit dans les logs.
 */
public final class ObjetsJoueur {

    private ObjetsJoueur() {
    }

    /** Vrai si tout a été placé (inventaire ou coffre de l'Ender). */
    public static boolean donner(Player joueur, ItemStack... objets) {
        Map<Integer, ItemStack> reste = joueur.getInventory().addItem(objets);
        if (reste.isEmpty()) {
            return true;
        }

        Collection<ItemStack> horsInventaire = reste.values();
        Map<Integer, ItemStack> resteEnder = joueur.getEnderChest().addItem(horsInventaire.toArray(new ItemStack[0]));
        if (resteEnder.isEmpty()) {
            joueur.sendMessage(MessageColor.YELLOW.apply("Inventaire plein : l'objet a été rangé dans ton coffre de l'Ender."));
            return true;
        }

        joueur.sendMessage(MessageColor.RED.apply("Inventaire et coffre de l'Ender pleins : l'objet n'a pas pu être donné. Préviens un admin."));
        for (ItemStack perdu : resteEnder.values()) {
            JavaPlugin.getProvidingPlugin(ObjetsJoueur.class).getLogger().warning(
                    "[Objets] Objet non donné à " + joueur.getName() + " (plus de place) : "
                            + perdu.getAmount() + "x " + perdu.getType());
        }
        return false;
    }
}
