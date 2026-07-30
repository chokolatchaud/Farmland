package fr.kevyn.farmland.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kevyn.farmland.cosmetics.CosmeticShop;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class MenuCosmetics {

    public static Inventory createmenu(PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 9, "§6Boutique cosmétiques");
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
