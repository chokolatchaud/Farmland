package fr.kevyn.plot;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;

public class Plot {
    UUID uuid;
    String plotidentifiant;
    World world;

    public Plot(UUID uuid, Plugin plugin) {
        this.uuid = uuid;
        String nameplot = uuid.toString();
        
        WorldManager multivers = MultiverseCoreApi.get().getWorldManager();
        
        System.out.println("Construction du plot demandée pour: " + nameplot);
        
        // ✅ AJOUTÉ : Vérification sécurité
        if (Bukkit.getWorld(nameplot) != null) {
            System.out.println("Le monde est déjà chargé dans Bukkit.");
            initializeWorld(nameplot);
            return;
        }
        
        if (multivers.isWorld(nameplot)) {
            System.out.println("Le monde existe dans la config Multiverse. Tentative de chargement...");
            
            boolean loaded = multivers.loadWorld(nameplot).isSuccess();

            if (loaded) {
                Bukkit.getScheduler().runTaskLater(plugin, task -> {
                    initializeWorld(nameplot);
                }, 40L);
                return; 
            } else {
                System.out.println("ERREUR: Chargement impossible. On retire le monde de la config pour tenter une réimportation.");
                multivers.removeWorld(nameplot);
            }
        }

        System.out.println("Création/Réimportation du monde: " + nameplot);
        
        boolean created = multivers.createWorld(
            CreateWorldOptions.worldName(nameplot)
                .worldType(WorldType.FLAT)
                .generateStructures(false)
        ).isSuccess();
        
        if (created) {
            Bukkit.getScheduler().runTaskLater(plugin, task -> {
                initializeWorld(nameplot);
            }, 80L);
        } else {
            System.err.println("ERREUR CRITIQUE: Impossible de créer ou réimporter le monde " + nameplot);
        }
    }
    
    private void initializeWorld(String nameplot) {
        World world = Bukkit.getWorld(nameplot);
        
        if (world == null) {
            System.out.println("ERREUR FATALE: Le monde " + nameplot + " est toujours null après attente!");
            return;
        }
        
        this.world = world;
        
        world.getWorldBorder().setSize(50);
        world.setAutoSave(true);

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.TRUE);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, Boolean.TRUE);
        
        System.out.println("Monde initialisé avec succès: " + nameplot);
    }
    
    public World getWorld() {
        return world;
    }
    
    public static World getWorldforname(String name) {
        return Bukkit.getWorld(name);
    }
    
    public static Plot Worldtoplot(World world) {
        if (PlotHashmap.getInstance() == null) return null;
        
        Map<String, Plot> plotinhashmap = PlotHashmap.getInstance().getPlots();
        if (plotinhashmap == null) return null;

        for (Plot plot : plotinhashmap.values()) {
            if (plot.getWorld() != null && plot.getWorld().equals(world)) {
                return plot;
            }
        }
        return null;
    }
}