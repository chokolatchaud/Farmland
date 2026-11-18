package fr.kevyn.farmland.directives.gameplay.objet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

/**
 * Enum représentant tous les items custom du resource pack
 * 
 * Système: Minecraft 1.21.4+ (item_model)
 * Généré automatiquement par generate_resourcepack.py
 * Total: 10 item(s)
 */
public enum CustomItemType {
    
    ARROW_NEXT("custom:arrow_next", "Arrow Next", Material.PAPER),
    ARROW_PREV("custom:arrow_prev", "Arrow Prev", Material.PAPER),
    CLOCK_DAYNIGHT("custom:clock_daynight", "Clock Daynight", Material.CLOCK),
    DOOR_PRIVACY("custom:door_privacy", "Door Privacy", Material.OAK_DOOR),
    RAIN_TOGGLE("custom:rain_toggle", "Rain Toggle", Material.LAPIS_BLOCK),
    TIME_FREEZE("custom:time_freeze", "Time Freeze", Material.CYAN_WOOL),
    UPGRADE_BOUGHT("custom:upgrade_bought", "Upgrade Bought", Material.LIME_STAINED_GLASS_PANE),
    UPGRADE_LOCKED("custom:upgrade_locked", "Upgrade Locked", Material.RED_STAINED_GLASS_PANE),
    WATERLAVASELECTION("custom:waterlavaselection", "Waterlavaselection", Material.WATER_BUCKET),
    MOB_SPAWN("custom:mob_spawn_toggle", "Mob Spawn", Material.SKELETON_SKULL), // nom de la texture dans le pack : mob_spawn_toggle.png
    SUNGLASS("custom:sunglass", "Sunglass", Material.CARVED_PUMPKIN),
    SUGGEST("", "Suggestion", Material.GREEN_WOOL),
    // Guide des joueurs (icônes provisoires du resource pack, textures_2d/guide*.png).
    // Le matériau de base s'affiche si le joueur n'a pas le pack.
    GUIDE("custom:guide", "Guide FarmLand", Material.BOOK),
    GUIDE_PLOT("custom:guide_plot", "Ton plot", Material.GRASS_BLOCK),
    GUIDE_METIERS("custom:guide_metiers", "Les métiers", Material.IRON_PICKAXE),
    GUIDE_VENDRE("custom:guide_vendre", "Vendre", Material.GOLD_NUGGET),
    GUIDE_DEPENSER("custom:guide_depenser", "Dépenser", Material.EMERALD),
    GUIDE_ACTIVITES("custom:guide_activites", "Activités", Material.OAK_BOAT),
    GUIDE_REGLES("custom:guide_regles", "Règles", Material.PAPER),
    // Jetons des métiers (pièce aux couleurs du métier) ; matériau de secours = ancienne icône.
    JETON_MINEUR("custom:jeton_mineur", "Jeton Mineur", Material.IRON_NUGGET),
    JETON_FARMEUR("custom:jeton_farmeur", "Jeton Farmeur", Material.WHEAT),
    JETON_PECHEUR("custom:jeton_pecheur", "Jeton Pêcheur", Material.PRISMARINE_SHARD),
    JETON_AGRICULTEUR("custom:jeton_agriculteur", "Jeton Agriculteur", Material.LEATHER),
    JETON_TUEUR("custom:jeton_tueur", "Jeton Tueur", Material.GUNPOWDER);


    private final String itemModel;
    private final String displayName;
    private final Material baseMaterial;
    
    CustomItemType(String itemModel, String displayName, Material baseMaterial) {
        this.itemModel = itemModel;
        this.displayName = displayName;
        this.baseMaterial = baseMaterial;
    }
    
    /**
     * Crée une ItemStack de cet item custom
     * @return ItemStack avec l'item_model approprié
     */
    public ItemStack create() {
        return create(1);
    }
    
    /**
     * Crée une ItemStack de cet item custom avec une quantité spécifique
     * @param amount Quantité d'items
     * @return ItemStack avec l'item_model approprié
     */
    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(baseMaterial, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + displayName);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            // Utiliser setItemModel pour Minecraft 1.21.4+
            // Note: Cette méthode peut varier selon votre version de l'API Spigot
            try {
                // Pour Paper/Spigot 1.21.4+
                meta.setItemModel(NamespacedKey.fromString(itemModel));
            } catch (Exception e) {
                // Fallback: stocker dans PersistentDataContainer
                NamespacedKey key = new NamespacedKey("your_plugin", "item_model");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemModel);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Identifie le type d'un ItemStack
     * @param item ItemStack à identifier
     * @return Le type custom ou null si ce n'est pas un item custom
     */
    public static CustomItemType fromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        ItemMeta meta = item.getItemMeta();
        
        // Essayer de récupérer l'item_model
        String itemModelStr = null;
        
        try {
            // Pour Paper/Spigot 1.21.4+
            NamespacedKey modelKey = meta.getItemModel();
            if (modelKey != null) {
                itemModelStr = modelKey.toString();
            }
        } catch (Exception e) {
            // Fallback: lire depuis PersistentDataContainer
            NamespacedKey key = new NamespacedKey("your_plugin", "item_model");
            itemModelStr = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        
        if (itemModelStr == null) return null;
        
        for (CustomItemType type : values()) {
            if (type.itemModel.equals(itemModelStr)) {
                return type;
            }
        }
        
        return null;
    }
    
    /**
     * Vérifie si un ItemStack est un item custom
     * @param item ItemStack à vérifier
     * @return true si c'est un item custom
     */
    public static boolean isCustomItem(ItemStack item) {
        return fromItem(item) != null;
    }
    
    // Getters
    
    public String getItemModel() {
        return itemModel;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Material getBaseMaterial() {
        return baseMaterial;
    }
}
