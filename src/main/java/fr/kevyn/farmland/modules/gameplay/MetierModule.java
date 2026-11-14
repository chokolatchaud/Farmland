package fr.kevyn.farmland.modules.gameplay;

import fr.kevyn.farmland.directives.administration.messagediscord;
import fr.kevyn.farmland.directives.gameplay.recompense.LevelRewardManager;
import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.directives.metiers.MetiersCommands;
import fr.kevyn.farmland.directives.metiers.MetiersMenuListener;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Enregistre les éléments liés aux métiers et au marché.
 */
public final class MetierModule {

    private final FarmlandMain plugin;
    private FileConfiguration rewardsConfig;
    private LevelRewardManager levelRewardManager;

    public MetierModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        loadRewardsConfig();

        levelRewardManager =
                new LevelRewardManager(rewardsConfig);

        RecompenseUtil.init(levelRewardManager);

        loadMarket();
        registerMarketTask();
        registerJobListeners();
        registerJobCommands();

        plugin.getLogger().info("[Metier] Module Metier activé");
        messagediscord.sendmessage("Module Metier bien lancé", "statut");
    }

    private void loadRewardsConfig() {
        File rewardsFile =
                new File(plugin.getDataFolder(), "rewards.yml");

        if (!rewardsFile.exists()) {
            plugin.saveResource("rewards.yml", false);
        }

        rewardsConfig =
                YamlConfiguration.loadConfiguration(rewardsFile);
    }


    public FileConfiguration getRewardsConfig() {
        return rewardsConfig;
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
        plugin.getServer().getPluginManager().registerEvents(new MetiersMenuListener(), plugin);
    }

    private void registerJobCommands() {
        plugin.getCommand("houe").setExecutor(new FarmCommands());
        plugin.getCommand("pioche").setExecutor(new MineCommands());
        plugin.getCommand("peche").setExecutor(new FishingCommands());
        plugin.getCommand("epee").setExecutor(new épeeCommands());
        plugin.getCommand("hache").setExecutor(new HacheCommands());
        plugin.getCommand("metiers").setExecutor(new MetiersCommands());

    }
}
