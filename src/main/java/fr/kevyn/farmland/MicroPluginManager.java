package fr.kevyn.farmland;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.ChatListener;
import fr.kevyn.farmland.EventBuild.EventBuildAndUse;
import fr.kevyn.farmland.EventBuild.LuckpermGrade;
import fr.kevyn.farmland.EventBuild.Plotinventory;
import fr.kevyn.farmland.Farming.FarmCommands;
import fr.kevyn.farmland.Farming.HoueFarmEvent;
import fr.kevyn.farmland.Farming.HarvestFarmEvent;
import fr.kevyn.farmland.Farming.BlockPlantSecureListener;
import fr.kevyn.farmland.boathub.BoatRaceHologram;
import fr.kevyn.farmland.boathub.BoatRaceListener;
import fr.kevyn.farmland.boathub.DailyBoatReward;
import fr.kevyn.farmland.boathub.RaceAdminCommands;
import fr.kevyn.farmland.chat.AnnouncementBroadcaster;
import fr.kevyn.farmland.chat.ChatCalcListener;
import fr.kevyn.farmland.game.GameCommands;
import fr.kevyn.farmland.game.HubCommand;
import fr.kevyn.farmland.market.BuyCommands;
import fr.kevyn.farmland.market.MarketHolograms;
import fr.kevyn.farmland.menufarm.BagCommands;
import fr.kevyn.farmland.menufarm.MenuListenerFarm;
import fr.kevyn.farmland.menufarm.ShopCommand;
import fr.kevyn.farmland.moderation.ModerationCommands;
import fr.kevyn.farmland.playerserver.PlayerAdminCommands;
import fr.kevyn.farmland.save.Filesave;
import fr.kevyn.farmland.scoreboard.CreativePlotScoreboard;
import fr.kevyn.farmland.tpa.TpaCommand;
import fr.kevyn.farmland.vote.VoteCommand;
import fr.kevyn.farmland.vote.VoteListener;
import fr.kevyn.farmland.worldeditgestion.WorldEditSecureListener;
import fr.kevyn.plot.PlotAdminCommands;
import fr.kevyn.plot.Plotcommands;
import fr.kevyn.farmland.mineur.EventMineSpawn;

public class MicroPluginManager {

    public static void moduleGame(FarmlandMain plugin) {
    	
    	int timecalculateMarket = 20 *60 *60 *24 * 1; //1 jour
    	int timeDonateMoneyStructure = 20 * 60 * 30; //30 minutes

        GameCommands gameCommands = new GameCommands();

        //erer
        plugin.getCommand("pay").setExecutor(gameCommands);
        plugin.getCommand("money").setExecutor(gameCommands);
        plugin.getCommand("msgf").setExecutor(gameCommands);
        plugin.getCommand("r").setExecutor(gameCommands);
        plugin.getCommand("reportmsg").setExecutor(gameCommands);
        //plugin.getCommand("game").setExecutor(new GameManagercommands());;
        plugin.getCommand("buy").setExecutor(new BuyCommands(plugin));
        plugin.getCommand("hub").setExecutor(new HubCommand(plugin));
        plugin.getCommand("joinboat").setExecutor(new HubCommand(plugin));
        plugin.getCommand("vote").setExecutor(new VoteCommand(plugin));
        plugin.getCommand("psadmin").setExecutor(new PlayerAdminCommands(plugin));
        plugin.getCommand("plotadmin").setExecutor(new PlotAdminCommands(plugin));
        plugin.getCommand("bag").setExecutor(new BagCommands());


        TpaCommand tpaCommand = new TpaCommand(plugin);
        plugin.getCommand("tpa").setExecutor(tpaCommand);
        plugin.getCommand("tpahere").setExecutor(tpaCommand);
        plugin.getCommand("tpaccept").setExecutor(tpaCommand);
        plugin.getCommand("tpdeny").setExecutor(tpaCommand);

        // hologrammes du marche : chargement + apparition/rafraichissement toutes les 60s
        MarketHolograms.load(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> MarketHolograms.updateAll(plugin), 100L, 20L * 60);


        // autosave des joueurs toutes les 5 minutes (evite la perte de session si crash)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Filesave.SavePlayerserverFile(plugin);
            plugin.getLogger().info("[Autosave] Joueurs sauvegardes");
        }, 20L * 60 * 5, 20L * 60 * 5);

        // Vote - NuVotifier (softdepend)
        if (Bukkit.getPluginManager().getPlugin("Votifier") != null || Bukkit.getPluginManager().getPlugin("NuVotifier") != null) {
            plugin.getServer().getPluginManager().registerEvents(new VoteListener(plugin), plugin);
            plugin.getLogger().info("[Vote] Module NuVotifier activé — WorldEdit 30min par vote");
        } else {
            plugin.getLogger().warning("[Vote] NuVotifier non trouvé — les votes ne donneront pas de récompense");
        }
        
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);

        // tab toutes les 10 secondes
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ChatListener.updateTab(p);
            }
        }, 0L, 200L);

        // scoreboard toutes les 2 secondes
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                CreativePlotScoreboard.setscoreboardplot(p);
            }
        }, 0L, 40L);

        try {
            plugin.getCommand("raceadmin").setExecutor(new RaceAdminCommands(plugin));
            BoatRaceHologram.load(plugin);
            Bukkit.getScheduler().runTaskTimer(plugin, () -> BoatRaceHologram.update(plugin), 100L, 20L * 60);
        } catch (Exception e) {
            plugin.getLogger().severe("[BoatRace] Erreur au chargement du module course de bateaux : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void moduleModeration(FarmlandMain plugin) {
        try {
            plugin.getCommand("banf").setExecutor(new ModerationCommands(plugin));
            plugin.getCommand("kickf").setExecutor(new ModerationCommands(plugin));
            plugin.getCommand("warnf").setExecutor(new ModerationCommands(plugin));
            plugin.getCommand("unbanf").setExecutor(new ModerationCommands(plugin));
            plugin.getServer().getPluginManager().registerEvents(new LuckpermGrade(), plugin);
            messagediscord.sendmessage("Module Modération bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module Modération !");
            messagediscord.sendmessage("Module Modération erreur: " + e.toString(), "statut");
            e.printStackTrace();
        }
    }

    public static void modulePlot(FarmlandMain plugin) {
        try {
            plugin.getServer().getPluginManager().registerEvents(new EventBuildAndUse(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new Plotinventory(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new BoatRaceListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new ChatCalcListener(), plugin);

            // podium quotidien de la course de bateaux : verifie toutes les minutes si le jour a change
            Bukkit.getScheduler().runTaskTimer(plugin, () ->
                DailyBoatReward.checkAndRewardIfNewDay(plugin), 100L, 20L * 60);

            // calcul dans le chat toutes les 5 minutes
            Bukkit.getScheduler().runTaskTimer(plugin, () ->
                ChatCalcListener.lancerNouveauCalcul(plugin), 20L * 60 * 5, 20L * 60 * 5);

            // annonce aleatoire toutes les 5 minutes (jeu + Discord)
            Bukkit.getScheduler().runTaskTimer(plugin, () ->
                AnnouncementBroadcaster.broadcastRandom(plugin), 20L * 60 * 5, 20L * 60 * 5);
            plugin.getCommand("plot").setExecutor(new Plotcommands(plugin));
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module Plot !");
            messagediscord.sendmessage("Module Plot erreur: " + e.toString(), "statut");
            e.printStackTrace();
        }
    }

    public static void moduleSecureWorldEdit(FarmlandMain plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            try {
                new WorldEditSecureListener();
                plugin.getLogger().info("[WorldEdit] Module chargé avec succès.");
                messagediscord.sendmessage("Module WorldEditSecure bien lancé", "statut");
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors du chargement du module WorldEditSecure !");
                messagediscord.sendmessage("Module WorldEditSecure erreur: " + e.toString(), "statut");
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().warning("[WorldEdit] WorldEdit non installé, le module sera ignoré.");
            messagediscord.sendmessage("[WorldEdit] WorldEdit non installé, le module sera ignoré.", "statut");
        }
    }

    public static void moduleSaveCommand(FarmlandMain plugin) {
        try {
            plugin.getCommand("playerserver").setExecutor(new Savecomands(plugin));
            plugin.getCommand("saveplayer").setExecutor(new Savecomands(plugin));

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if (players.isEmpty()) {
                    plugin.getLogger().info("Sauvegarde non faite, aucun joueur connecté");
                    
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        Filesave.SavePlayerserverFile(plugin);
                       

                        
                    });
                    
                    plugin.getLogger().info("Sauvegarde réalisée pour " + players.size() + " joueurs");
                }
            }, 6000L, 6000L);

            messagediscord.sendmessage("Module SaveCommand bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module SaveCommand !");
            messagediscord.sendmessage("Module SaveCommand erreur: " + e.toString(), "statut");
            e.printStackTrace(); 
        }
    }

    public static void modulePlaceholderAPI(FarmlandMain plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                new PlaceholderApiplayerserver().register();
                plugin.getLogger().info("PlayerPlaceholderAPI registered successfully!");
                messagediscord.sendmessage("Module PlaceholderAPI bien lancé", "statut");
            } catch (Exception e) {
                plugin.getLogger().warning("Impossible de charger PlaceholderAPI : " + e.getMessage());
            }
        } else {
            plugin.getLogger().warning("PlaceholderAPI non trouvé ! Les placeholders joueurs ne fonctionneront pas.");
        }
    }
    
    

    public static void moduleWebApi(FarmlandMain plugin) {
        if (!plugin.getConfig().getBoolean("webapi.enabled", false)) {
            plugin.getLogger().info("[WebAPI] Module désactivé");
            return;
        }
        try {
            String base = plugin.getConfig().getString("webapi.base_url", "");
            String key  = plugin.getConfig().getString("webapi.api_key", "");
            long ticks  = plugin.getConfig().getLong("webapi.push_interval_seconds", 30L) * 20L;

            plugin.initWebApi(base, key);


            // push des sites de vote (une seule source de verite : le config.yml du plugin)
            plugin.getWebApi().pushVoteSites(
                plugin.getConfig().getStringList("vote.sites"),
                plugin.getConfig().getString("vote.reward", "World Edit 1 heure")
            );

            // push statut serveur toutes les X secondes
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                plugin.getWebApi().pushServerStatus(
                    Bukkit.getOnlinePlayers().size(),
                    Bukkit.getMaxPlayers(),
                    Bukkit.getBukkitVersion()
                );
            }, 20L, ticks);

            plugin.getLogger().info("[WebAPI] Module chargé → " + base);
            messagediscord.sendmessage("Module WebAPI bien lancé → " + base, "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("[WebAPI] Erreur lors du chargement du module WebAPI !");
            messagediscord.sendmessage("Module WebAPI erreur: " + e.toString(), "statut");
            e.printStackTrace();
        }
    }
    
    public static void modulemetier(FarmlandMain plugin) {
    	plugin.getServer().getPluginManager().registerEvents(new EventMineSpawn(), plugin);
    	plugin.getServer().getPluginManager().registerEvents(new HoueFarmEvent(), plugin);
    	plugin.getServer().getPluginManager().registerEvents(new HarvestFarmEvent(), plugin);
    	plugin.getServer().getPluginManager().registerEvents(new BlockPlantSecureListener(), plugin);
    	plugin.getCommand("houe").setExecutor(new FarmCommands());
    	plugin.getCommand("shop").setExecutor(new ShopCommand());
    	plugin.getServer().getPluginManager().registerEvents(new MenuListenerFarm(), plugin);
    	plugin.getLogger().info("[Metier] Module Metier activé");
    	messagediscord.sendmessage("Module Metier bien lancé ","statut");
        
    }

    public static void loadModules(FarmlandMain plugin) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            moduleModeration(plugin);
            modulePlot(plugin);
            modulePlaceholderAPI(plugin);
            moduleSaveCommand(plugin);
            moduleGame(plugin);
            moduleSecureWorldEdit(plugin);
            moduleWebApi(plugin);
            modulemetier(plugin);
        }, 20L);
    }
}