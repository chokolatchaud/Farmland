package fr.kevyn.farmland.Farming;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * /upgradehoue - ameliore les chances de recolte multipliee. 0 a 10, cout croissant.
 */
public class UpgradeHoueCommand implements CommandExecutor {

    private static final int NIVEAU_MAX = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return true;

        int niveauActuel = ps.getHoueLevel();
        if (niveauActuel >= NIVEAU_MAX) {
            player.sendMessage("§cTa Houe est déjà au niveau maximum (" + NIVEAU_MAX + ") !");
            return true;
        }

        // cout eleve : debloque des multiplicateurs de rendement brut, l'effet
        // devient tres puissant a haut niveau, donc plus cher pour eviter le snowball
        int cout = 150 * (niveauActuel + 1);
        if (ps.getMoney() < cout) {
            player.sendMessage("§cIl te faut " + cout + " $FB pour améliorer ta Houe !");
            return true;
        }

        ps.setMoney(ps.getMoney() - cout);
        ps.setHoueLevel(niveauActuel + 1);

        player.sendMessage("§aHoue améliorée au niveau " + (niveauActuel + 1) + " ! (-" + cout + " $FB)");
        return true;
    }
}
