package fr.kevyn.farmland.EventBuild;


import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;

public class EventBuildAndUse implements Listener {
    private FarmlandMain plugin;
  

    public EventBuildAndUse(FarmlandMain plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
    	Block bloc = event.getBlock();
    	GameRegion gameregion = GameRegionHashMap.getInstance().Blockwhatistregion(bloc);
    	if(!authorizedbuild(player, gameregion, bloc, false, null)) {
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void onSpawnMob(EntitySpawnEvent event) {
    	if (event.getEntityType() == EntityType.ARMOR_STAND) return;
        if (event.getEntity() instanceof LivingEntity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	Player player = event.getPlayer();
    	Block bloc = event.getBlock();
    	GameRegion gameregion = GameRegionHashMap.getInstance().Blockwhatistregion(bloc);
    	
    	if(!authorizedbuild(player, gameregion, bloc, true,null)) {
    		event.setCancelled(true);
    	}
    	
    }


    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    	Player player = event.getPlayer();
    	Block bloc = event.getBlock();
    	Material bucket = event.getBucket();
    	GameRegion gameregion = GameRegionHashMap.getInstance().Blockwhatistregion(bloc);
    	if(!authorizedbuild(player, gameregion, bloc,false, bucket)) {
    		event.setCancelled(true);
    	}
        
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
    	Player player = event.getPlayer();
    	Block bloc = event.getBlock();
    	Material bucket = event.getBucket();
    	GameRegion gameregion = GameRegionHashMap.getInstance().Blockwhatistregion(bloc);
    	if(!authorizedbuild(player, gameregion, bloc,false, bucket)) {
    		event.setCancelled(true);
    	}
        
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
        	GameRegion gameregion = GameRegionHashMap.getInstance().Playerwhatistregion(player);
        	if(!authorizedbuild(player, gameregion, null,false, null)) {
        		event.setCancelled(true);
        	}
            
        }
    }
    
    
    
    
    public boolean authorizedbuild(Player player, GameRegion region,Block bloc, Boolean countbloc, Material bucket) {
        if (player.hasPermission("farmland.placeblocbypass")) {
        	if(countbloc) {countBlockPlacement(player);}
    		return true;
        };

        
        //on verifie la permission
        if (!player.hasPermission("farmland.placebloc")) {
            player.sendMessage(MessageColor.RED.apply("❌ Vous n'avez pas la permission de placer/détruire des blocs."));
            return false;
        }

        
        //on verfie si le bloc est autorisé
        if (bloc != null && (bloc.getType() == Material.SPAWNER || bloc.getType() == Material.COMMAND_BLOCK)) {
            return false;
        }
        
        
        //on verifie si Region
        if (region != null) {
        	return whereonregionplayer(player, region);
        	}

        
        //On verifie si cest son plot ADD/TRUST
        if(whereonplotplayers(player)) {   	
        	return false;
        }
        
        if(!canUseWaterLava(player, bloc, bucket)) {
        	return false;
        }
        
        //Sinnon on fait
        if(countbloc) {countBlockPlacement(player);}
		return true;
    }
    
    
    

    public boolean whereonregionplayer(Player player, GameRegion gameregion) {
    	if (gameregion.getCanbuild()) {
            UUID playerproprietaire = gameregion.getPropriétaire();
            
            if (playerproprietaire == null) {
            	player.sendMessage(MessageColor.RED.apply("Cette région n'a pas de propriétaire !"));
                return false;
                
            }
            if (!player.getUniqueId().equals(playerproprietaire)) {
            	 player.sendMessage(MessageColor.RED.apply("Tu n'as pas le droit ici !"));
                return false;
               
            }
        } else {
        	player.sendMessage(MessageColor.RED.apply("⛔ Cette région est protégée !"));
            return false;
            
        }
    	return true;

    	
    }

    public boolean whereonplotplayers(Player player) {

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null || ps.getPlotdata() == null) {
            player.sendMessage(MessageColor.RED.apply("⚠ Vos données serveur sont introuvables !"));
            return true;
        }
        String currentWorld = player.getWorld().getName();

        if (currentWorld.equalsIgnoreCase(ps.getPlotdata().getPlotProprety())) return false;
        if (ps.getPlotdata().getAllplotadd().contains(currentWorld)) return false;
        if (ps.getPlotdata().getAllplottrust().contains(currentWorld)) return false;

        player.sendMessage(MessageColor.RED.apply("⛔ Vous ne pouvez pas modifier ce terrain !"));
        return true;
    }

    // joueurs déjà prévenus d'une erreur de comptage (évite le spam à chaque bloc)
    private static final java.util.Set<java.util.UUID> comptageErreurPrevenu = new java.util.HashSet<>();

    public void countBlockPlacement(Player player) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) {
            if (!comptageErreurPrevenu.contains(player.getUniqueId())) {
                comptageErreurPrevenu.add(player.getUniqueId());
                player.sendMessage(MessageColor.RED.apply("⚠ Erreur dans le comptage de vos blocs."));
                messagediscord.sendmessage("Erreur comptage blocs pour " + player.getName(), "statut");
            }
            return;
        }

        ps.setBlocpose(ps.getBlocpose() + 1);
        ps.setBlocposetotal(ps.getBlocposetotal() + 1);

        if (ps.getBlocpose() >= 150) {
            ps.setBlocpose(ps.getBlocpose() - 150);
            ps.setMoney(ps.getMoney() + 3);
            player.sendMessage(MessageColor.AQUA.apply("+3$ pour 150 blocs placés !"));
        }
    }

    public boolean canUseWaterLava(Player player, Block bloc, Material Bucket) {
    	
    	
    	if(Bucket == null) {
    		return true;
    	}
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null || ps.getPlotdata() == null) return false;

        String currentWorld = player.getWorld().getName();
        boolean isOwner = currentWorld.equalsIgnoreCase(ps.getPlotdata().getPlotProprety());
        boolean isTrusted = ps.getPlotdata().getAllplottrust().contains(currentWorld) ||
                           ps.getPlotdata().getAllplotadd().contains(currentWorld);

        if (!isOwner && !isTrusted) {
            player.sendMessage(MessageColor.RED.apply("❌ Vous ne pouvez pas utiliser de seau ici."));
            return false;
        }

        if (ps.getPlotdata().getwaterlava()) {
            player.sendMessage(MessageColor.RED.apply("&c❌ L'eau et la lave sont désactivées dans ce plot !"));
            return false;
        }

        return true;
    }
    
 





}