package fr.kevyn.farmland.directives.gameplay.guide;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tous les textes du guide (hologrammes, /guide dans le chat, menu, règles),
 * au même endroit pour les relire et les tenir à jour facilement.
 * Aucune dépendance à Bukkit : testé par GuideTextesTest.
 *
 * Les chiffres viennent du code : à mettre à jour si les règles du jeu changent.
 */
public final class GuideTextes {

    private GuideTextes() {
    }

    // ===== HOLOGRAMMES : 6 thèmes, posés au hub avec /guideadmin holo set <thème> =====

    public static final Map<String, String> HOLOGRAMMES = new LinkedHashMap<>();
    static {
        HOLOGRAMMES.put("bienvenue",
                "§6§l✦ BIENVENUE SUR FARMLAND ✦\n"
              + "§fServeur §ecréatif §fà §emétiers\n"
              + "\n"
              + "§7Construis librement sur ton plot,\n"
              + "§7gagne des §e$FB §7avec les métiers.\n"
              + "\n"
              + "§a/guide §7ou §aclic droit sur ton Guide\n"
              + "§7Respect, pas de triche : §f/guide §7➜ Règles");

        HOLOGRAMMES.put("plot",
                "§a§l🏠 TON PLOT\n"
              + "§7Ton monde à toi, créé à ta 1re connexion.\n"
              + "\n"
              + "§f/plot home §7y aller\n"
              + "§f/plot visit <joueur> §7visiter\n"
              + "§f/plot add <joueur> §7inviter à construire\n"
              + "§f/plot config §7météo, mobs, privé...\n"
              + "§f/plot buy §7agrandir (dès §e50 $FB§7)");

        HOLOGRAMMES.put("metiers",
                "§e§l⛏ LES 5 MÉTIERS\n"
              + "§7Prends ton outil et travaille sur ton plot :\n"
              + "\n"
              + "§fMineur §8/pioche §7minerais du générateur\n"
              + "§fFarmeur §8/houe §7cultures mûres\n"
              + "§fPêcheur §8/peche §7poissons\n"
              + "§fAgriculteur §8/hache §7animaux\n"
              + "§fTueur §8/epee §7monstres\n"
              + "\n"
              + "§7Chaque action = §eXP §7+ §bjetons§7. §f/metiers");

        HOLOGRAMMES.put("vendre",
                "§b§l💰 VENDRE SES JETONS\n"
              + "§f/bag §7clique un jeton : vends 10, 25, 50 ou 100 %\n"
              + "§f/market §7prix du moment\n"
              + "\n"
              + "§7Le prix §cbaisse §7quand on vend beaucoup\n"
              + "§7et §aremonte §7avec le temps.\n"
              + "§e➜ Vends petit à petit pour gagner plus !");

        HOLOGRAMMES.put("depenser",
                "§d§l🛒 DÉPENSER SES $FB\n"
              + "§f/plot buy §7agrandir ton plot\n"
              + "§f/buy worldedit §7WorldEdit 1 h (§e200 $FB§7)\n"
              + "§f/buy cosmetic §7chapeaux et accessoires\n"
              + "§f/pay <joueur> <montant> §7donner des $FB\n"
              + "\n"
              + "§7Tout en un coup d'œil : §f/shop");

        HOLOGRAMMES.put("activites",
                "§c§l🎯 ACTIVITÉS\n"
              + "§fCourse de bateaux §8/joinboat\n"
              + "§7  podium du jour : §e500 / 250 / 100 $FB\n"
              + "§fVote §8/vote §7➜ 1 h de WorldEdit gratuit\n"
              + "§fCalcul dans le chat §7➜ §e+75 $FB §7au 1er\n"
              + "§f150 blocs posés §7➜ §e+25 $FB\n"
              + "§fClassements §8/classement");
    }

    public static boolean themeExiste(String theme) {
        return theme != null && HOLOGRAMMES.containsKey(theme.toLowerCase());
    }

    // ===== Chat : à la première connexion (et /guide depuis la console) =====

    public static final List<String> GUIDE_CHAT = List.of(
            "§6§l═══ Bienvenue sur FarmLand ! ═══",
            "§fServeur §ecréatif §fà métiers : construis, travaille, gagne des §e$FB§f.",
            "§a1. §f/plot home §7➜ ton plot perso",
            "§a2. §f/pioche /houe /peche /hache /epee §7➜ ton outil de métier",
            "§a3. §7Travaille avec l'outil ➜ §bjetons §7+ XP",
            "§a4. §f/bag §7➜ vends tes jetons, puis dépense tes $FB",
            "§e➜ Clic droit sur ton §6Guide §e(ou §f/guide§e) : tout, avec tes chiffres."
    );

    // ===== Menu : textes fixes (les chiffres du joueur sont ajoutés par GuideMenu) =====

    public static final List<String> REGLES = List.of(
            "§f• Respect envers tous : pas d'insultes,",
            "§f  de propos discriminatoires ni de harcèlement.",
            "§f• Pas de spam, de flood, de majuscules",
            "§f  abusives ni de publicité.",
            "§c• Interdits : §fcheat, duplication, bugs",
            "§f  exploités, vol, grief.",
            "§f• Respecte les plots des autres.",
            "§f• Pas d'infos perso, de comptes partagés",
            "§f  ou vendus, ni d'usurpation.",
            "§f• Le staff a le dernier mot.",
            "§7Un souci : §f/reportmsg §7ou un ticket Discord."
    );

    /**
     * Le conseil « Prochain objectif » de la fiche du joueur : ce qu'il a de mieux à faire maintenant.
     *
     * @param aUnOutil          le joueur a au moins un outil de métier dans son inventaire
     * @param jetons            nombre total de jetons dans son /bag
     * @param argent            son solde en $FB
     * @param coutAgrandissement prix du prochain agrandissement de plot
     */
    public static String prochainObjectif(boolean aUnOutil, long jetons, long argent, int coutAgrandissement) {
        if (!aUnOutil && jetons == 0) {
            return "Prends un outil de métier (Métiers) et travaille sur ton plot.";
        }
        if (jetons > 0) {
            return "Vends tes " + jetons + " jetons (Vendre), petit à petit.";
        }
        if (coutAgrandissement > 0 && argent >= coutAgrandissement) {
            return "Agrandis ton plot pour " + coutAgrandissement + " $FB (Plot).";
        }
        return "Travaille un métier pour gagner des jetons.";
    }
}
