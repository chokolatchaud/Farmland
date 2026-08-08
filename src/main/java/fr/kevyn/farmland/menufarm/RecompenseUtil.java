package fr.kevyn.farmland.menufarm;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.kevyn.farmland.playerserver.PlayerServer;

/**
 * Point central des recompenses de metier. Chaque action legitime (recolte,
 * kill, peche, minage) donne :
 *   - de l'XP, GRATUIT, qui fait monter automatiquement le niveau de l'outil
 *     (meme niveau que celui utilise par ArmesUtil/MultiplicateurUtil/le
 *     Cobblegenerator - un seul systeme de niveau, plus de /upgradeX payant
 *     necessaire pour la progression de base)
 *   - des JETONS, la monnaie VENDABLE du metier, stockee dans le /bag comme
 *     n'importe quelle ressource (reutilise ps.ressources), vendue via le
 *     marche existant (MarketCalc)
 *
 * Le seuil d'XP necessaire pour passer au niveau superieur augmente a
 * chaque niveau (100 * (niveau+1)), plafonne a 10 comme le reste du
 * systeme de niveaux.
 */
public class RecompenseUtil {

    private static final int NIVEAU_MAX = 10;

    // ===== Jetons - un item distinct par metier, sert juste d'icone dans le /bag =====
    public static final Material JETON_MINEUR = Material.IRON_NUGGET;
    public static final Material JETON_FARMEUR = Material.WHEAT;
    public static final Material JETON_PECHEUR = Material.PRISMARINE_SHARD;
    public static final Material JETON_AGRICULTEUR = Material.LEATHER;
    public static final Material JETON_TUEUR = Material.GUNPOWDER;

    public static void donnerRecompenseMineur(Player joueur, PlayerServer ps, int xp, int jeton) {
        int nouveauNiveau = ajouterXp(ps, "Mineur", ps.getCobblestonegeneratorlevel(), xp);
        if (nouveauNiveau > ps.getCobblestonegeneratorlevel()) {
            ps.setCobblestonegeneratorlevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Mineur niveau " + nouveauNiveau + " !");
        }
        ps.addRessource(JETON_MINEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Mineur");
    }

    public static void donnerRecompenseFarmeur(Player joueur, PlayerServer ps, int xp, int jeton) {
        int nouveauNiveau = ajouterXp(ps, "Farmeur", ps.getHoueLevel(), xp);
        if (nouveauNiveau > ps.getHoueLevel()) {
            ps.setHoueLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Farmeur niveau " + nouveauNiveau + " !");
        }
        ps.addRessource(JETON_FARMEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Farmeur");
    }

    public static void donnerRecompensePecheur(Player joueur, PlayerServer ps, int xp, int jeton) {
        int nouveauNiveau = ajouterXp(ps, "Pecheur", ps.getCanneLevel(), xp);
        if (nouveauNiveau > ps.getCanneLevel()) {
            ps.setCanneLevel(nouveauNiveau);
            joueur.sendMessage("§b⭐ Pêcheur niveau " + nouveauNiveau + " !");
        }
        ps.addRessource(JETON_PECHEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Pêcheur");
    }

    public static void donnerRecompenseAgriculteur(Player joueur, PlayerServer ps, int xp, int jeton) {
        int nouveauNiveau = ajouterXp(ps, "Agriculteur", ps.getHacheLevel(), xp);
        if (nouveauNiveau > ps.getHacheLevel()) {
            ps.setHacheLevel(nouveauNiveau);
            ItemStack hacheEnMain = joueur.getInventory().getItemInMainHand();
            if (Outils.isOutilsAttendu(hacheEnMain, org.bukkit.Material.NETHERITE_AXE)) {
                fr.kevyn.farmland.agriculteur.HacheFarm.appliquerDegats(hacheEnMain,
                    fr.kevyn.farmland.agriculteur.ArmesUtil.calculerDegats(nouveauNiveau));
            }
            joueur.sendMessage("§b⭐ Agriculteur niveau " + nouveauNiveau + " !");
        }
        ps.addRessource(JETON_AGRICULTEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Agriculteur");
    }

    public static void donnerRecompenseTueur(Player joueur, PlayerServer ps, int xp, int jeton) {
        int nouveauNiveau = ajouterXp(ps, "Tueur", ps.getEpeeLevel(), xp);
        if (nouveauNiveau > ps.getEpeeLevel()) {
            ps.setEpeeLevel(nouveauNiveau);
            ItemStack epeeEnMain = joueur.getInventory().getItemInMainHand();
            if (Outils.isOutilsAttendu(epeeEnMain, org.bukkit.Material.NETHERITE_SWORD)) {
                fr.kevyn.farmland.tueur.epeeFarm.appliquerDegats(epeeEnMain,
                    fr.kevyn.farmland.agriculteur.ArmesUtil.calculerDegats(nouveauNiveau));
            }
            joueur.sendMessage("§b⭐ Tueur niveau " + nouveauNiveau + " !");
        }
        ps.addRessource(JETON_TUEUR, jeton);
        joueur.sendMessage("§a+" + xp + " XP §7| §e+" + jeton + " Jeton Tueur");
    }

    /** Ajoute l'XP, renvoie le NOUVEAU niveau (identique a l'ancien si pas de palier franchi) */
    private static int ajouterXp(PlayerServer ps, String metier, int niveauActuel, int xpGagne) {
        if (niveauActuel >= NIVEAU_MAX) return niveauActuel; // deja au max, XP ignore

        int xpTotal = ps.getXp(metier) + xpGagne;
        int seuil = 100 * (niveauActuel + 1);

        int niveau = niveauActuel;
        while (xpTotal >= seuil && niveau < NIVEAU_MAX) {
            xpTotal -= seuil;
            niveau++;
            seuil = 100 * (niveau + 1);
        }

        ps.setXp(metier, xpTotal);
        return niveau;
    }
}
