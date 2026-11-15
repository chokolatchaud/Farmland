package fr.kevyn.farmland.directives.metiers;

import fr.kevyn.farmland.directives.gameplay.recompense.LevelRewardManager;
import fr.kevyn.farmland.directives.infrastructure.JobType;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestRewardCommand implements CommandExecutor {

    private final LevelRewardManager rewardManager;

    public TestRewardCommand(LevelRewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande doit être utilisée en jeu.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(
                    "§cUsage : /testreward <metier> <niveau>"
            );
            return true;
        }

        JobType job;

        try {
            job = JobType.valueOf(
                    args[0].toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            player.sendMessage(
                    "§cMétier inconnu."
            );

            player.sendMessage(
                    "§7MINEUR, FARMEUR, PECHEUR, AGRICULTEUR, TUEUR"
            );

            return true;
        }

        int level;

        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(
                    "§cLe niveau doit être un nombre."
            );
            return true;
        }

        PlayerServer ps =
                PlayerserverHashMap.getInstance()
                        .getplayerHaspMaps(
                                player.getUniqueId()
                        );

        if (ps == null) {
            player.sendMessage(
                    "§cImpossible de trouver tes données joueur."
            );
            return true;
        }

        player.sendMessage(
                "§7Test de la récompense §e"
                        + job.name()
                        + " §7niveau §e"
                        + level
        );

        rewardManager.giveRewards(
                player,
                ps,
                job,
                level
        );

        return true;
    }
}