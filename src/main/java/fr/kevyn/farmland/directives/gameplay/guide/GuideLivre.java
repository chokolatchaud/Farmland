package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.gameplay.objet.CustomItemType;
import fr.kevyn.farmland.directives.gameplay.objet.ObjetsJoueur;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * L'objet « Guide FarmLand » (icône custom:guide du resource pack, livre
 * vanilla sans le pack). Clic droit = ouvre le menu du guide.
 * Reconnu par un tag : le joueur n'en garde qu'un seul dans son inventaire.
 * (Avant : un livre écrit de 24 pages, remplacé par le menu.)
 */
public final class GuideLivre {

    private static final NamespacedKey CLE = new NamespacedKey("farmland", "guide_livre");

    private GuideLivre() {
    }

    public static ItemStack creer() {
        ItemStack guide = CustomItemType.GUIDE.create();
        ItemMeta meta = guide.getItemMeta();
        meta.setDisplayName("§6§l✦ Guide FarmLand ✦");
        meta.setLore(List.of(
                "§7Tout le serveur, avec tes chiffres :",
                "§7ton plot, tes métiers, tes jetons...",
                "",
                "§eClic droit pour l'ouvrir"
        ));
        meta.getPersistentDataContainer().set(CLE, PersistentDataType.BYTE, (byte) 1);
        guide.setItemMeta(meta);
        return guide;
    }

    public static boolean estLeGuide(ItemStack item) {
        return item != null && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(CLE, PersistentDataType.BYTE);
    }

    public static boolean possede(Player joueur) {
        for (ItemStack item : joueur.getInventory().getContents()) {
            if (estLeGuide(item)) return true;
        }
        return false;
    }

    /** Donne le guide s'il ne l'a pas déjà ; vrai si un guide a été donné. */
    public static boolean donnerSiAbsent(Player joueur) {
        if (possede(joueur)) return false;
        return ObjetsJoueur.donner(joueur, creer());
    }
}
