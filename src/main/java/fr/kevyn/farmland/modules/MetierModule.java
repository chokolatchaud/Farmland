package fr.kevyn.farmland.modules;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.ChatListener;
import fr.kevyn.farmland.Farming.BlockFertilizeListener;
import fr.kevyn.farmland.Farming.FarmCommands;
import fr.kevyn.farmland.Farming.HarvestFarmEvent;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.agriculteur.HacheCommands;
import fr.kevyn.farmland.agriculteur.KillEventAgriculteur;
import fr.kevyn.farmland.mineur.EventMineSpawn;
import fr.kevyn.farmland.mineur.MineCommands;
import fr.kevyn.farmland.pecheur.EventPeche;
import fr.kevyn.farmland.pecheur.FishingCommands;
import fr.kevyn.farmland.tueur.KillEventTueur;
import fr.kevyn.farmland.tueur.épeeCommands;
import fr.kevyn.farmland.market.Market;
import fr.kevyn.farmland.market.MarketHolder;
import fr.kevyn.farmland.market.MarketCalcTask;
import fr.kevyn.farmland.save.MarketSave;

/**
 * Enregistre les éléments liés aux métiers et au marché.
 */
public final class MetierModule {

    private final FarmlandMain plugin;

    public MetierModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Market market = MarketSave.loadMarket(plugin);
        if (market != null) {
            MarketHolder.set(market);
        }

        MarketCalcTask.demarrer(plugin);

        plugin.getServer().getPluginManager().registerEvents(new EventMineSpawn(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new HarvestFarmEvent(), plugin);
        plugin.getCommand("houe").setExecutor(new FarmCommands());
        plugin.getCommand("pioche").setExecutor(new MineCommands());
        plugin.getCommand("peche").setExecutor(new FishingCommands());
        plugin.getServer().getPluginManager().registerEvents(new EventPeche(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new KillEventAgriculteur(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new KillEventTueur(), plugin);
        plugin.getCommand("epee").setExecutor(new épeeCommands());
        plugin.getCommand("hache").setExecutor(new HacheCommands());
        plugin.getServer().getPluginManager().registerEvents(new BlockFertilizeListener(), plugin);

        plugin.getLogger().info("[Metier] Module Metier activé");
        messagediscord.sendmessage("Module Metier bien lancé", "statut");
    }
}
