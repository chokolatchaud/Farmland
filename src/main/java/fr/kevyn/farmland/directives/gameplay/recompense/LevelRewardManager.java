package fr.kevyn.farmland.directives.gameplay.recompense;

import fr.kevyn.farmland.directives.infrastructure.JobType;
import fr.kevyn.farmland.directives.infrastructure.journal.JournalEvenements;
import fr.kevyn.farmland.directives.metiers.agriculteur.HacheFarm;
import fr.kevyn.farmland.directives.metiers.farmeur.HoueFarmeur;
import fr.kevyn.farmland.directives.metiers.mineur.PiocheFarm;
import fr.kevyn.farmland.directives.metiers.pecheur.PecheurFarm;
import fr.kevyn.farmland.directives.metiers.tueur.epeeFarm;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelRewardManager {

    private final FileConfiguration rewardsConfig;

    public LevelRewardManager(FileConfiguration rewardsConfig) {
        this.rewardsConfig = rewardsConfig;
    }

    public void giveRewards(
            Player player,
            PlayerServer ps,
            JobType job,
            int level
    ) {

        giveRewardsFromSection(
                player,
                ps,
                job,
                level,
                "rewards.all." + level
        );

        giveRewardsFromSection(
                player,
                ps,
                job,
                level,
                "rewards." + job.name() + "." + level
        );
    }

    private void giveRewardsFromSection(
            Player player,
            PlayerServer ps,
            JobType job,
            int level,
            String path
    ) {

        List<Map<?, ?>> rewards =
                rewardsConfig.getMapList(path);

        for (Map<?, ?> reward : rewards) {

            Object typeObject = reward.get("type");

            if (typeObject == null) {
                continue;
            }

            String type =
                    typeObject.toString().toUpperCase();

            switch (type) {

                case "MONEY" ->
                        giveMoney(player, ps, reward);

                case "ITEM" ->
                        giveItem(player, reward);

                case "JOB_ITEM" ->
                        giveJobItem(player, job, level);

                default ->
                        player.sendMessage(
                                "§cType de récompense inconnu : "
                                        + type
                        );
            }
        }
    }

    private void giveMoney(
            Player player,
            PlayerServer ps,
            Map<?, ?> reward
    ) {

        Object amountObject = reward.get("amount");

        if (!(amountObject instanceof Number amountNumber)) {
            return;
        }

        int amount = amountNumber.intValue();

        ps.setMoney(
                ps.getMoney() + amount
        );

        JournalEvenements.enregistrer(
                "recompense_niveau",
                player.getName(),
                "+" + amount
                        + " $FB, solde "
                        + ps.getMoney()
                        + " $FB"
        );

        player.sendMessage(
                "§6★ Récompense de niveau : §e+"
                        + amount
                        + " $FB"
        );
    }

    private void giveJobItem(
            Player player,
            JobType job,
            int level
    ) {

        ItemStack item = switch (job) {

            case MINEUR ->
                    PiocheFarm.create();

            case FARMEUR ->
                    HoueFarmeur.create();

            case PECHEUR ->
                    PecheurFarm.create();

            case AGRICULTEUR ->
                    HacheFarm.createHache();

            case TUEUR ->
                    epeeFarm.create();
        };

        player.give(item);

        player.sendMessage(
                "§6★ §eTu as reçu un objet spécial "
                        + job.name()
                        + " niveau "
                        + level
                        + " !"
        );
    }

    public List<String> getRewardLore(
            JobType job,
            int level
    ) {

        List<String> lore = new ArrayList<>();

        lore.add("§6Récompenses :");

        addRewardLoreFromSection(
                lore,
                job,
                level,
                "rewards.all." + level
        );

        addRewardLoreFromSection(
                lore,
                job,
                level,
                "rewards." + job.name() + "." + level
        );

        if (lore.size() == 1) {
            lore.add("§7Aucune récompense spéciale");
        }

        return lore;
    }

    private void addRewardLoreFromSection(
            List<String> lore,
            JobType job,
            int level,
            String path
    ) {

        List<Map<?, ?>> rewards =
                rewardsConfig.getMapList(path);

        for (Map<?, ?> reward : rewards) {

            Object typeObject = reward.get("type");

            if (typeObject == null) {
                continue;
            }

            String type =
                    typeObject.toString().toUpperCase();

            switch (type) {

                case "MONEY" -> {

                    Object amountObject =
                            reward.get("amount");

                    if (amountObject instanceof Number number) {

                        int amount =
                                number.intValue();

                        lore.add(
                                "§e+" + amount + " $FB"
                        );
                    }
                }

                case "ITEM" -> {

                    Object materialObject =
                            reward.get("material");

                    if (materialObject == null) {
                        continue;
                    }

                    int amount = 1;

                    Object amountObject =
                            reward.get("amount");

                    if (amountObject instanceof Number number) {
                        amount = number.intValue();
                    }

                    lore.add(
                            "§b" + amount + "x "
                                    + materialObject
                                    .toString()
                                    .toLowerCase()
                                    .replace("_", " ")
                    );
                }

                case "JOB_ITEM" ->
                        lore.add(
                                "§dObjet spécial "
                                        + getJobDisplayName(job)
                        );
            }
        }
    }

    private String getJobDisplayName(JobType job) {

        return switch (job) {

            case MINEUR ->
                    "du Mineur";

            case FARMEUR ->
                    "du Farmeur";

            case PECHEUR ->
                    "du Pêcheur";

            case AGRICULTEUR ->
                    "de l'Agriculteur";

            case TUEUR ->
                    "du Tueur";
        };
    }
}