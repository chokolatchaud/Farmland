package fr.kevyn.farmland.market;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /market - affiche l'etat actuel du marche par metier a n'importe quel joueur.
 */
public class MarketCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Market market = MarketHolder.get();

        sender.sendMessage("§6═══ État du marché ═══");
        sender.sendMessage(ligneMetier("⛏ Mineur", market.getMoneyforcoefMineur()));
        sender.sendMessage(ligneMetier("🌾 Farmeur", market.getMoneyforcoefFarmeur()));
        sender.sendMessage(ligneMetier("🐷 Agriculteur", market.getMoneyforcoefAgriculteur()));
        sender.sendMessage(ligneMetier("🐟 Pêcheur", market.getMoneyforcoefPecheur()));
        sender.sendMessage(ligneMetier("⚔ Tueur", market.getMoneyforcoefTueur()));
        sender.sendMessage("§7Un coefficient élevé = les prix sont bons, vends maintenant !");

        return true;
    }

    private String ligneMetier(String nom, int coefficient) {
        String couleur = coefficient >= 100 ? "§a" : coefficient >= 75 ? "§e" : "§c";
        return "§f" + nom + " §7: " + couleur + coefficient + "%";
    }
}
