package fr.kevyn.farmland.directives.metiers.farmeur;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Beacon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Fabrique et identifie le Beacon du Farmeur.
 * Les paramètres sont stockés dans le PDC de l'item puis copiés sur le bloc posé,
 * ce qui permet au beacon de conserver son identité après un redémarrage.
 */
public final class BeaconFarmeur {

    private static final NamespacedKey CLE_BEACON =
            new NamespacedKey("farmland", "beacon_farmeur");
    private static final NamespacedKey CLE_BONUS =
            new NamespacedKey("farmland", "beacon_farmeur_bonus");
    private static final NamespacedKey CLE_RAYON =
            new NamespacedKey("farmland", "beacon_farmeur_rayon");

    private static final double BONUS_PAR_DEFAUT = 0.20;
    private static final int RAYON_PAR_DEFAUT = 16;

    private BeaconFarmeur() {
    }

    public static ItemStack create(double bonus, int rayon) {
        double bonusValide = normaliserBonus(bonus);
        int rayonValide = Math.max(1, rayon);

        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();

        int pourcentage = (int) Math.round(bonusValide * 100.0);
        meta.setDisplayName("§a✦ Beacon du Farmeur");
        meta.setLore(List.of(
                "",
                "§7Récompense du métier Farmeur",
                "§eNiveau 150",
                "",
                "§a+" + pourcentage + "% de vitesse de croissance",
                "§7Rayon : §f" + rayonValide + " blocs",
                "§8Les bonus de plusieurs beacons ne se cumulent pas"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(CLE_BEACON, PersistentDataType.BYTE, (byte) 1);
        pdc.set(CLE_BONUS, PersistentDataType.DOUBLE, bonusValide);
        pdc.set(CLE_RAYON, PersistentDataType.INTEGER, rayonValide);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isBeaconFarmeur(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta()
                .getPersistentDataContainer()
                .has(CLE_BEACON, PersistentDataType.BYTE);
    }

    public static void marquerBeaconPose(Beacon beacon, ItemStack item) {
        if (!isBeaconFarmeur(item)) {
            return;
        }

        PersistentDataContainer itemPdc =
                item.getItemMeta().getPersistentDataContainer();

        double bonus = itemPdc.getOrDefault(
                CLE_BONUS,
                PersistentDataType.DOUBLE,
                BONUS_PAR_DEFAUT
        );

        int rayon = itemPdc.getOrDefault(
                CLE_RAYON,
                PersistentDataType.INTEGER,
                RAYON_PAR_DEFAUT
        );

        PersistentDataContainer beaconPdc = beacon.getPersistentDataContainer();
        beaconPdc.set(CLE_BEACON, PersistentDataType.BYTE, (byte) 1);
        beaconPdc.set(CLE_BONUS, PersistentDataType.DOUBLE, normaliserBonus(bonus));
        beaconPdc.set(CLE_RAYON, PersistentDataType.INTEGER, Math.max(1, rayon));

        beacon.update(true);
    }

    public static boolean isBeaconFarmeur(Beacon beacon) {
        return beacon.getPersistentDataContainer()
                .has(CLE_BEACON, PersistentDataType.BYTE);
    }

    public static double getBonus(Beacon beacon) {
        return normaliserBonus(
                beacon.getPersistentDataContainer().getOrDefault(
                        CLE_BONUS,
                        PersistentDataType.DOUBLE,
                        BONUS_PAR_DEFAUT
                )
        );
    }

    public static int getRayon(Beacon beacon) {
        return Math.max(
                1,
                beacon.getPersistentDataContainer().getOrDefault(
                        CLE_RAYON,
                        PersistentDataType.INTEGER,
                        RAYON_PAR_DEFAUT
                )
        );
    }

    public static ItemStack recreateDepuisBeacon(Beacon beacon) {
        return create(getBonus(beacon), getRayon(beacon));
    }

    private static double normaliserBonus(double bonus) {
        return Math.max(0.0, Math.min(1.0, bonus));
    }
}
