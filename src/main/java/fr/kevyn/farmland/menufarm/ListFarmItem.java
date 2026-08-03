package fr.kevyn.farmland.menufarm;

import java.util.ArrayList;

import org.bukkit.Material;

public class ListFarmItem {
	
	public static ArrayList<Material> getlistfarmitem() {
		ArrayList<Material> farmingitem = new ArrayList<Material>();
		farmingitem.add(Material.WHEAT);
		farmingitem.add(Material.CARROT);
		farmingitem.add(Material.POTATO);
		farmingitem.add(Material.PUMPKIN);
		
		farmingitem.add(Material.COAL);
		farmingitem.add(Material.IRON_INGOT);
		farmingitem.add(Material.GOLD_INGOT);
		farmingitem.add(Material.DIAMOND);
		
		farmingitem.add(Material.PORKCHOP);
		farmingitem.add(Material.BEEF);
		farmingitem.add(Material.CHICKEN);
		farmingitem.add(Material.MUTTON);
		
		farmingitem.add(Material.COD);
		farmingitem.add(Material.SALMON);
		farmingitem.add(Material.TROPICAL_FISH);
		farmingitem.add(Material.PUFFERFISH);
		
		farmingitem.add(Material.ROTTEN_FLESH);
		farmingitem.add(Material.BONE);
		farmingitem.add(Material.GUNPOWDER);
		farmingitem.add(Material.SHULKER_SHELL);
		return farmingitem;
	}
	
	public static String ReturnNameofMaterial(Material material) {
		switch (material) {
		case Material.WHEAT: {
			return "Blé";
		}case Material.CARROT:{
			return "Carotte";
			
		}case Material.POTATO:{
			return "Patate";
			
		}case Material.PUMPKIN:{
			return "Citrouille";
			
		}case Material.COAL:{
			return "Charbon";
			
		}case Material.IRON_INGOT:{
			return "Fer";
		}
		case Material.GOLD_INGOT:{
			return "Or";
		}
		case Material.DIAMOND:{
			return "Diamant";
		}
		case Material.PORKCHOP:{
			return "Cochon";
		}
		case Material.BEEF:{
			return "Vache";
		}
		case Material.CHICKEN:{
			return "Poulet";
		}
		case Material.MUTTON:{
			return "Mouton";
		}
		case Material.COD:{
			return "Morue";
		}
		case Material.SALMON:{
			return "Saumon";
		}
		case Material.TROPICAL_FISH:{
			return "Guppy";
		}
		case Material.PUFFERFISH:{
			return "Poisson-Globe";
		}
		case Material.ROTTEN_FLESH:{
			return "Chair périmé";
		}
		case Material.BONE:{
			return "Os";
		}
		case Material.GUNPOWDER:{
			return "Poudre à Canon";
		}
		case Material.SHULKER_SHELL:{
			return "Coquille";
			
		}
		
		public static Integer ReturnSlotofMaterial(Material material) {
			switch (material) {
			case Material.WHEAT: {
				return 9;
			}case Material.CARROT:{
				return 17;
				
			}case Material.POTATO:{
				return 25;
				
			}case Material.PUMPKIN:{
				return 33;
				
			}case Material.COAL:{
				return 11;
				
			}case Material.IRON_INGOT:{
				return 19;
			}
			case Material.GOLD_INGOT:{
				return 27;
			}
			case Material.DIAMOND:{
				return 35;
			}
			case Material.PORKCHOP:{
				return 13;
			}
			case Material.BEEF:{
				return 21;
			}
			case Material.CHICKEN:{
				return 29;
			}
			case Material.MUTTON:{
				return 37;
			}
			case Material.COD:{
				return 15;
			}
			case Material.SALMON:{
				return 23;
			}
			case Material.TROPICAL_FISH:{
				return 31;
			}
			case Material.PUFFERFISH:{
				return 39;
			}
			case Material.ROTTEN_FLESH:{
				return 17;
			}
			case Material.BONE:{
				return 25;
			}
			case Material.GUNPOWDER:{
				return 33;
			}
			case Material.SHULKER_SHELL:{
				return 41;
			}

		
		
		
			
		default:
			throw new IllegalArgumentException("Unexpected value: " + material);
		} 
		
	}

}
