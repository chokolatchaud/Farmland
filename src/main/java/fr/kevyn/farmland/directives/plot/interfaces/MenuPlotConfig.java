package fr.kevyn.farmland.directives.plot.interfaces;

import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.directives.plot.gestion.Meteo;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import fr.kevyn.farmland.doonees.plots.PlotData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import fr.kevyn.farmland.directives.gameplay.objet.CustomItemType;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;

import java.util.List;

public class MenuPlotConfig {

	private static final String TITRE = MessageColor.DARK_GREEN.apply("Configuration du plot");

	public static Inventory createmenuplotconfig(String name, PlayerServer playerserver) {
        Inventory inv = Bukkit.createInventory(null, 27, TITRE);
        GameMenu plotconfig = new GameMenu(inv, TypeMenu.PLOTCONFIG);
        GameMenu.fillmenu(Material.GREEN_STAINED_GLASS_PANE, inv);
        PlotData plot = playerserver.getPlotdata();

        // CLOCK - Jour/Nuit
        boolean nuit = plot.getMeteoTime() == Meteo.NIGHT;
        GameMenu.set_oneitem_menu(CustomItemType.CLOCK_DAYNIGHT.create(), MessageColor.YELLOW.apply("Jour/Nuit"),
                description("Passe ton plot au jour ou à la nuit.", nuit ? "Nuit" : "Jour"), 10, inv);

        // LAPIS - Pluie
        boolean pluie = plot.getMeteoRain() == Meteo.RAIN;
        GameMenu.set_oneitem_menu(CustomItemType.RAIN_TOGGLE.create(), MessageColor.BLUE.apply("Pluie"),
                description("Fait pleuvoir ou arrête la pluie sur ton plot.", pluie ? "Pluie" : "Beau temps"), 19, inv);

        // CYAN WOOL - Temps figé
        boolean tempsFige = plot.getMeteoActive() == Meteo.TIMEFALSE;
        GameMenu.set_oneitem_menu(CustomItemType.TIME_FREEZE.create(), MessageColor.GOLD.apply("Temps qui passe"),
                description("Fige l'heure de ton plot ou laisse le temps défiler.", tempsFige ? "Figé" : "Le temps passe"), 1, inv);

        // WATER BUCKET - Eau/Lave
        String namewaterlava;
        if (plot.getwaterlava()) {
        	namewaterlava = "Eau/Lave désactivé";
        } else {
        	namewaterlava = "Eau/Lave activé";
        }
        GameMenu.set_oneitem_menu(CustomItemType.WATERLAVASELECTION.create(), MessageColor.AQUA.apply(namewaterlava),
                description("Autorise ou interdit la pose d'eau et de lave.", plot.getwaterlava() ? "Interdit" : "Autorisé"), 12, inv);

        // DOOR - Privé/Public
        String namePrivatepublic;
        if (plot.getPrivateplot()) {
        	namePrivatepublic = "Plot privé";
        } else {
        	namePrivatepublic = "Plot public";
        }

        // MOBSPAWN
        String nameMobSpawn;
        if (plot.getMobSpawn()) {
            nameMobSpawn = "Spawn des mobs activé";
        } else {
            nameMobSpawn = "Spawn des mobs désactivé";
        }

        GameMenu.set_oneitem_menu(CustomItemType.DOOR_PRIVACY.create(), MessageColor.GREEN.apply(namePrivatepublic),
                description("Un plot privé ne peut pas être visité par les autres joueurs.", plot.getPrivateplot() ? "Privé" : "Public"), 14, inv);
        GameMenu.set_oneitem_menu(CustomItemType.MOB_SPAWN.create(), MessageColor.RED.apply(nameMobSpawn),
                description("Autorise ou empêche l'apparition des mobs sur ton plot.", plot.getMobSpawn() ? "Activé" : "Désactivé"), 16, inv);
        return inv;
    }

	/** Description d'un réglage : ce qu'il fait, son état actuel, et l'action */
	private static List<String> description(String explication, String etat) {
		return List.of(
				MessageColor.GRAY.apply(explication),
				"",
				MessageColor.GRAY.apply("Actuellement : ") + MessageColor.WHITE.apply(etat),
				MessageColor.YELLOW.apply("Clic pour changer"));
	}

}
