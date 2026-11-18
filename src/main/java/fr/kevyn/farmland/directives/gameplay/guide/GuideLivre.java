package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.gameplay.objet.ObjetsJoueur;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Le livre « Guide FarmLand » (tout de A à Z), reconnu par un tag : le joueur
 * n'en garde qu'un seul dans son inventaire.
 */
public final class GuideLivre {

    private static final NamespacedKey CLE = new NamespacedKey("farmland", "guide_livre");

    private GuideLivre() {
    }

    public static ItemStack creer() {
        ItemStack livre = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) livre.getItemMeta();
        meta.setTitle(GuideTextes.TITRE_LIVRE);
        meta.setAuthor(GuideTextes.AUTEUR_LIVRE);
        List<Component> pages = new ArrayList<>();
        for (String page : GuideTextes.PAGES_LIVRE) {
            pages.add(LegacyComponentSerializer.legacySection().deserialize(page));
        }
        meta.pages(pages);
        meta.getPersistentDataContainer().set(CLE, PersistentDataType.BYTE, (byte) 1);
        livre.setItemMeta(meta);
        return livre;
    }

    public static boolean estLeGuide(ItemStack item) {
        return item != null && item.getType() == Material.WRITTEN_BOOK && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(CLE, PersistentDataType.BYTE);
    }

    public static boolean possede(Player joueur) {
        for (ItemStack item : joueur.getInventory().getContents()) {
            if (estLeGuide(item)) return true;
        }
        return false;
    }

    /** Donne le livre s'il ne l'a pas déjà ; vrai si un livre a été donné. */
    public static boolean donnerSiAbsent(Player joueur) {
        if (possede(joueur)) return false;
        return ObjetsJoueur.donner(joueur, creer());
    }
}
