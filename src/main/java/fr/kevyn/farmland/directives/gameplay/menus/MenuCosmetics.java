package fr.kevyn.farmland.directives.gameplay.menus;

import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.directives.cosmetic.CosmeticShop;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;

public class MenuCosmetics {

    public static Inventory createmenu(PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 18, fr.kevyn.farmland.directives.infrastructure.Glyphes.titreAvecFond(fr.kevyn.farmland.directives.infrastructure.Glyphes.FOND_COSMETIQUES, "Boutique cosmétiques")); // 2 lignes : plus de 9 cosmétiques
        GameMenu menu = new GameMenu(inv, TypeMenu.COSMETICS);

        int slot = 0;
        for (CosmeticShop.Cosmetic cosmetic : CosmeticShop.COSMETICS) {
            ItemStack item = cosmetic.createItem();
            ItemMeta meta = item.getItemMeta();

            boolean owned = playerserver.getCosmeticsOwned().contains(cosmetic.id);
            meta.setLore(java.util.List.of(
                    owned ? "§a✔ Possédé — clique pour équiper" : "§7Prix : §e" + cosmetic.price + " $FB"
            ));
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot++;
        }

        return inv;
    }
}
