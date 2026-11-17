package fr.kevyn.farmland.directives.marché.commands;

import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;
import fr.kevyn.farmland.doonees.marche.Market;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
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
        sender.sendMessage(ligneMetier("⛏ Mineur", MarketCalc.MINEUR, market));
        sender.sendMessage(ligneMetier("🌾 Farmeur", MarketCalc.FARMEUR, market));
        sender.sendMessage(ligneMetier("🐷 Agriculteur", MarketCalc.AGRICULTEUR, market));
        sender.sendMessage(ligneMetier("🐟 Pêcheur", MarketCalc.PECHEUR, market));
        sender.sendMessage(ligneMetier("⚔ Tueur", MarketCalc.TUEUR, market));
        sender.sendMessage("§7Le prix baisse quand on vend beaucoup et remonte avec le temps.");
        sender.sendMessage("§7Au-dessus de 100 % : le métier manque, c'est le moment de vendre !");

        return true;
    }

    private String ligneMetier(String nom, String metier, Market market) {
        int coefficient = market.getCoefficient(metier);
        String couleur = coefficient >= 100 ? "§a" : coefficient >= 75 ? "§e" : "§c";
        String prix = String.format(java.util.Locale.FRANCE, "%.2f", MarketCalc.getPrixActuel(metier, market));
        return "§f" + nom + " §7: " + couleur + coefficient + "% §7(" + prix + " $FB le jeton)";
    }
}
