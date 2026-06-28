package fr.kevyn.farmland.worldeditgestion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;

import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.plot.PlotData;

public class WorldEditSecureListener implements Listener {

    private static final Set<String> BANNED_BLOCKS = new HashSet<>(Arrays.asList(
        "tnt", "bedrock", "barrier", "command_block", "chain_command_block",
        "repeating_command_block", "structure_block", "structure_void", "jigsaw",
        "spawner", "end_portal", "end_portal_frame", "end_gateway"
    ));

    public WorldEditSecureListener() {
        WorldEdit.getInstance().getEventBus().register(this);
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("Farmland"));
        Bukkit.getLogger().info("[WorldEditSecure] ✅ Listener WorldEdit/FAWE activé !");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        if (player.hasPermission("farmland.worldeditbypass")) return;

        // Bloquer TOUTES les commandes WorldEdit/FAWE si pas la permission
        if (msg.startsWith("//") || msg.startsWith("/worldedit") || msg.startsWith("/fawe") || msg.startsWith("/we ")) {
            // Autoriser //wand, //pos1, //pos2, //hpos1, //hpos2 pour pouvoir faire /define
            if (msg.startsWith("//wand") || msg.startsWith("//pos1") || msg.startsWith("//pos2")
                || msg.startsWith("//hpos1") || msg.startsWith("//hpos2") || msg.startsWith("//sel")) {
                return; // laisse passer — nécessaire pour /define
            }
            if (!player.hasPermission("farmland.worldedit")) {
                player.sendMessage(MessageColor.RED.apply("❌ Tu n'as pas le WorldEdit ! Tape /buy worldedit (15 $FB / 1h)"));
                event.setCancelled(true);
                return;
            }
        }

        if (msg.startsWith("//set") || msg.startsWith("//replace") || msg.startsWith("//fill") ||
            msg.startsWith("//line") || msg.startsWith("//cyl") || msg.startsWith("//hcyl") ||
            msg.startsWith("//sphere") || msg.startsWith("//hsphere") || msg.startsWith("//paste")) {

            if (!isAuthorized(player)) {
                event.setCancelled(true);
                player.sendMessage("§c❌ Vous n'êtes pas autorisé à utiliser cette commande !");
                return;
            }

            if ((msg.contains("water") || msg.contains("lava")) && !canUseLiquids(player)) {
                event.setCancelled(true);
                player.sendMessage("§c❌ L'eau et la lave sont désactivées dans ce plot !");
                return;
            }

            for (String bannedBlock : BANNED_BLOCKS) {
                if (msg.contains(bannedBlock)) {
                    event.setCancelled(true);
                    player.sendMessage("§c❌ Ce bloc est interdit avec WorldEdit !");
                    return;
                }
            }

            try {
                BukkitPlayer wePlayer = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player);
                SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
                Region sel = sessionManager.findByName(wePlayer.getName()).getSelection(wePlayer.getWorld());

                if (sel != null && !isSelectionInsideWorldBorder(player, sel)) {
                    event.setCancelled(true);
                    player.sendMessage("§c❌ La sélection dépasse la limite du monde !");
                    return;
                }

                

            } catch (Exception e) {
                Bukkit.getLogger().warning("[WorldEditSecure] Erreur lors de la vérification de sélection : " + e.getMessage());
            }
        }
    }

    @Subscribe
    public void onEditSession(EditSessionEvent event) {

        event.setExtent(new AbstractDelegateExtent(event.getExtent()) {

            private boolean warnedAuth = false;
            private boolean warnedLiquid = false;
            private boolean warnedBannedBlock = false;

            @Override
            public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) {

                Actor actor = event.getActor();
                if (actor == null || !actor.isPlayer()) return super.setBlock(location, block);

                Player player = Bukkit.getPlayer(actor.getUniqueId());
                if (player == null) return super.setBlock(location, block);

                boolean hasBypass = player.hasPermission("farmland.worldeditbypass");

                if (!hasBypass) {
                    if (!player.hasPermission("farmland.worldedit")) {
                        return false;
                    }

                    if (!isAuthorized(player) || !isBlockInsideWorldBorder(player, location)) {
                        if (!warnedAuth) {
                            player.sendMessage("§c❌ Vous ne pouvez pas modifier cette zone !");
                            warnedAuth = true;
                        }
                        return false;
                    }

                }

                String blockId = block.getBlockType().getId().toLowerCase();

                if (blockId.contains("water") || blockId.contains("lava")) {
                    if (!canUseLiquids(player)) {
                        if (!warnedLiquid) {
                            player.sendMessage("§c❌ L'eau et la lave sont désactivées dans ce plot !");
                            warnedLiquid = true;
                        }
                        return false;
                    }
                }

                if (isBlockBanned(blockId)) {
                    if (!warnedBannedBlock) {
                        player.sendMessage("§c❌ Ce bloc est interdit avec WorldEdit !");
                        warnedBannedBlock = true;
                    }
                    return false;
                }

                return super.setBlock(location, block);
            }
        });
    }

    private boolean isBlockBanned(String blockId) {
        String cleanId = blockId.replace("minecraft:", "");
        return BANNED_BLOCKS.stream().anyMatch(banned -> cleanId.contains(banned));
    }

    private boolean canUseLiquids(Player player) {
        World world = player.getWorld();
        PlayerServer proprietaire = null;

        for (PlayerServer ps : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
            if (ps.getPlotdata() == null || ps.getPlotdata().getNameWorld() == null) continue;
            if (world.getName().equalsIgnoreCase(ps.getPlotdata().getNameWorld())) {
                proprietaire = ps;
                break;
            }
        }

        if (proprietaire == null) return false;

        boolean isOwner = proprietaire.getPlotdata().getPlotProprety().equals(player.getUniqueId().toString());
        boolean isTrusted = proprietaire.getPlotdata().getAllplottrust().contains(player.getUniqueId().toString());

        if (!isOwner && !isTrusted) return false;

        return !proprietaire.getPlotdata().getwaterlava();
    }

    private boolean isAuthorized(Player player) {
        PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (playerServer == null || playerServer.getPlotdata() == null) return false;

        PlotData plotData = playerServer.getPlotdata();
        String currentWorldName = player.getWorld().getName();
        String ownerWorldName = plotData.getPlotProprety();

        boolean isOwner = ownerWorldName.equalsIgnoreCase(currentWorldName);
        boolean isTrusted = plotData.getAllplottrust().contains(currentWorldName);

        return (isOwner || isTrusted);
    }

    private boolean isBlockInsideWorldBorder(Player player, BlockVector3 loc) {
        WorldBorder border = player.getWorld().getWorldBorder();
        Location center = border.getCenter();
        double halfSize = border.getSize() / 2.0;

        double minX = center.getX() - halfSize;
        double maxX = center.getX() + halfSize;
        double minZ = center.getZ() - halfSize;
        double maxZ = center.getZ() + halfSize;

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return (x >= minX && x <= maxX && z >= minZ && z <= maxZ);
    }

    private boolean isSelectionInsideWorldBorder(Player player, Region region) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        return isBlockInsideWorldBorder(player, min) && isBlockInsideWorldBorder(player, max);
    }
}