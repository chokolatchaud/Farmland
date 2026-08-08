package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.playerserver.PlayerServer;

/**
 * Point central des recompenses de metier. Chaque action legitime (recolte,
 * kill, peche, minage) donne :
 *   - de l'XP, GRATUIT, qui fait monter automatiquement le niveau de l'outil,
 *     SANS PLAFOND (niveaux infinis)
 *   - des JETONS, la monnaie VENDABLE du metier, stockee dans le /bag
 *
 * Farmeur/Pecheur/Mineur : le niveau ameliore directement les chances de
 * "duplique" (plusieurs Jetons d'un coup, voir MultiplicateurUtil).
 *
 * Agriculteur/Tueur (armes) : le niveau augmente les DEGATS jusqu'a un
 * plafond de 20 (niveau 15). Au-dela, le niveau continue de monter mais
 * n'ajoute plus de degats - il alimente a la place le MEME systeme de
 * multiplicateur de duplique que les 3 autres metiers.
 *
 * Plus de /upgradeX payant : toute la progression vient desormais
 * uniquement de l'XP gagnee en jouant.
 */
public class RecompenseUtil {

    // ===== Jetons - un item distinct par metier, sert juste d'icone dans le /bag =====
    public static final Material JETON_MINEUR = Material.IRON_NUGGET;
    public static final Material JETON_FARMEUR = Material.WHEAT;
    public static final Material JETON_PECHEUR = Material.PRISMARINE_SHARD;
    public static final Material JETON_AGRICULTEUR = Material.LEATHER;
    public static final Material JETON_TUEUR = Material.GUNPOWDER;

    public static void donnerRecompenseMineur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
        int niveauAvant = ps.getCobblestonegeneratorlevel();
        int nouveauNiveau = ajouterXp(ps, "Mineur", niveauAvant, xp);
        if (nouveauNiveau > niveauAvant) {
            ps.setCobblestonegeneratorlevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Mineur niveau " + nouveauNiveau + " !");
        }
        int jeton = MultiplicateurUtil.tirerMultiplicateur(Math.max(1, nouveauNiveau)) * jetonBase;
        ps.addRessource(JETON_MINEUR, jeton);
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
        ps.addRessource(JETON_FARMEUR, jeton);
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
        ps.addRessource(JETON_PECHEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Pêcheur");
    }

    public static void donnerRecompenseAgriculteur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
        int niveauAvant = ps.getHacheLevel();
        int nouveauNiveau = ajouterXp(ps, "Agriculteur", niveauAvant, xp);

        if (nouveauNiveau > niveauAvant) {
            ps.setHacheLevel(nouveauNiveau);
            ItemStack hacheEnMain = joueur.getInventory().getItemInMainHand();
            if (Outils.isOutilsAttendu(hacheEnMain, org.bukkit.Material.NETHERITE_AXE)) {
                fr.kevyn.farmland.agriculteur.HacheFarm.appliquerDegats(hacheEnMain,
                    fr.kevyn.farmland.agriculteur.ArmesUtil.calculerDegats(nouveauNiveau));
            }
            joueur.sendMessage("§b⭐ Agriculteur niveau " + nouveauNiveau + " !");
        }

        int niveauExcedent = fr.kevyn.farmland.agriculteur.ArmesUtil.niveauExcedentaire(nouveauNiveau);
        int jeton = jetonBase * (niveauExcedent > 0 ? MultiplicateurUtil.tirerMultiplicateur(niveauExcedent) : 1);
        ps.addRessource(JETON_AGRICULTEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Agriculteur");
    }

    public static void donnerRecompenseTueur(Player joueur, PlayerServer ps, int xp, int jetonBase) {
        int niveauAvant = ps.getEpeeLevel();
        int nouveauNiveau = ajouterXp(ps, "Tueur", niveauAvant, xp);

        if (nouveauNiveau > niveauAvant) {
            ps.setEpeeLevel(nouveauNiveau);
            ItemStack epeeEnMain = joueur.getInventory().getItemInMainHand();
            if (Outils.isOutilsAttendu(epeeEnMain, org.bukkit.Material.NETHERITE_SWORD)) {
                fr.kevyn.farmland.tueur.epeeFarm.appliquerDegats(epeeEnMain,
                    fr.kevyn.farmland.agriculteur.ArmesUtil.calculerDegats(nouveauNiveau));
            }
            joueur.sendMessage("§b⭐ Tueur niveau " + nouveauNiveau + " !");
        }

        int niveauExcedent = fr.kevyn.farmland.agriculteur.ArmesUtil.niveauExcedentaire(nouveauNiveau);
        int jeton = jetonBase * (niveauExcedent > 0 ? MultiplicateurUtil.tirerMultiplicateur(niveauExcedent) : 1);
        ps.addRessource(JETON_TUEUR, jeton);
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
