package fr.kevyn.farmland.tueur;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.agriculteur.ArmesUtil;
import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * /upgradeepee - ameliore l'Epee (arme du Tueur). 0 a 10, cout croissant.
 */
public class UpgradeEpeeCommand implements CommandExecutor {

    private static final int NIVEAU_MAX = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return true;

        int niveauActuel = ps.getEpeeLevel();
        if (niveauActuel >= NIVEAU_MAX) {
            player.sendMessage("§cTon Épée est déjà au niveau maximum (" + NIVEAU_MAX + ") !");
            return true;
        }

        int cout = 100 * (niveauActuel + 1);
        if (ps.getMoney() < cout) {
            player.sendMessage("§cIl te faut " + cout + " $FB pour améliorer ton Épée !");
            return true;
        }

        ItemStack epeeEnMain = player.getInventory().getItemInMainHand();
        if (!Outils.isOutilsAttendu(epeeEnMain, org.bukkit.Material.NETHERITE_SWORD)) {
            player.sendMessage("§cTiens ton Épée en main principale pour l'améliorer !");
            return true;
        }

        ps.setMoney(ps.getMoney() - cout);
        ps.setEpeeLevel(niveauActuel + 1);
        epeeFarm.appliquerDegats(epeeEnMain, ArmesUtil.calculerDegats(niveauActuel + 1));

        player.sendMessage("§aÉpée améliorée au niveau " + (niveauActuel + 1) + " ! (-" + cout + " $FB)");
        return true;
    }
}
