package fr.kevyn.farmland.mineur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * /upgradepioche - ameliore les chances de minerai rare. 0 a 10, cout croissant.
 */
public class UpgradePiocheCommand implements CommandExecutor {

    private static final int NIVEAU_MAX = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return true;

        int niveauActuel = ps.getCobblestonegeneratorlevel();
        if (niveauActuel >= NIVEAU_MAX) {
            player.sendMessage("§cTon Cobblegenerator est déjà au niveau maximum (" + NIVEAU_MAX + ") !");
            return true;
        }

        int cout = 100 * (niveauActuel + 1);
        if (ps.getMoney() < cout) {
            player.sendMessage("§cIl te faut " + cout + " $FB pour améliorer ton Cobblegenerator !");
            return true;
        }

        ps.setMoney(ps.getMoney() - cout);
        ps.setCobblestonegeneratorlevel(niveauActuel + 1);

        player.sendMessage("§aCobblegenerator amélioré au niveau " + (niveauActuel + 1) + " ! (-" + cout + " $FB)");
        return true;
    }
}
