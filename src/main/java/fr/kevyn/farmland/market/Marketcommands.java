package fr.kevyn.farmland.market;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.save.MarketSave;

public class Marketcommands implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeul un joueur peut exécuter cette commande !");
			return true;
		}

		Player player = (Player) sender;

		List<MarketSave.MarketSnapshot> history = MarketSave.getFullHistory(FarmlandMain.getPlugin(FarmlandMain.class));

		if (history.isEmpty()) {
			player.sendMessage("§cPas d'historique du marché !");
			return true;
		}

		// Afficher le marché
		afficherMarche(player, history);

		return true;
	}

	private void afficherMarche(Player player, List<MarketSave.MarketSnapshot> history) {
		// 5 catégories : Créativité, Architecture, Densité, Équilibre, Finition
		String[] categories = { "Créativité", "Architecture", "Densité", "Équilibre", "Finition" };

		player.sendMessage("§7╔════════════════════════════════════════════════╗");
		player.sendMessage("§7║           §eMARCHÉ DYNAMIQUE§7              ║");
		player.sendMessage("§7╠════════════════════════════════════════════════╣");

		// Afficher chaque catégorie
		for (int cat = 0; cat < categories.length; cat++) {
			StringBuilder ligne = new StringBuilder();
			ligne.append("§7║ §e").append(String.format("%-12s", categories[cat])).append("§7 ");

			// Afficher 20 caractères pour l'historique
			for (int i = 0; i < 20; i++) {
				char symbol;

				if (i >= history.size()) {
					// Pas de données = point gris
					symbol = '·';
					ligne.append("§8").append(symbol);
				} else {
					// Comparer avec le suivant
					Market marketActuel = history.get(i).market;
					Market marketSuivant = i + 1 < history.size() ? history.get(i + 1).market : null;

					int coefActuel = getCoef(marketActuel, cat);
					int coefSuivant = marketSuivant != null ? getCoef(marketSuivant, cat) : coefActuel;

					if (coefSuivant > coefActuel) {
						symbol = '▲';  // Hausse
						ligne.append("§a").append(symbol);
					} else if (coefSuivant < coefActuel) {
						symbol = '▼';  // Baisse
						ligne.append("§c").append(symbol);
					} else {
						symbol = '─';  // Stable
						ligne.append("§7").append(symbol);
					}
				}
			}

			ligne.append("§7 ║");
			player.sendMessage(ligne.toString());
		}

		player.sendMessage("§7╠════════════════════════════════════════════════╣");
		player.sendMessage("§7║ §a▲ Hausse §7│ §c▼ Baisse §7│ §7─ Stable §7│ §8· Vide §7           ║");
		player.sendMessage("§7╚════════════════════════════════════════════════╝");
	}

	/**
	 * Récupère le coef selon l'index (0=Créativité, 1=Architecture, etc.)
	 */
	private int getCoef(Market market, int index) {
		switch (index) {
			case 0:
				return market.getMoneyforcoefCréativité();
			case 1:
				return market.getMoneyforcoefArchitecture();
			case 2:
				return market.getMoneyforcoefDensité();
			case 3:
				return market.getMoneyforcoefÉquilibre();
			case 4:
				return market.getMoneyforcoefFinition();
			default:
				return 0;
		}
	}
}