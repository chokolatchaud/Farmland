package fr.kevyn.farmland.directives.metiers.farmeur;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;

public class HoueFarmeur {


    public static ItemStack create() {
    	ItemStack houe = Outils.create(Material.NETHERITE_HOE, "§aHoue du Farmeur");
    	return houe;
    }

}


