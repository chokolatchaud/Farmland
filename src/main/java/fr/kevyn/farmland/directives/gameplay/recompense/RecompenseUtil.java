package fr.kevyn.farmland.directives.gameplay.recompense;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;

import fr.kevyn.farmland.directives.infrastructure.JobType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.metiers.agriculteur.ArmesUtil;
import fr.kevyn.farmland.directives.metiers.agriculteur.HacheFarm;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.directives.metiers.tueur.epeeFarm;

import java.util.List;


public class RecompenseUtil {

    private static LevelRewardManager rewardManager;

    public static void init(LevelRewardManager manager) {
        rewardManager = manager;
    }

    public static double getBreakSpeedFarmeur(int level) {
        return 1.0 + (level * 0.01);
    }

    public static void updateFarmerBreakSpeed(
            Player player,
            PlayerServer ps
    ) {

        var attribute =
                player.getAttribute(Attribute.BLOCK_BREAK_SPEED);

        if (attribute == null) {
            return;
        }

        double speed =
                getBreakSpeedFarmeur(ps.getHoueLevel());

        attribute.setBaseValue(speed);
    }

    public static void donnerRecompenseMineur(Player joueur, PlayerServer ps, int xp, int jetons) {
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
        afficherProgression(joueur, ps, JobType.MINEUR, xp, jetons);
    }

    public static void donnerRecompenseFarmeur(Player joueur, PlayerServer ps, int xp, int jetons) {
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
            updateFarmerBreakSpeed(
                    joueur,
                    ps
            );
        }
        afficherProgression(joueur, ps, JobType.FARMEUR, xp, jetons);
    }

    public static void donnerRecompensePecheur(
            Player joueur,
            PlayerServer ps,
            int xp,
            int jetons
    ) {

        int niveauAvant = ps.getCanneLevel();
        int nouveauNiveau =
                ajouterXp(ps, "Pecheur", niveauAvant, xp);

        if (nouveauNiveau > niveauAvant) {

            ps.setCanneLevel(nouveauNiveau);

            joueur.sendMessage(
                    "§b⭐ Pêcheur niveau "
                            + nouveauNiveau + " !"
            );

            donnerRecompensesDeNiveaux(
                    joueur,
                    ps,
                    JobType.PECHEUR,
                    niveauAvant,
                    nouveauNiveau
            );
        }
        afficherProgression(joueur, ps, JobType.PECHEUR, xp, jetons);
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

        int jeton = tirerJetonsArme(nouveauNiveau);
        ps.addJetonAgriculteur(jeton);

        donnerRecompensesDeNiveaux(
                joueur,
                ps,
                JobType.AGRICULTEUR,
                niveauAvant,
                nouveauNiveau
        );
        afficherProgression(joueur, ps, JobType.AGRICULTEUR, xp, jeton);
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

            donnerRecompensesDeNiveaux(
                    joueur,
                    ps,
                    JobType.TUEUR,
                    niveauAvant,
                    nouveauNiveau
            );
        }

        int jeton = tirerJetonsArme(nouveauNiveau);
        ps.addJetonTueur(jeton);
        afficherProgression(joueur, ps, JobType.TUEUR, xp, jeton);
    }

    /**
     * Jetons de l'Agriculteur et du Tueur : même tirage que les autres métiers,
     * selon le niveau du métier dès le niveau 1 (avant : 1 jeton fixe jusqu'au niveau 13).
     * Les dégâts de l'arme restent gérés à part par ArmesUtil.
     */
    public static int tirerJetonsArme(int niveau) {
        return MultiplicateurUtil.tirerMultiplicateur(Math.max(1, niveau));
    }


    /**
     * Affiche la progression du metier en ActionBar lors d'un gain d'XP,
     * avec les jetons gagnes (plus de message dans le chat : le Farmeur en
     * mode rapide le saturait). Seuls les passages de niveau restent dans le chat.
     * L'XP stockee correspond a la progression du niveau actuel,
     * y compris lorsqu'un gain vient de faire monter le joueur de niveau.
     */
    private static void afficherProgression(Player joueur, PlayerServer ps, JobType metier, int xpGagne, int jetonsGagnes) {
        int niveau = getLevel(ps, metier);
        String cleXp = switch (metier) {
            case MINEUR -> "Mineur";
            case FARMEUR -> "Farmeur";
            case PECHEUR -> "Pecheur";
            case AGRICULTEUR -> "Agriculteur";
            case TUEUR -> "Tueur";
        };
        String nom = switch (metier) {
            case MINEUR -> "Mineur";
            case FARMEUR -> "Farmeur";
            case PECHEUR -> "Pêcheur";
            case AGRICULTEUR -> "Agriculteur";
            case TUEUR -> "Tueur";
        };
        int xpActuelle = ps.getXp(cleXp);
        int xpRequise = getXpNeeded(niveau);
        int segments = Math.min(6, Math.max(0, (int) ((long) xpActuelle * 6 / xpRequise)));

        String progression = "▰".repeat(segments) + "▱".repeat(6 - segments);
        joueur.sendActionBar(
                Component.text(nom + " Niv. " + niveau + "  ", NamedTextColor.GOLD)
                        .append(Component.text(progression, NamedTextColor.GREEN))
                        .append(Component.text("  " + xpActuelle + "/" + xpRequise, NamedTextColor.GRAY))
                        .append(Component.text("  +" + xpGagne + " XP", NamedTextColor.YELLOW))
                        .append(Component.text(texteJetons(jetonsGagnes), NamedTextColor.AQUA))
        );
    }

    /** Partie « jetons » de la barre d'action : "  +3 Jetons", "  +1 Jeton", rien si 0. */
    static String texteJetons(int jetons) {
        if (jetons <= 0) return "";
        return "  +" + jetons + (jetons > 1 ? " Jetons" : " Jeton");
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

        return rewardManager.getRewardLore(
                job,
                level
        );
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

        if (nouveauNiveau > ancienNiveau) {
            // fanfare de montée de niveau
            joueur.playSound(joueur.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1.0f);
        }

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
        rewardManager.giveRewards(
                joueur,
                ps,
                job,
                niveau
        );

    }
}
