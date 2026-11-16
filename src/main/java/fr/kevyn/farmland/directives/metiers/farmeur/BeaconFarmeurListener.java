package fr.kevyn.farmland.directives.metiers.farmeur;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gère le Beacon du Farmeur :
 * - copie son tag persistant sur le bloc lorsqu'il est posé ;
 * - restitue l'item spécial lorsqu'il est cassé ;
 * - accélère de façon probabiliste la croissance naturelle des cultures proches.
 *
 * Un bonus de 0.20 signifie qu'à chaque croissance naturelle réussie,
 * la culture a 20 % de chances de gagner un stade supplémentaire.
 * Les beacons qui se chevauchent ne se cumulent pas : seul le meilleur bonus compte.
 */
public final class BeaconFarmeurListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<BeaconKey, BeaconData> beacons = new HashMap<>();

    public BeaconFarmeurListener(JavaPlugin plugin) {
        this.plugin = plugin;

        // Les chunks déjà chargés ne déclencheront pas forcément ChunkLoadEvent
        // après l'enregistrement du listener : on les indexe dès maintenant.
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                indexerChunk(chunk);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.BEACON
                || !BeaconFarmeur.isBeaconFarmeur(event.getItemInHand())) {
            return;
        }

        BlockState state = event.getBlockPlaced().getState();
        if (!(state instanceof Beacon beacon)) {
            return;
        }

        BeaconFarmeur.marquerBeaconPose(beacon, event.getItemInHand());
        enregistrer(beacon);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.BEACON) {
            return;
        }

        BlockState state = event.getBlock().getState();
        if (!(state instanceof Beacon beacon) || !BeaconFarmeur.isBeaconFarmeur(beacon)) {
            return;
        }

        ItemStack specialDrop = BeaconFarmeur.recreateDepuisBeacon(beacon);
        BeaconKey key = BeaconKey.from(event.getBlock());

        // Empêche le beacon vanilla de tomber. On restitue ensuite l'item spécial
        // uniquement si le break n'a pas été annulé par un autre listener.
        event.setDropItems(false);

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (event.isCancelled()) {
                return;
            }

            beacons.remove(key);

            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                event.getBlock().getWorld().dropItemNaturally(dropLocation, specialDrop);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onGrow(BlockGrowEvent event) {
        if (!estCulture(event.getBlock().getType())) {
            return;
        }

        double bonus = meilleurBonus(event.getBlock());
        if (bonus <= 0.0 || ThreadLocalRandom.current().nextDouble() >= bonus) {
            return;
        }

        if (!(event.getNewState().getBlockData() instanceof Ageable ageable)) {
            return;
        }

        if (ageable.getAge() >= ageable.getMaximumAge()) {
            return;
        }

        ageable.setAge(ageable.getAge() + 1);
        event.getNewState().setBlockData(ageable);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        indexerChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        UUID worldId = event.getWorld().getUID();
        int chunkX = event.getChunk().getX();
        int chunkZ = event.getChunk().getZ();

        Iterator<BeaconKey> iterator = beacons.keySet().iterator();
        while (iterator.hasNext()) {
            BeaconKey key = iterator.next();
            if (key.worldId().equals(worldId)
                    && key.chunkX() == chunkX
                    && key.chunkZ() == chunkZ) {
                iterator.remove();
            }
        }
    }

    private void indexerChunk(Chunk chunk) {
        for (BlockState state : chunk.getTileEntities(false)) {
            if (state instanceof Beacon beacon && BeaconFarmeur.isBeaconFarmeur(beacon)) {
                enregistrer(beacon);
            }
        }
    }

    private void enregistrer(Beacon beacon) {
        BeaconKey key = BeaconKey.from(beacon.getBlock());

        beacons.put(
                key,
                new BeaconData(
                        beacon.getWorld().getUID(),
                        beacon.getX(),
                        beacon.getY(),
                        beacon.getZ(),
                        BeaconFarmeur.getBonus(beacon),
                        BeaconFarmeur.getRayon(beacon)
                )
        );
    }

    private double meilleurBonus(Block culture) {
        UUID worldId = culture.getWorld().getUID();
        double meilleurBonus = 0.0;

        for (BeaconData beacon : beacons.values()) {
            if (!beacon.worldId().equals(worldId)) {
                continue;
            }

            double dx = culture.getX() - beacon.x();
            double dy = culture.getY() - beacon.y();
            double dz = culture.getZ() - beacon.z();
            double rayon = beacon.rayon();

            if ((dx * dx) + (dy * dy) + (dz * dz) <= rayon * rayon) {
                meilleurBonus = Math.max(meilleurBonus, beacon.bonus());
            }
        }

        return meilleurBonus;
    }

    private boolean estCulture(Material material) {
        return switch (material) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART -> true;
            default -> false;
        };
    }

    private record BeaconData(
            UUID worldId,
            int x,
            int y,
            int z,
            double bonus,
            int rayon
    ) {
    }

    private record BeaconKey(
            UUID worldId,
            int x,
            int y,
            int z
    ) {
        static BeaconKey from(Block block) {
            return new BeaconKey(
                    block.getWorld().getUID(),
                    block.getX(),
                    block.getY(),
                    block.getZ()
            );
        }

        int chunkX() {
            return x >> 4;
        }

        int chunkZ() {
            return z >> 4;
        }
    }
}
