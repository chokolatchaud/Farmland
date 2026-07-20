package fr.kevyn.farmland;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.EventBuild.ChatListener;
import fr.kevyn.farmland.EventBuild.EventBuildAndUse;
import fr.kevyn.farmland.EventBuild.LuckpermGrade;
import fr.kevyn.farmland.EventBuild.Plotinventory;
import fr.kevyn.farmland.game.GameCommands;
import fr.kevyn.farmland.market.DonateMoneyForStructure;
import fr.kevyn.farmland.market.MarketCalc;
import fr.kevyn.farmland.market.Marketcommands;
import fr.kevyn.farmland.moderation.ModerationCommands;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.region.RegionCommands;
import fr.kevyn.farmland.save.Filesave;
import fr.kevyn.farmland.save.RegionSave;
import fr.kevyn.farmland.scoreboard.CreativePlotScoreboard;
import fr.kevyn.farmland.structure.Definecommands;
import fr.kevyn.farmland.structure.GetStructure;
import fr.kevyn.farmland.structure.StructureCommands;
import fr.kevyn.farmland.worldeditgestion.WorldEditSecureListener;
import fr.kevyn.plot.Plotcommands;

public class MicroPluginManager {

    public static void moduleGame(FarmlandMain plugin) {
    	
    	int timecalculateMarket = 20 *60 *60 *24 * 1; //1 jour
    	int timeDonateMoneyStructure = 20 * 60 * 60; //1 heure

        GameCommands gameCommands = new GameCommands();

        //erer
        plugin.getCommand("pay").setExecutor(gameCommands);
        plugin.getCommand("money").setExecutor(gameCommands);
        plugin.getCommand("msgf").setExecutor(gameCommands);
        plugin.getCommand("r").setExecutor(gameCommands);
        plugin.getCommand("reportmsg").setExecutor(gameCommands);
        plugin.getCommand("createregion").setExecutor(new RegionCommands());
        plugin.getCommand("listregion").setExecutor(new RegionCommands());
        plugin.getCommand("region").setExecutor(new RegionCommands());
        //plugin.getCommand("game").setExecutor(new GameManagercommands());
        plugin.getCommand("liststructure").setExecutor(new StructureCommands(plugin));
        plugin.getCommand("viewmoney").setExecutor(new StructureCommands(plugin));
        plugin.getCommand("define").setExecutor(new Definecommands());
        plugin.getCommand("undefine").setExecutor(new Definecommands());
        plugin.getCommand("market").setExecutor(new Marketcommands());
        plugin.getCommand("recalcmarket").setExecutor(new Marketcommands());
        plugin.getCommand("buy").setExecutor(new fr.kevyn.farmland.market.BuyCommands(plugin));
        plugin.getCommand("tuto").setExecutor(new fr.kevyn.farmland.TutoCommand());
        plugin.getCommand("hub").setExecutor(new fr.kevyn.farmland.game.HubCommand(plugin));
        plugin.getCommand("joinboat").setExecutor(new fr.kevyn.farmland.game.HubCommand(plugin));
        plugin.getCommand("vote").setExecutor(new fr.kevyn.farmland.vote.VoteCommand(plugin));
        plugin.getCommand("marketadmin").setExecutor(new fr.kevyn.farmland.market.MarketAdminCommands(plugin));
        plugin.getCommand("psadmin").setExecutor(new fr.kevyn.farmland.playerserver.PlayerAdminCommands(plugin));

        // hologrammes du marche : chargement + apparition/rafraichissement toutes les 60s
        fr.kevyn.farmland.market.MarketHolograms.load(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> fr.kevyn.farmland.market.MarketHolograms.updateAll(plugin), 100L, 20L * 60);

        // autosave des joueurs toutes les 5 minutes (evite la perte de session si crash)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            fr.kevyn.farmland.save.Filesave.SavePlayerserverFile(plugin);
            plugin.getLogger().info("[Autosave] Joueurs sauvegardes");
        }, 20L * 60 * 5, 20L * 60 * 5);

        // Vote - NuVotifier (softdepend)
        if (Bukkit.getPluginManager().getPlugin("Votifier") != null || Bukkit.getPluginManager().getPlugin("NuVotifier") != null) {
            plugin.getServer().getPluginManager().registerEvents(new fr.kevyn.farmland.vote.VoteListener(plugin), plugin);
            plugin.getLogger().info("[Vote] Module NuVotifier activé — WorldEdit 30min par vote");
        } else {
            plugin.getLogger().warning("[Vote] NuVotifier non trouvé — les votes ne donneront pas de récompense");
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
        	MarketCalc.Calcforcoef(plugin);
        	
            
            }, timecalculateMarket, timecalculateMarket);//1 jours
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
        	DonateMoneyForStructure.AllPlayer(plugin);

            }, timeDonateMoneyStructure, timeDonateMoneyStructure);//15min
        
        
        
        




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

        // module course de bateaux : protege par try-catch pour qu'une erreur ici
        // ne puisse plus jamais bloquer l'enregistrement des systemes au-dessus
        // (scoreboard, tab, market, vote...) comme cela avait casse le scoreboard
        try {
            plugin.getCommand("raceadmin").setExecutor(new fr.kevyn.farmland.boathub.RaceAdminCommands(plugin));
            fr.kevyn.farmland.boathub.BoatRaceHologram.load(plugin);
            Bukkit.getScheduler().runTaskLater(plugin, () -> fr.kevyn.farmland.boathub.BoatRaceHologram.update(plugin), 100L);
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
            plugin.getServer().getPluginManager().registerEvents(new fr.kevyn.farmland.boathub.BoatRaceListener(), plugin);
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
                        RegionSave.saveAllRegions(plugin);
                        // -----------------------------------------------------------------------

                        
                    });
                    for (Player player : players) {
                        player.sendMessage(
                            net.kyori.adventure.text.Component.text("✦ Rejoins le Discord de Farmland : ", net.kyori.adventure.text.format.NamedTextColor.GOLD)
                                .append(net.kyori.adventure.text.Component.text("discord.gg/VH7MJpwpub", net.kyori.adventure.text.format.NamedTextColor.AQUA, net.kyori.adventure.text.format.TextDecoration.UNDERLINED)
                                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl("https://discord.gg/VH7MJpwpub")))
                        );
                    }
                    plugin.getLogger().info("Sauvegarde réalisée pour " + players.size() + " joueurs");
                }
            }, 6000L, 6000L);

            messagediscord.sendmessage("Module SaveCommand bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module SaveCommand !");
            messagediscord.sendmessage("Module SaveCommand erreur: " + e.toString(), "statut");
            e.printStackTrace(); //test
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
            plugin.getLogger().info("[WebAPI] Module désactivé (webapi.enabled=false dans config.yml)");
            return;
        }
        try {
            String base = plugin.getConfig().getString("webapi.base_url", "");
            String key  = plugin.getConfig().getString("webapi.api_key", "");
            long ticks  = plugin.getConfig().getLong("webapi.push_interval_seconds", 30L) * 20L;
            long leaderboardTicks = plugin.getConfig().getLong("webapi.leaderboard_push_minutes", 15L) * 60L * 20L;

            plugin.initWebApi(base, key);

            // push initial du marche au demarrage
            MarketCalc.pushMarketToWebApi(plugin);

            // push des sites de vote (une seule source de verite : le config.yml du plugin)
            plugin.getWebApi().pushVoteSites(
                plugin.getConfig().getStringList("vote.sites"),
                plugin.getConfig().getString("vote.reward", "+15 $FB")
            );

            // push leaderboard au demarrage + toutes les webapi.leaderboard_push_minutes (defaut 15min)
            // tous les joueurs en memoire (pas seulement les connectes)
            Runnable pushLeaderboard = () -> {
                // copie de la liste : on itère en async pendant que /define peut la modifier
                java.util.ArrayList<GameRegion> structures = new java.util.ArrayList<>(GetStructure.getallStructure());
                for (PlayerServer ps : PlayerserverHashMap.getInstance().getHashMapPlayer().values()) {
                    int nbStructures = 0;
                    for (GameRegion r : structures) {
                        if (r.getPropriétaire().equals(ps.getUuid())) nbStructures++;
                    }
                    plugin.getWebApi().pushPlayerBalance(ps.getName(), ps.getMoney(), nbStructures, ps.getBlocposetotal());
                }
                plugin.getLogger().info("[WebAPI] Leaderboard pousse → " + PlayerserverHashMap.getInstance().getHashMapPlayer().size() + " joueur(s)");
            };

            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, pushLeaderboard, 20L, leaderboardTicks);

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

    public static void loadModules(FarmlandMain plugin) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            moduleModeration(plugin);
            modulePlot(plugin);
            modulePlaceholderAPI(plugin);
            moduleSaveCommand(plugin);
            moduleGame(plugin);
            moduleSecureWorldEdit(plugin);
            moduleWebApi(plugin);
        }, 20L);
    }
}