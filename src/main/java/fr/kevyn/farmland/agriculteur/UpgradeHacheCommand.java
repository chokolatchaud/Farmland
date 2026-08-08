package fr.kevyn.farmland.agriculteur;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.menufarm.Outils;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;

/**
 * /upgradehache - ameliore la Hache (arme de l'Agriculteur). 0 a 10, cout croissant.
 */
public class UpgradeHacheCommand implements CommandExecutor {

    private static final int NIVEAU_MAX = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
        if (ps == null) return true;

        int niveauActuel = ps.getHacheLevel();
        if (niveauActuel >= NIVEAU_MAX) {
            player.sendMessage("§cTa Hache est déjà au niveau maximum (" + NIVEAU_MAX + ") !");
            return true;
        }

        int cout = 100 * (niveauActuel + 1) * 3;
        if (ps.getMoney() < cout) {
            player.sendMessage("§cIl te faut " + cout + " $FB pour améliorer ta Hache !");
            return true;
        }

        ItemStack hacheEnMain = player.getInventory().getItemInMainHand();
        if (!Outils.isOutilsAttendu(hacheEnMain, Material.NETHERITE_AXE)) {
            player.sendMessage("§cTiens ta Hache en main principale pour l'améliorer !");
            return true;
        }

        ps.setMoney(ps.getMoney() - cout);
        ps.setHacheLevel(niveauActuel + 1);
        HacheFarm.appliquerDegats(hacheEnMain, ArmesUtil.calculerDegats(niveauActuel + 1));

        player.sendMessage("§aHache améliorée au niveau " + (niveauActuel + 1) + " ! (-" + cout + " $FB)");
        return true;
    }
}
