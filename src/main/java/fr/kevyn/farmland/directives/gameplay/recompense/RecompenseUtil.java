package fr.kevyn.farmland.directives.gameplay.recompense;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.directives.infrastructure.JobType;
import fr.kevyn.farmland.directives.metiers.farmeur.HoueFarmeur;
import fr.kevyn.farmland.directives.metiers.mineur.PiocheFarm;
import fr.kevyn.farmland.directives.metiers.pecheur.PecheurFarm;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.metiers.agriculteur.ArmesUtil;
import fr.kevyn.farmland.directives.metiers.agriculteur.HacheFarm;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.directives.metiers.tueur.epeeFarm;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class RecompenseUtil {

    public static void donnerRecompenseMineur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getCobblestonegeneratorlevel();
        int nouveauNiveau = ajouterXp(ps, "Mineur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {

            ps.setCobblestonegeneratorlevel(nouveauNiveau);

            joueur.sendMessage(
                    "§b⭐ Mineur niveau " + nouveauNiveau + " !"
            );

            donnerRecompensesDeNiveaux(
                    joueur,
                    ps,
                    JobType.MINEUR,
                    niveauAvant,
                    nouveauNiveau
            );
        }
    }

    public static void donnerRecompenseFarmeur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getHoueLevel();
        int nouveauNiveau = ajouterXp(ps, "Farmeur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {

            ps.setHoueLevel(nouveauNiveau);

            joueur.sendMessage(
                    "§b⭐ Farmeur niveau " + nouveauNiveau + " !"
            );

            donnerRecompensesDeNiveaux(
                    joueur,
                    ps,
                    JobType.FARMEUR,
                    niveauAvant,
                    nouveauNiveau
            );
        }
    }

    public static void donnerRecompensePecheur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getCanneLevel();
        int nouveauNiveau = ajouterXp(ps, "Pecheur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setCanneLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Pêcheur niveau " + nouveauNiveau + " !");
        }
        donnerRecompensesDeNiveaux(
                joueur,
                ps,
                JobType.PECHEUR,
                niveauAvant,
                nouveauNiveau
        );
    }

    public static void donnerRecompenseAgriculteur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getHacheLevel();
        int nouveauNiveau = ajouterXp(ps, "Agriculteur", niveauAvant, xp);

        if (nouveauNiveau > niveauAvant) {
            ps.setHacheLevel(nouveauNiveau);
            ItemStack hacheEnMain = joueur.getInventory().getItemInMainHand();
            if (Outils.isOutilsAttendu(hacheEnMain, org.bukkit.Material.NETHERITE_AXE)) {
                HacheFarm.appliquerDegats(hacheEnMain,
                    ArmesUtil.calculerDegats(nouveauNiveau));
            }
            joueur.sendMessage("§b⭐ Agriculteur niveau " + nouveauNiveau + " !");
        }

        int niveauExcedent = ArmesUtil.niveauExcedentaire(nouveauNiveau);
        int jeton = (niveauExcedent > 0 ? MultiplicateurUtil.tirerMultiplicateur(niveauExcedent) : 1);
        ps.addJetonAgriculteur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Agriculteur");
    }

    public static void donnerRecompenseTueur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getEpeeLevel();
        int nouveauNiveau = ajouterXp(ps, "Tueur", niveauAvant, xp);

        if (nouveauNiveau > niveauAvant) {
            ps.setEpeeLevel(nouveauNiveau);
            ItemStack epeeEnMain = joueur.getInventory().getItemInMainHand();
            if (Outils.isOutilsAttendu(epeeEnMain, Material.NETHERITE_SWORD)) {
                epeeFarm.appliquerDegats(epeeEnMain,
                    ArmesUtil.calculerDegats(nouveauNiveau));
            }
            joueur.sendMessage("§b⭐ Tueur niveau " + nouveauNiveau + " !");
        }

        int niveauExcedent = ArmesUtil.niveauExcedentaire(nouveauNiveau);
        int jeton = (niveauExcedent > 0 ? MultiplicateurUtil.tirerMultiplicateur(niveauExcedent) : 1);
        ps.addJetonTueur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Tueur");
    }


    private static int ajouterXp(PlayerServer ps, String metier, int niveauActuel, int xpGagne) {
        int xpTotal = ps.getXp(metier) + xpGagne;
        int seuil = getXpNeeded(niveauActuel);

        int niveau = niveauActuel;
        while (xpTotal >= seuil) {
            xpTotal -= seuil;
            niveau++;
            seuil = getXpNeeded(niveau);
        }

        ps.setXp(metier, xpTotal);
        return niveau;
    }

    public static int getXpNeeded(int level) {
        return 100 * (level + 1);
    }

    public static List<String> getRewardLore(
            JobType job,
            int level
    ) {

        List<String> lore = new ArrayList<>();

        lore.add("§6Récompenses :");

        switch (level) {

            case 10 ->
                    lore.add("§e+1 000 $FB");

            case 20 ->
                    lore.add("§e+2 500 $FB");

            case 30 ->
                    lore.add(
                            "§dObjet spécial "
                                    + getJobDisplayName(job)
                    );

            case 40 ->
                    lore.add("§e+5 000 $FB");

            case 50 ->
                    lore.add("§bBeacon spécial du métier");

            default ->
                    lore.add("§7Amélioration de la progression");
        }

        lore.add("§7Meilleur potentiel de jetons");

        return lore;
    }

    public static int getLevel(PlayerServer ps, JobType job) {
        return switch (job) {
            case MINEUR -> ps.getCobblestonegeneratorlevel();
            case FARMEUR -> ps.getHoueLevel();
            case PECHEUR -> ps.getCanneLevel();
            case AGRICULTEUR -> ps.getHacheLevel();
            case TUEUR -> ps.getEpeeLevel();
        };
    }

    private static void donnerRecompensesDeNiveaux(
            Player joueur,
            PlayerServer ps,
            JobType job,
            int ancienNiveau,
            int nouveauNiveau
    ) {

        for (int niveau = ancienNiveau + 1;
             niveau <= nouveauNiveau;
             niveau++) {

            donnerRecompenseNiveau(
                    joueur,
                    ps,
                    job,
                    niveau
            );
        }
    }

    private static void donnerRecompenseNiveau(
            Player joueur,
            PlayerServer ps,
            JobType job,
            int niveau
    ) {
        switch (niveau) {

            case 10 -> donnerArgent(joueur, ps, 1000);

            case 20 -> donnerArgent(joueur, ps, 2500);

            case 30 -> donnerObjetMetier(joueur, job);

            case 40 -> donnerArgent(joueur, ps, 5000);

            case 50 -> {
                joueur.give(new ItemStack(Material.BEACON));

                joueur.sendMessage(
                        "§6★ Récompense niveau 50 : §eBeacon de métier !"
                );
            }

            default -> {
                // Pas de récompense spéciale à ce niveau
            }
        }

    }
    private static void donnerArgent(
            Player joueur,
            PlayerServer ps,
            int montant
    ) {

        ps.setMoney(
                ps.getMoney() + montant
        );

        joueur.sendMessage(
                "§6★ Récompense de niveau : §e+"
                        + montant
                        + " $FB"
        );
    }

    private static void donnerObjetMetier(
            Player joueur,
            JobType job
    ) {

        ItemStack item = switch (job) {

            case MINEUR -> PiocheFarm.create();

            case FARMEUR -> HoueFarmeur.create();

            case PECHEUR -> PecheurFarm.create();

            case AGRICULTEUR -> HacheFarm.createHache();

            case TUEUR -> epeeFarm.create();
        };

        personnaliserObjetNiveau30(item, job);

        joueur.give(item);

        joueur.sendMessage(
                "§6★ §eTu as reçu ton objet spécial niveau 30 !"
        );
    }

    private static String getJobDisplayName(JobType job) {
        return switch (job) {
            case MINEUR -> "du Mineur";
            case FARMEUR -> "du Farmeur";
            case PECHEUR -> "du Pêcheur";
            case AGRICULTEUR -> "de l'Agriculteur";
            case TUEUR -> "du Tueur";
        };
    }

    private static void personnaliserObjetNiveau30(
            ItemStack item,
            JobType job
    ) {

        ItemMeta meta = item.getItemMeta();

        String nom = switch (job) {

            case MINEUR ->
                    "§b⛏ Pioche du Mineur Expérimenté";

            case FARMEUR ->
                    "§a🌾 Houe du Farmeur Expérimenté";

            case PECHEUR ->
                    "§3🎣 Canne du Pêcheur Expérimenté";

            case AGRICULTEUR ->
                    "§6🪓 Hache de l'Agriculteur Expérimenté";

            case TUEUR ->
                    "§c⚔ Lame du Tueur Expérimenté";
        };

        meta.setDisplayName(nom);

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("§7Récompense de métier");
        lore.add("§6Niveau 30");
        lore.add("");
        lore.add("§8Objet unique de progression");

        meta.setLore(lore);

        item.setItemMeta(meta);

        Outils.setRewardLevel(item, 30);
    }
}
