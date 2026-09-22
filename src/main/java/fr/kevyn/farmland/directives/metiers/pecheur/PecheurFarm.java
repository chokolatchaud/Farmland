package fr.kevyn.farmland.directives.metiers.pecheur;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;

public class PecheurFarm {
	

    public static ItemStack create() {
    	ItemStack item = Outils.create(Material.FISHING_ROD, "§aCanne à Péche");
        return item;
    }

}
