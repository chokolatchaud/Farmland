package fr.kevyn.farmland.structure;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.GameRegionHashMap;
import fr.kevyn.farmland.region.TypeRegion;
import fr.kevyn.farmland.save.RegionSave;
import discordwebhook.messagediscord;

public class Definecommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
    	if(command.getName().equalsIgnoreCase("define")) {
    		try {
                // Vérifications de base
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cSeul un joueur peut exécuter cette commande !");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length < 1) {
                    player.sendMessage("§c/define <nom>");
                    return true;
                }

                // Vérifier le joueur
                PlayerServer playerserver = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                if (playerserver == null) {
                    player.kickPlayer("Erreur 23");
                    return true;
                }

                // Vérifier le monde
                String worldPlayer = playerserver.getPlotdata().getNameWorld();
                String worldActuel = player.getWorld().getName();

                if (!worldPlayer.equalsIgnoreCase(worldActuel)) {
                    player.sendMessage("§cTu dois être sur ton plot !");
                    return true;
                }

                // Vérifier la sélection WorldEdit
                Region selection = getSelection(player);
                if (selection == null) {
                    player.sendMessage("§cSelectionne ta structure avec WorldEdit !");
                    return true;
                }

                // Vérifier le chevauchement
                if (isOverlapping(selection, worldActuel)) {
                    player.sendMessage("§cTa sélection chevauche une GameRegion !");
                    return true;
                }

                // Vérifier le nombre de structures
                long structureCount = GameRegionHashMap.getInstance().getRegionhashmap().stream()
                    .filter(r -> r.gettype() == TypeRegion.STRUCTURE && player.getUniqueId().equals(r.getPropriétaire()))
                    .count();

                if (structureCount >= 5) {
                    player.sendMessage("§cTu as atteint le maximum de 5 structures !");
                    return true;
                }

                // Créer la GameRegion
                String nomStructure = args[0];
                GameRegion gameRegion = createGameRegion(selection, player, worldActuel, nomStructure);

                player.sendMessage("§7Scan en cours...");
                messagediscord.sendmessage("Structure créée : " + nomStructure + " par " + player.getName(), "farmland");

                // Scanner async
                Bukkit.getScheduler().runTaskAsynchronously(FarmlandMain.getPlugin(FarmlandMain.class), () -> {
                    int score = scanStructure(gameRegion);

                    // Retour au thread principal
                    Bukkit.getScheduler().runTask(FarmlandMain.getPlugin(FarmlandMain.class), () -> {
                        gameRegion.setScore(score);
                        RegionSave.saveOneRegion(FarmlandMain.getPlugin(FarmlandMain.class), gameRegion);

                        sendScoreMessage(player, score);
                    });
                });

                return true;
            } catch (Exception e) {
                sender.sendMessage("§c[ERREUR] " + e.getMessage());
                e.printStackTrace();
                messagediscord.sendmessage("Erreur Definecommands : " + e.toString(), "erreur");
                return true;
            }
    		
    		
    	}else if(command.getName().equalsIgnoreCase("undefine")) {
    		Player player = (Player) sender;
    		if(player == null) {
    			sender.sendMessage("§cSeul un joueur peut exécuter cette commande !");
                return true;
    			
    		}
    		
    		GameRegion Structureplayer = GameRegionHashMap.getInstance().Playerwhatistregion(player);
    		if(Structureplayer == null) {
    			player.sendMessage(MessageColor.RED.apply("Veuillez vous mettre dans votre structure"));
    		}
    		
    		
    		GameRegionHashMap.getInstance().getRegionhashmap().remove(Structureplayer);
    	    
    	    // 2. ✅ Supprimer le fichier JSON
    	    File regionFile = new File(FarmlandMain.getPlugin(FarmlandMain.class).getDataFolder() + "/regions", 
    	        Structureplayer.getName() + ".json");
    	    if(regionFile.exists()) {
    	        regionFile.delete();
    	    }
    	    player.sendMessage(MessageColor.GREEN.apply("Votre Structure a été supprimée avec succés"));
    	    return true;
    		
    		
    		
    		
    	}
		return true;

        
    }

    // ===== MÉTHODES UTILITAIRES =====

    private Region getSelection(Player player) {
        try {
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
            return session.getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isOverlapping(Region selection, String world) {
        int selMinX = selection.getMinimumPoint().getX();
        int selMinY = selection.getMinimumPoint().getY();
        int selMinZ = selection.getMinimumPoint().getZ();
        int selMaxX = selection.getMaximumPoint().getX();
        int selMaxY = selection.getMaximumPoint().getY();
        int selMaxZ = selection.getMaximumPoint().getZ();

        return GameRegionHashMap.getInstance().getRegionhashmap().stream()
            .filter(r -> r.getWorldname().equalsIgnoreCase(world))
            .anyMatch(r -> !(selMaxX < r.getMinX() || selMinX > r.getMaxX() ||
                             selMaxY < r.getMinY() || selMinY > r.getMaxY() ||
                             selMaxZ < r.getMinZ() || selMinZ > r.getMaxZ()));
    }

    private GameRegion createGameRegion(Region selection, Player player, String world, String nom) {
        GameRegion gameRegion = new GameRegion(
            selection.getMinimumPoint().getX(), selection.getMinimumPoint().getY(), selection.getMinimumPoint().getZ(),
            selection.getMaximumPoint().getX(), selection.getMaximumPoint().getY(), selection.getMaximumPoint().getZ(),
            player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
            nom, false, world, TypeRegion.STRUCTURE, "none"
        );
        gameRegion.setPropriétaire(player.getUniqueId());
        return gameRegion;
    }

    private int scanStructure(GameRegion gameRegion) {
        World world = Bukkit.getWorld(gameRegion.getWorldname());
        if (world == null) return 0;

        int minX = (int) gameRegion.getMinX();
        int minY = (int) gameRegion.getMinY();
        int minZ = (int) gameRegion.getMinZ();
        int maxX = (int) gameRegion.getMaxX();
        int maxY = (int) gameRegion.getMaxY();
        int maxZ = (int) gameRegion.getMaxZ();

        // Compteurs
        int totalBlocs = 0;
        int blocsAir = 0;
        int transitions = 0;
        int blocsAuSol = 0;
        int blocsAuPlafond = 0;
        int blocsBords = 0;
        int blocsInterieur = 0;
        int transitionsVerti = 0;

        Set<String> blocsUniques = new HashSet<>();
        Set<String> famillesBlocs = new HashSet<>();

        // Scanner
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material blocActuel = world.getBlockAt(x, y, z).getType();
                    totalBlocs++;

                    if (blocActuel == Material.AIR) {
                        blocsAir++;
                        continue;
                    }

                    blocsUniques.add(blocActuel.name());

                    // Position
                    if (y == minY) blocsAuSol++;
                    if (y == maxY) blocsAuPlafond++;
                    if (x == minX || x == maxX || z == minZ || z == maxZ) blocsBords++;
                    else blocsInterieur++;

                    // Transitions
                    Material nord = world.getBlockAt(x, y, z - 1).getType();
                    Material sud = world.getBlockAt(x, y, z + 1).getType();
                    Material est = world.getBlockAt(x + 1, y, z).getType();
                    Material ouest = world.getBlockAt(x - 1, y, z).getType();
                    Material haut = world.getBlockAt(x, y + 1, z).getType();
                    Material bas = world.getBlockAt(x, y - 1, z).getType();

                    if (nord != blocActuel) transitions++;
                    if (sud != blocActuel) transitions++;
                    if (est != blocActuel) transitions++;
                    if (ouest != blocActuel) transitions++;
                    if (haut != blocActuel || bas != blocActuel) transitionsVerti++;

                    // Familles
                    addFamille(blocActuel.name(), famillesBlocs);
                }
            }
        }

        // Calcul dimensions
        int blocsPlein = totalBlocs - blocsAir;
        int hauteur = maxY - minY + 1;
        int largeur = maxX - minX + 1;
        int profondeur = maxZ - minZ + 1;

        // Ratios
        float ratioDiversite = Math.min((float) blocsUniques.size() / 20f, 1.0f);
        float ratioFamilles = Math.min((float) famillesBlocs.size() / 7f, 1.0f);
        float ratioComplexite = blocsPlein > 0 ? Math.min((float) transitions / (blocsPlein * 3f), 1.0f) : 0f;
        float ratioVertical = blocsPlein > 0 ? Math.min((float) transitionsVerti / (blocsPlein * 2f), 1.0f) : 0f;
        float ratioVolume = Math.min((float) blocsPlein / 500f, 1.0f);
        float ratioDensite = totalBlocs > 0 ? (float) blocsPlein / totalBlocs : 0f;
        float ratioHauteur = Math.min((float) hauteur / 10f, 1.0f);
        float ratioLargeur = Math.min((float) largeur / 10f, 1.0f);
        float ratioProfondeur = Math.min((float) profondeur / 10f, 1.0f);
        float ratioInterieur = blocsPlein > 0 ? Math.min((float) blocsInterieur / blocsPlein, 1.0f) : 0f;
        float ratioSol = Math.min((float) blocsAuSol / (largeur * profondeur), 1.0f);
        float ratioPlafond = Math.min((float) blocsAuPlafond / (largeur * profondeur * 0.3f), 1.0f);
        float ratioBords = blocsPlein > 0 ? 1f - Math.min((float) blocsBords / blocsPlein, 1.0f) : 0f;
        float ratioPrecieux = hasBlocPrecieux(blocsUniques) ? 0.5f : 0f;
        float ratioProportions = calculateProportions(hauteur, largeur, profondeur);

        // Score final
        float score = (ratioDiversite * 12f)
                    + (ratioFamilles * 8f)
                    + (ratioComplexite * 15f)
                    + (ratioVertical * 10f)
                    + (ratioVolume * 8f)
                    + (ratioDensite * 5f)
                    + (ratioHauteur * 5f)
                    + (ratioLargeur * 3f)
                    + (ratioProfondeur * 3f)
                    + (ratioInterieur * 8f)
                    + (ratioSol * 5f)
                    + (ratioPlafond * 3f)
                    + (ratioBords * 5f)
                    + (ratioPrecieux * 5f)
                    + (ratioProportions * 5f);

        return Math.round(score);
    }

    private void addFamille(String blocName, Set<String> families) {
        if (blocName.contains("LOG") || blocName.contains("PLANK")) families.add("BOIS");
        if (blocName.contains("STONE") || blocName.contains("BRICK")) families.add("PIERRE");
        if (blocName.contains("WOOL") || blocName.contains("GLASS")) families.add("COULEUR");
        if (blocName.contains("LEAF") || blocName.contains("GRASS")) families.add("NATURE");
        if (blocName.contains("GOLD") || blocName.contains("DIAMOND")) families.add("PRECIEUX");
        if (blocName.contains("TERRACOTTA") || blocName.contains("CONCRETE")) families.add("TERRACOTTA");
        if (blocName.contains("SAND") || blocName.contains("GRAVEL")) families.add("NATUREL");
    }

    private boolean hasBlocPrecieux(Set<String> blocsUniques) {
        return blocsUniques.stream()
            .anyMatch(b -> b.contains("GOLD") || b.contains("DIAMOND") || b.contains("EMERALD"));
    }

    private float calculateProportions(int hauteur, int largeur, int profondeur) {
        int min = Math.min(Math.min(hauteur, largeur), profondeur);
        if (min <= 0) return 0f;
        return 1f - Math.abs((float)(hauteur - largeur) / Math.max(hauteur, largeur));
    }

    private void sendScoreMessage(Player player, int score) {
        player.sendMessage("§7====== §eRésultat §7======");
        player.sendMessage("§7Créativité    : §e" + CoefStructure.ScoreToCoefCréativité(score) + "§7/§e35");
        player.sendMessage("§7Architecture  : §e" + CoefStructure.ScoreToCoefArchitecture(score) + "§7/§e23");
        player.sendMessage("§7Densité       : §e" + CoefStructure.ScoreToCoefDensité(score) + "§7/§e11");
        player.sendMessage("§7Équilibre     : §e" + CoefStructure.ScoreToCoefÉquilibre(score) + "§7/§e16");
        player.sendMessage("§7Finition      : §e" + CoefStructure.ScoreToCoefFinition(score) + "§7/§e15");
        player.sendMessage("§7Score global  : §e" + score + "§7/§e100");
        player.sendMessage("§c§o(Score calculé par algorithme)");
    }
}