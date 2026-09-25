package fr.kevyn.farmland.modules.gameplay;

import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.directives.metiers.farmeur.BlockFertilizeListener;
import fr.kevyn.farmland.directives.metiers.farmeur.FarmCommands;
import fr.kevyn.farmland.directives.metiers.farmeur.HarvestFarmEvent;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.metiers.agriculteur.HacheCommands;
import fr.kevyn.farmland.directives.metiers.agriculteur.KillEventAgriculteur;
import fr.kevyn.farmland.directives.metiers.mineur.EventMineSpawn;
import fr.kevyn.farmland.directives.metiers.mineur.MineCommands;
import fr.kevyn.farmland.directives.metiers.pecheur.EventPeche;
import fr.kevyn.farmland.directives.metiers.pecheur.FishingCommands;
import fr.kevyn.farmland.directives.metiers.tueur.KillEventTueur;
import fr.kevyn.farmland.directives.metiers.tueur.épeeCommands;
import fr.kevyn.farmland.doonees.marche.Market;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.directives.marché.calcul.MarketCalcTask;
import fr.kevyn.farmland.persistance.marché.MarketSave;

/**
 * Enregistre les éléments liés aux métiers et au marché.
 */
public final class MetierModule {

    private final FarmlandMain plugin;

    public MetierModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        loadMarket();
        registerMarketTask();
        registerJobListeners();
        registerJobCommands();

        plugin.getLogger().info("[Metier] Module Metier activé");
        messagediscord.sendmessage("Module Metier bien lancé", "statut");
    }

    private void loadMarket() {
        Market market = MarketSave.loadMarket(plugin);
        if (market != null) {
            MarketHolder.set(market);
        }
    }

    private void registerMarketTask() {
        MarketCalcTask.demarrer(plugin);
    }

    private void registerJobListeners() {
        plugin.getServer().getPluginManager().registerEvents(new EventMineSpawn(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new HarvestFarmEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EventPeche(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new KillEventAgriculteur(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new KillEventTueur(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BlockFertilizeListener(), plugin);
    }

    private void registerJobCommands() {
        plugin.getCommand("houe").setExecutor(new FarmCommands());
        plugin.getCommand("pioche").setExecutor(new MineCommands());
        plugin.getCommand("peche").setExecutor(new FishingCommands());
        plugin.getCommand("epee").setExecutor(new épeeCommands());
        plugin.getCommand("hache").setExecutor(new HacheCommands());
    }
}
