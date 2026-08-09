package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.agriculteur.ArmesUtil;
import fr.kevyn.farmland.agriculteur.HacheFarm;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.tueur.epeeFarm;


public class RecompenseUtil {

    public static void donnerRecompenseMineur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
        int niveauAvant = ps.getCobblestonegeneratorlevel();
        int nouveauNiveau = ajouterXp(ps, "Mineur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setCobblestonegeneratorlevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Mineur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau)) * jetonBase;
        ps.addJetonMineur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Mineur");
    }

    public static void donnerRecompenseFarmeur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
        int niveauAvant = ps.getHoueLevel();
        int nouveauNiveau = ajouterXp(ps, "Farmeur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setHoueLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Farmeur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau)) * jetonBase;
        ps.addJetonFarmeur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Farmeur");
    }

    public static void donnerRecompensePecheur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
        int niveauAvant = ps.getCanneLevel();
        int nouveauNiveau = ajouterXp(ps, "Pecheur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setCanneLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Pêcheur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau)) * jetonBase;
        ps.addJetonPecheur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Pêcheur");
    }

    public static void donnerRecompenseAgriculteur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
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
        int jeton = jetonBase * (niveauExcedent > 0 ? MultiplicateurUtil.tirerMultiplicateur(niveauExcedent) : 1);
        ps.addJetonAgriculteur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Agriculteur");
    }

    public static void donnerRecompenseTueur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
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
        int jeton = jetonBase * (niveauExcedent > 0 ? MultiplicateurUtil.tirerMultiplicateur(niveauExcedent) : 1);
        ps.addJetonTueur(jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Tueur");
    }

    /** Ajoute l'XP, renvoie le NOUVEAU niveau (identique a l'ancien si pas de palier franchi). Niveaux INFINIS. */
    private static int ajouterXp(PlayerServer ps, String metier, int niveauActuel, int xpGagne) {
        int xpTotal = ps.getXp(metier) + xpGagne;
        int seuil = 100 * (niveauActuel + 1);

        int niveau = niveauActuel;
        while (xpTotal >= seuil) {
            xpTotal -= seuil;
            niveau++;
            seuil = 100 * (niveau + 1);
        }

        ps.setXp(metier, xpTotal);
        return niveau;
    }
}
