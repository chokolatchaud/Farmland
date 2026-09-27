package fr.kevyn.farmland.directives.gameplay.recompense;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.directives.infrastructure.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.directives.metiers.agriculteur.ArmesUtil;
import fr.kevyn.farmland.directives.metiers.agriculteur.HacheFarm;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.directives.metiers.tueur.epeeFarm;

import java.util.ArrayList;
import java.util.List;


public class RecompenseUtil {

    public static void donnerRecompenseMineur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getCobblestonegeneratorlevel();
        int nouveauNiveau = ajouterXp(ps, "Mineur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setCobblestonegeneratorlevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Mineur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau));
        ps.addJetonMineur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Mineur");
    }

    public static void donnerRecompenseFarmeur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getHoueLevel();
        int nouveauNiveau = ajouterXp(ps, "Farmeur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setHoueLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Farmeur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau));
        ps.addJetonFarmeur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Farmeur");
    }

    public static void donnerRecompensePecheur(Player joueur, PlayerServer ps, int xp) {
        int niveauAvant = ps.getCanneLevel();
        int nouveauNiveau = ajouterXp(ps, "Pecheur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setCanneLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Pêcheur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau));
        ps.addJetonPecheur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Pêcheur");
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

    public static List<String> getRewardLore(JobType job, int level) {

        List<String> lore = new ArrayList<>();

        lore.add("§6Récompenses :");

        if (level == 10) {
            lore.add("§7- §e1 000 $FB");
        }

        if (level == 20) {
            lore.add("§7- §e2 500 $FB");
        }

        if (level == 30) {
            lore.add("§7- §dObjet spécial du métier");
        }

        lore.add("§7- Meilleur potentiel de jetons");

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
}
