package fr.kevyn.farmland.menufarm;

import org.bukkit.inventory.Inventory;

import fr.kevyn.farmland.menu.TypeMenu;
import fr.kevyn.farmland.playerserver.PlayerServer;

public class AllMenuMetier {
	
	
	public static Inventory createmenuAnimal(PlayerServer playerserver) {
        Inventory inv = CreateMenuMetier.createmenuMetier("Agriculteur", 9, TypeMenu.PASSIF_MOB, ListFarmItem.listAgriculteuritem(), playerserver);
        return inv;
        }
	
	public static Inventory createmenuBag(PlayerServer playerserver) {
		Inventory inv = CreateMenuMetier.createmenuMetier("Sac", 54, TypeMenu.BAG, ListFarmItem.getlistfarmitem(), playerserver);
		return inv;
        }
	public static Inventory createmenuSeeds(PlayerServer playerserver) {
		Inventory inv = CreateMenuMetier.createmenuMetier("Graine", 9, TypeMenu.SEEDS, ListFarmItem.getlistSeeditem(),playerserver);
		return inv;
		
        
        }
	public static Inventory createmenuShop(PlayerServer playerserver) {
		Inventory inv = CreateMenuMetier.createmenuMetier("Shop", 54, TypeMenu.SHOP, ListFarmItem.getlistshop(),playerserver);
		return inv;
		
        
        }
	public static Inventory createmenuMobs(PlayerServer playerserver) {
        Inventory inv = CreateMenuMetier.createmenuMetier("Tueur", 9, TypeMenu.AGGRESSIVE_MOB, ListFarmItem.listTueuritem(), playerserver);
        return inv;
        }
       

        

        


    }

        

        


    
    




