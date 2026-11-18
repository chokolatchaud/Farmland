package fr.kevyn.farmland.directives.gameplay.guide;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tous les textes du guide (hologrammes, /guide, livre), au même endroit pour
 * les relire et les tenir à jour facilement. Aucune dépendance à Bukkit :
 * testé par GuideTextesTest (longueur des pages du livre, thèmes présents).
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
              + "§a/guide §7l'essentiel  §8│  §a/guide livre §7tout\n"
              + "§7Respect, pas de triche : règles à la fin du livre");

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

    // ===== /guide : court, l'essentiel =====

    public static final List<String> GUIDE_CHAT = List.of(
            "§6§l═══ Guide FarmLand ═══",
            "§fServeur §ecréatif §fà métiers : construis, travaille, gagne des §e$FB§f.",
            "§a1. §f/plot home §7➜ ton plot perso",
            "§a2. §f/pioche /houe /peche /hache /epee §7➜ ton outil de métier",
            "§a3. §7Travaille avec l'outil ➜ §bjetons §7+ XP (§f/metiers§7)",
            "§a4. §f/bag §7➜ vends tes jetons (petit à petit = plus de $FB)",
            "§a5. §f/shop §7➜ dépense tes $FB (plot, WorldEdit, cosmétiques)",
            "§7Tout le reste : §f/guide livre"
    );

    // ===== Livre : tout de A à Z (une page = un sujet, ~13 lignes courtes) =====

    public static final String TITRE_LIVRE = "Guide FarmLand";
    public static final String AUTEUR_LIVRE = "FarmLand";

    public static final List<String> PAGES_LIVRE = List.of(
            // sommaire
            "§6§lGUIDE FARMLAND§r\n\n"
          + "§01. Le serveur\n"
          + "§02-4. Ton plot\n"
          + "§05-10. Les métiers\n"
          + "§011. Niveaux\n"
          + "§012-13. Vendre\n"
          + "§014-16. Dépenser\n"
          + "§017-18. Activités\n"
          + "§019-20. Commandes\n"
          + "§021-23. Règles",
            "§l1. LE SERVEUR§r\n\n"
          + "Serveur §2créatif§0 :\n"
          + "prends tes blocs\n"
          + "dans l'inventaire\n"
          + "créatif.\n\n"
          + "Les objets n'ont\n"
          + "pas de valeur.\n"
          + "Ce qui compte : les\n"
          + "§6$FB§0 et les §3jetons§0\n"
          + "des métiers.",
            "§l2. TON PLOT§r\n\n"
          + "Ton monde à toi,\n"
          + "créé à ta 1re\n"
          + "connexion.\n\n"
          + "§l/plot home§r\n"
          + " y aller\n"
          + "§l/plot visit <nom>§r\n"
          + " visiter un plot",
            "§l2. INVITER§r\n\n"
          + "§l/plot add <nom>§r\n"
          + " il peut construire\n"
          + "§l/plot trust <nom>§r\n"
          + " confiance totale,\n"
          + " WorldEdit compris\n"
          + "§l/plot unadd <nom>§r\n"
          + "§l/plot untrust <nom>§r\n"
          + " retirer",
            "§l3. CONFIGURER§r\n\n"
          + "§l/plot config§r :\n"
          + "- jour / nuit\n"
          + "- pluie / beau temps\n"
          + "- temps figé ou non\n"
          + "- mobs oui / non\n"
          + "- eau et lave\n"
          + "- plot privé\n\n"
          + "§l/plot setspawn§r\n"
          + " ton point d'arrivée",
            "§l4. AGRANDIR§r\n\n"
          + "§l/plot buy§r\n\n"
          + "+5 blocs de bordure\n"
          + "par achat.\n\n"
          + "Prix : §650 $FB§0,\n"
          + "puis 65, puis 100.\n"
          + "Tous les 21 achats,\n"
          + "+20 $FB par achat.",
            "§l5. LES MÉTIERS§r\n\n"
          + "Mineur §l/pioche§r\n"
          + "Farmeur §l/houe§r\n"
          + "Pêcheur §l/peche§r\n"
          + "Agriculteur §l/hache§r\n"
          + "Tueur §l/epee§r\n\n"
          + "Chaque action :\n"
          + "§lXP§r + §3jetons§0,\n"
          + "affichés au-dessus\n"
          + "de tes objets.",
            "§l6. MINEUR§r\n\n"
          + "Fais un générateur\n"
          + "de pierre (eau +\n"
          + "lave) sur ton plot.\n\n"
          + "Il crée parfois des\n"
          + "minerais. Casse-les\n"
          + "avec ta pioche.\n\n"
          + "1 minerai maximum\n"
          + "toutes les 2 s.",
            "§l7. FARMEUR§r\n\n"
          + "Plante tes cultures\n"
          + "et casse-les mûres\n"
          + "avec ta houe.\n\n"
          + "§lFarm rapide§r :\n"
          + "Maj + clic droit\n"
          + "avec la houe, puis\n"
          + "vise tes cultures\n"
          + "mûres : récolte\n"
          + "automatique.",
            "§l8. PÊCHEUR§r\n\n"
          + "Pêche avec ta canne\n"
          + "de Pêcheur\n"
          + "(§l/peche§r).\n\n"
          + "Chaque prise donne\n"
          + "§l70 XP§r et plusieurs\n"
          + "tirages de jetons :\n"
          + "la pêche est lente,\n"
          + "c'est compensé.",
            "§l9. AGRICULTEUR§r\n"
          + "Tue des animaux\n"
          + "avec ta hache.\n\n"
          + "§l10. TUEUR§r\n"
          + "Tue des monstres\n"
          + "avec ton épée.\n\n"
          + "Active les mobs :\n"
          + "§l/plot config§r.\n"
          + "Ton arme devient\n"
          + "plus forte avec\n"
          + "ton niveau.",
            "§l11. NIVEAUX§r\n"
          + "§l/metiers§r : tes\n"
          + "niveaux et ton XP.\n"
          + "Plus de niveau =\n"
          + "plus de jetons.\n\n"
          + "Récompenses :\n"
          + "niv 10 : §61 000 $FB§0\n"
          + "niv 20 : §62 500 $FB§0\n"
          + "niv 40 : §65 000 $FB§0\n"
          + "niv 50 : une balise\n"
          + "niv 100 : outil\n"
          + " de Maître",
            "§l12. VENDRE§r\n\n"
          + "§l/bag§r : ton sac.\n"
          + "Clique un jeton,\n"
          + "puis choisis :\n"
          + "§l10, 25, 50 ou 100 %§r\n"
          + "de ton stock.\n\n"
          + "Chaque bouton\n"
          + "montre le gain\n"
          + "avant de vendre.",
            "§l13. LE MARCHÉ§r\n\n"
          + "Prix = offre et\n"
          + "demande :\n"
          + "- grosses ventes :\n"
          + "  le prix §cbaisse§0\n"
          + "- peu de ventes :\n"
          + "  il §2remonte§0\n"
          + "  (jusqu'à +90 %)\n\n"
          + "§l/market§r : les prix\n\n"
          + "Vends peu à la fois",
            "§l14. WORLDEDIT§r\n\n"
          + "§l/buy worldedit§r :\n"
          + "1 h pour §6200 $FB§0\n"
          + "(tape-la 2 fois).\n\n"
          + "§l/vote§r : 1 h gratuite\n"
          + "par vote.\n\n"
          + "Marche sur ton plot\n"
          + "et ceux où tu es\n"
          + "§ltrust§r.",
            "§l15. COSMÉTIQUES§r\n\n"
          + "§l/buy cosmetic§r :\n"
          + "chapeaux et\n"
          + "accessoires,\n"
          + "de §6250§0 à §6500 $FB§0.\n\n"
          + "Acheté = à toi.\n"
          + "Clique dessus\n"
          + "pour l'équiper.",
            "§l16. ARGENT§r\n\n"
          + "§l/money§r ton solde\n"
          + "§l/money <nom>§r\n"
          + "§l/pay <nom> <x>§r\n"
          + " donner des $FB\n"
          + "§l/shop§r la boutique\n\n"
          + "Bonus : §6+25 $FB§0\n"
          + "tous les 150 blocs\n"
          + "posés sur ton plot.",
            "§l17. COURSE§r\n\n"
          + "§l/joinboat§r au hub\n"
          + "(retape pour\n"
          + "quitter).\n\n"
          + "Passe les 3 points\n"
          + "de contrôle puis\n"
          + "l'arrivée.\n\n"
          + "Podium du jour :\n"
          + "§6500 / 250 / 100 $FB§0",
            "§l18. ACTIVITÉS§r\n\n"
          + "§lCalcul§r : toutes\n"
          + "les 5 min dans le\n"
          + "chat. Le 1er qui\n"
          + "répond gagne\n"
          + "§675 $FB§0.\n\n"
          + "§l/classement§r\n"
          + " les meilleurs\n"
          + "§l/vote§r soutiens\n"
          + " le serveur",
            "§l19. ENTRE JOUEURS§r\n"
          + "§l/msgf <nom> <msg>§r\n"
          + " message privé\n"
          + "§l/r <msg>§r répondre\n"
          + "§l/tpa <nom>§r\n"
          + " aller vers lui\n"
          + "§l/tpahere <nom>§r\n"
          + " l'appeler\n"
          + "§l/tpaccept§r\n"
          + "§l/tpdeny§r\n"
          + "§l/reportmsg <nom>§r\n"
          + " signaler au staff",
            "§l20. RAPPEL§r\n\n"
          + "§l/hub§r le hub\n"
          + "§l/plot home§r ton plot\n"
          + "§l/metiers§r niveaux\n"
          + "§l/bag§r vendre\n"
          + "§l/market§r les prix\n"
          + "§l/shop§r boutique\n"
          + "§l/guide§r l'essentiel\n\n"
          + "Règles : pages\n"
          + "suivantes.",
            "§l21. RÈGLES§r\n\n"
          + "- Respect envers\n"
          + "tous : pas\n"
          + "d'insultes, de\n"
          + "propos\n"
          + "discriminatoires\n"
          + "ni de harcèlement.\n\n"
          + "- Pas de spam, de\n"
          + "flood, de majuscules\n"
          + "abusives ni de pub.",
            "§l22. RÈGLES§r\n\n"
          + "- §cInterdits§0 :\n"
          + "cheat, duplication,\n"
          + "bugs exploités,\n"
          + "vol, grief.\n\n"
          + "- Respecte les plots\n"
          + "et les constructions\n"
          + "des autres.",
            "§l23. RÈGLES§r\n\n"
          + "- Pas d'infos perso,\n"
          + "de comptes partagés\n"
          + "ou vendus, ni\n"
          + "d'usurpation.\n"
          + "- Le staff a le\n"
          + "dernier mot.\n"
          + "Un souci :\n"
          + "§l/reportmsg§r ou\n"
          + "ticket Discord.\n"
          + "§2Respect = bonne\n"
          + "ambiance !"
    );
}
