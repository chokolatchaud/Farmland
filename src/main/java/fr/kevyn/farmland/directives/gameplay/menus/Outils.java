package fr.kevyn.farmland.directives.gameplay.menus;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Outils {


		    public static ItemStack create(Material material,String name) {
		        ItemStack item = new ItemStack(material);
		        ItemMeta meta = item.getItemMeta();
		        meta.setDisplayName(name);
		        meta.getPersistentDataContainer().set(CleOutils(item), PersistentDataType.BYTE, (byte) 1);
		        item.setItemMeta(meta);
		        appliquerModele(item);
		        return item;
		        
		    }

		    public static boolean isOutils(ItemStack item) {
		        if (item == null || !item.hasItemMeta()) return false;
		        NamespacedKey cle = cleOutilsOuNull(item);
		        if (cle == null) return false; // pas un outil de métier (arc, pioche en fer...) : réponse "non", sans erreur
		        return item.getItemMeta().getPersistentDataContainer().has(cle, PersistentDataType.BYTE);
		    }


		    /** Vrai si le joueur a deja un outil de metier de ce type dans son inventaire (main secondaire comprise). */
		    public static boolean possedeOutil(org.bukkit.entity.Player joueur, Material materielAttendu) {
		        for (ItemStack item : joueur.getInventory().getContents()) {
		            if (isOutilsAttendu(item, materielAttendu)) return true;
		        }
		        return false;
		    }

		    public static boolean isOutilsAttendu(ItemStack item, Material materielAttendu) {
		        if (item == null) return false;
		        // Le type d'abord : un objet d'un autre type est écarté tout de suite
		        return item.getType() == materielAttendu && isOutils(item);
		    }

		    // Clé de l'outil de métier pour ce matériau, ou null si ce n'est pas un outil de métier
		    private static NamespacedKey cleOutilsOuNull(ItemStack item) {
		        return switch (item.getType()) {
		            case NETHERITE_HOE, NETHERITE_SWORD, NETHERITE_PICKAXE, NETHERITE_AXE, FISHING_ROD -> CleOutils(item);
		            default -> null;
		        };
		    }
		    
		    
		    public static NamespacedKey CleOutils(ItemStack item) {
		        Material material = item.getType();
		        
		        switch (material) {
				case NETHERITE_HOE: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "houe_farmeur");
					return CLE_TAG;
				}
				case NETHERITE_SWORD: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "epee_tueur");
					return CLE_TAG;
					
				}
				case NETHERITE_PICKAXE: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "pioche_mineur");
					return CLE_TAG;
					
				}
				case NETHERITE_AXE: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "hache_agriculteur");
					return CLE_TAG;
				}
				case FISHING_ROD: {
					NamespacedKey CLE_TAG = new NamespacedKey("farmland", "canneapeche_pecheur");
					return CLE_TAG;
				}

				default:
					throw new IllegalArgumentException("Unexpected value: " + material);
				}
		    }

			private static final NamespacedKey CLE_RECOMPENSE_NIVEAU =
					new NamespacedKey("farmland", "recompense_niveau");

			public static void setRewardLevel(ItemStack item, int level) {

				if (item == null || !item.hasItemMeta()) {
					return;
				}

				ItemMeta meta = item.getItemMeta();

				meta.getPersistentDataContainer().set(
						CLE_RECOMPENSE_NIVEAU,
						PersistentDataType.INTEGER,
						level
				);

				item.setItemMeta(meta);
				appliquerModele(item); // outil de Maître : apparence dorée
			}

			/**
			 * Apparence du resource pack pour un outil de métier : custom:outil_<métier>,
			 * ou custom:outil_<métier>_maitre pour l'outil de Maître (récompense niveau 100).
			 * Sans le pack, l'outil garde son apparence vanilla. Vrai si le modèle a changé.
			 */
			public static boolean appliquerModele(ItemStack item) {
				if (!isOutils(item)) return false;
				String modele = modeleAttendu(item.getType(), getRewardLevel(item) >= 100);
				if (modele == null) return false;
				ItemMeta meta = item.getItemMeta();
				NamespacedKey cle = NamespacedKey.fromString(modele);
				if (cle == null || cle.equals(meta.getItemModel())) return false;
				meta.setItemModel(cle);
				item.setItemMeta(meta);
				return true;
			}

			/** Nom du modèle custom d'un outil de métier, ou null si ce matériau n'en est pas un. */
			static String modeleAttendu(Material materiau, boolean maitre) {
				String metier = switch (materiau) {
					case NETHERITE_PICKAXE -> "mineur";
					case NETHERITE_HOE -> "farmeur";
					case FISHING_ROD -> "pecheur";
					case NETHERITE_AXE -> "agriculteur";
					case NETHERITE_SWORD -> "tueur";
					default -> null;
				};
				if (metier == null) return null;
				return "custom:outil_" + metier + (maitre ? "_maitre" : "");
			}

			public static int getRewardLevel(ItemStack item) {

				if (item == null || !item.hasItemMeta()) {
					return 0;
				}

				return item.getItemMeta()
						.getPersistentDataContainer()
						.getOrDefault(
								CLE_RECOMPENSE_NIVEAU,
								PersistentDataType.INTEGER,
								0
						);
			}






}
