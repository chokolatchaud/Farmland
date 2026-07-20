package fr.kevyn.farmland.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.boathub.BoatGamemanager;

/**
 * /hub — téléporte le joueur au hub du serveur.
 * Le monde du hub est configurable dans config.yml (hub.world).
 * Servira plus tard de point d'entrée vers le Build Battle Car.
 */
public class HubCommand implements CommandExecutor {

    private final FarmlandMain plugin;

    public HubCommand(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(command.getName().equalsIgnoreCase("joinboat")) {
    		if (!(sender instanceof Player)) {
                sender.sendMessage("Seul un joueur peut exécuter cette commande !");
                return true;
            }
    		Player player = (Player) sender;
    		
    		BoatGamemanager.join(player,plugin);
    		return true;
    		
    	}
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut exécuter cette commande !");
            return true;
        }
        
        Player player = (Player) sender;

        String worldName = plugin.getConfig().getString("hub.world", "world");
        World hubWorld = Bukkit.getWorld(worldName);

        if (hubWorld == null) {
            player.sendMessage(MessageColor.RED.apply("Le hub est introuvable, préviens un modérateur !"));
            plugin.getLogger().warning("[Hub] Monde '" + worldName + "' introuvable (config: hub.world)");
            return true;
        }

        Location spawn = hubWorld.getSpawnLocation().clone().add(0.5, 0, 0.5);
        player.teleport(spawn);
        player.sendMessage(MessageColor.GREEN.apply("✦ Téléportation au hub !"));
        return true;
    }
    
    
}
