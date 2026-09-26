package fr.kevyn.farmland.directives.plot.evenement;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.doonees.regions.GameRegion;
import fr.kevyn.farmland.doonees.regions.GameRegionHashMap;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;

import dev.rosewood.rosestacker.event.EntityStackEvent;

/**
 * Centralise les règles de construction, destruction et utilisation du terrain.
 */
public final class EventBuildAndUse implements Listener {

    private static final Set<UUID> COMPTAGE_ERREUR_PREVENU = new HashSet<>();

    private final FarmlandMain plugin;

    public EventBuildAndUse(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBoatPlace(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !isBoat(item.getType())) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, false, null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandPlace(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ARMOR_STAND) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, false, null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        Block block = event.getBlock();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, false, null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, false, null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawnMob(CreatureSpawnEvent event, Player player) {

        PlayerServer playerServer = getPlayerServer(player);
        if (playerServer == null || playerServer.getPlotdata() == null) {
            return;
        }
        if (!playerServer.getPlotdata().getMobSpawn()) {
            event.setCancelled(true);
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL ) {

            if (!(event.getEntity() instanceof Animals animal)) { return; }

            if(!canBreed(event.getEntityType())){return;}



            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityStack(EntityStackEvent event) {

        LivingEntity entity = event.getStack().getEntity();

        if (!canBreed(entity.getType())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, true, null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTntprime(TNTPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() != EntityType.TNT) {
            return;
        }

        event.blockList().clear();
        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material bucket = event.getBucket();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, false, bucket)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material bucket = event.getBucket();
        GameRegion region = getRegion(block);

        if (!canBuild(player, region, block, false, bucket)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        GameRegion region = GameRegionHashMap.getInstance().Playerwhatistregion(player);
        if (!canBuild(player, region, null, false, null)) {
            event.setCancelled(true);
        }
    }

    /**
     * Vérifie si un joueur est autorisé à construire ou utiliser un bloc.
     */
    public boolean canBuild(Player player, GameRegion region, Block block, boolean countBlock, Material bucket) {
        if (player.hasPermission("farmland.placeblocbypass")) {
            if (countBlock) {
                countBlockPlacement(player);
            }
            return true;
        }

        if (!player.hasPermission("farmland.placebloc")) {
            player.sendMessage(MessageColor.RED.apply(
                    "❌ Vous n'avez pas la permission de placer/détruire des blocs."
            ));
            return false;
        }

        if (isRestrictedBlock(block)) {
            return false;
        }

        if (region != null) {
            return canBuildInRegion(player, region);
        }

        if (isOutsideAllowedPlot(player)) {
            return false;
        }

        if (!canUseWaterLava(player, block, bucket)) {
            return false;
        }

        if (countBlock) {
            countBlockPlacement(player);
        }

        return true;
    }

    /**
     * Vérifie les droits de construction spécifiques à une GameRegion.
     */
    public boolean canBuildInRegion(Player player, GameRegion region) {
        if (!region.getCanbuild()) {
            player.sendMessage(MessageColor.RED.apply("⛔ Cette région est protégée !"));
            return false;
        }

        UUID owner = region.getPropriétaire();
        if (owner == null) {
            player.sendMessage(MessageColor.RED.apply("Cette région n'a pas de propriétaire !"));
            return false;
        }

        if (!player.getUniqueId().equals(owner)) {
            player.sendMessage(MessageColor.RED.apply("Tu n'as pas le droit ici !"));
            return false;
        }

        return true;
    }

    /**
     * Vérifie que le joueur se trouve sur son plot, un plot ajouté ou un plot trusted.
     */
    public boolean isOutsideAllowedPlot(Player player) {
        PlayerServer playerServer = getPlayerServer(player);
        if (playerServer == null || playerServer.getPlotdata() == null) {
            player.sendMessage(MessageColor.RED.apply(
                    "⚠ Vos données serveur sont introuvables !"
            ));
            return true;
        }

        String currentWorld = player.getWorld().getName();
        if (currentWorld.equalsIgnoreCase(playerServer.getPlotdata().getPlotProprety())) {
            return false;
        }
        if (playerServer.getPlotdata().getAllplotadd().contains(currentWorld)) {
            return false;
        }
        if (playerServer.getPlotdata().getAllplottrust().contains(currentWorld)) {
            return false;
        }

        player.sendMessage(MessageColor.RED.apply(
                "⛔ Vous ne pouvez pas modifier ce terrain !"
        ));
        return true;
    }

    public void countBlockPlacement(Player player) {
        PlayerServer playerServer = getPlayerServer(player);
        if (playerServer == null) {
            warnCountingError(player);
            return;
        }

        playerServer.setBlocpose(playerServer.getBlocpose() + 1);
        playerServer.setBlocposetotal(playerServer.getBlocposetotal() + 1);

        if (playerServer.getBlocpose() >= 150) {
            playerServer.setBlocpose(playerServer.getBlocpose() - 150);
            playerServer.setMoney(playerServer.getMoney() + 25);
            player.sendMessage(MessageColor.AQUA.apply("+25FB pour 150 blocs placés !"));
        }
    }

    /**
     * Vérifie les autorisations d'utilisation des seaux d'eau et de lave.
     */
    public boolean canUseWaterLava(Player player, Block block, Material bucket) {
        if (bucket == null) {
            return true;
        }

        PlayerServer playerServer = getPlayerServer(player);
        if (playerServer == null || playerServer.getPlotdata() == null) {
            return false;
        }

        String currentWorld = player.getWorld().getName();
        boolean isOwner = currentWorld.equalsIgnoreCase(
                playerServer.getPlotdata().getPlotProprety()
        );
        boolean isTrusted = playerServer.getPlotdata().getAllplottrust().contains(currentWorld)
                || playerServer.getPlotdata().getAllplotadd().contains(currentWorld);

        if (!isOwner && !isTrusted) {
            player.sendMessage(MessageColor.RED.apply(
                    "❌ Vous ne pouvez pas utiliser de seau ici."
            ));
            return false;
        }

        if (playerServer.getPlotdata().getwaterlava()) {
            player.sendMessage(MessageColor.RED.apply(
                    "&c❌ L'eau et la lave sont désactivées dans ce plot !"
            ));
            return false;
        }

        return true;
    }

    public boolean isBoat(Material type) {
        return type.name().endsWith("_BOAT") || type.name().endsWith("_RAFT");
    }

    private GameRegion getRegion(Block block) {
        return GameRegionHashMap.getInstance().Blockwhatistregion(block);
    }

    private PlayerServer getPlayerServer(Player player) {
        return PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
    }

    private boolean isRestrictedBlock(Block block) {
        if (block == null) {
            return false;
        }

        Material type = block.getType();
        return type == Material.SPAWNER
                || type == Material.COMMAND_BLOCK
                || type == Material.REPEATING_COMMAND_BLOCK
                || type == Material.CHAIN_COMMAND_BLOCK
                || type == Material.COMMAND_BLOCK_MINECART
                || type == Material.STRUCTURE_BLOCK
                || type == Material.STRUCTURE_VOID;
    }

    private void warnCountingError(Player player) {
        UUID uuid = player.getUniqueId();
        if (!COMPTAGE_ERREUR_PREVENU.add(uuid)) {
            return;
        }

        player.sendMessage(MessageColor.RED.apply(
                "⚠ Erreur dans le comptage de vos blocs."
        ));
        messagediscord.sendmessage(
                "Erreur comptage blocs pour " + player.getName(),
                "statut"
        );
    }

    private boolean canBreed(EntityType type) {
        return switch (type) {
            case COW,
                 SHEEP,
                 PIG,
                 CHICKEN,
                 RABBIT,
                 GOAT,
                 HORSE,
                 DONKEY,
                 LLAMA,
                 CAMEL,
                 FOX,
                 PANDA,
                 TURTLE,
                 BEE,
                 CAT,
                 WOLF -> true;
            default -> false;
        };
    }
}
