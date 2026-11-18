package fr.kevyn.farmland.directives.gameplay.guide;

import fr.kevyn.farmland.directives.gameplay.menus.Outils;
import fr.kevyn.farmland.directives.gameplay.objet.CustomItemType;
import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.directives.gameplay.scoreboard.CreativePlotScoreboard;
import fr.kevyn.farmland.directives.infrastructure.JobType;
import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;
import fr.kevyn.farmland.directives.marché.commands.BuyCommands;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotUpgrade;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.marche.Market;
import fr.kevyn.farmland.doonees.marche.MarketHolder;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Menu du guide, personnalisé : chaque page montre les chiffres du joueur
 * et des boutons qui agissent (aller au plot, recevoir l'outil, vendre...).
 *
 * Actions des boutons (lues par GuideMenuListener) :
 *   "page:<PAGE>"     ouvre une page du guide
 *   "cmd:<commande>"  ferme le menu et lance la commande pour le joueur
 *   "vente:<Metier>"  ouvre le menu de vente de ce jeton
 */
public final class GuideMenu extends GameMenu {

    public enum Page { ACCUEIL, PLOT, METIERS, VENDRE, DEPENSER, ACTIVITES, REGLES }

    private static final DecimalFormat PRIX = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.FRANCE));
    private static final int RETOUR = 31;

    private final Page page;
    private final Map<Integer, String> actions = new HashMap<>();

    private GuideMenu(Inventory inventory, Page page) {
        super(inventory, TypeMenu.GUIDE);
        this.page = page;
    }

    public Page getPageGuide() { return page; }

    public String actionDuSlot(int slot) { return actions.get(slot); }

    // ===== Construction =====

    public static Inventory ouvrir(Player joueur, PlayerServer ps, Page page) {
        int taille = page == Page.ACCUEIL ? 45 : 36;
        Inventory inv = Bukkit.createInventory(null, taille, titre(page));
        GuideMenu menu = new GuideMenu(inv, page);
        GameMenu.fillmenu(Material.BLACK_STAINED_GLASS_PANE, inv);
        switch (page) {
            case ACCUEIL -> menu.accueil(joueur, ps);
            case PLOT -> menu.plot(ps);
            case METIERS -> menu.metiers(ps);
            case VENDRE -> menu.vendre(ps);
            case DEPENSER -> menu.depenser(ps);
            case ACTIVITES -> menu.activites();
            case REGLES -> menu.regles();
        }
        if (page != Page.ACCUEIL) {
            menu.bouton(RETOUR, CustomItemType.ARROW_PREV.create(), "§7← Retour au guide", List.of(), "page:ACCUEIL");
        }
        return inv;
    }

    private static String titre(Page page) {
        return switch (page) {
            case ACCUEIL -> "§6✦ Guide FarmLand";
            case PLOT -> "§2Guide ▸ Ton plot";
            case METIERS -> "§6Guide ▸ Les métiers";
            case VENDRE -> "§3Guide ▸ Vendre";
            case DEPENSER -> "§5Guide ▸ Dépenser";
            case ACTIVITES -> "§4Guide ▸ Activités";
            case REGLES -> "§8Guide ▸ Règles";
        };
    }

    private void bouton(int slot, ItemStack item, String nom, List<String> lore, String action) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(nom);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        getInventory().setItem(slot, item);
        if (action != null) actions.put(slot, action);
    }

    // ===== Pages =====

    private void accueil(Player joueur, PlayerServer ps) {
        long jetons = 0;
        double valeur = 0;
        Market market = MarketHolder.get();
        for (String metier : MarketCalc.METIERS) {
            int stock = jetonsDe(ps, metier);
            jetons += stock;
            if (stock > 0) valeur += MarketCalc.estimer(metier, market, stock).gain();
        }
        int cout = MenuPlotUpgrade.getCost(ps.getUpgrade());
        boolean aUnOutil = Outils.possedeOutil(joueur, Material.NETHERITE_PICKAXE) || Outils.possedeOutil(joueur, Material.NETHERITE_HOE)
                || Outils.possedeOutil(joueur, Material.FISHING_ROD) || Outils.possedeOutil(joueur, Material.NETHERITE_AXE)
                || Outils.possedeOutil(joueur, Material.NETHERITE_SWORD);

        ItemStack tete = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) tete.getItemMeta();
        skull.setOwningPlayer(joueur);
        tete.setItemMeta(skull);
        bouton(4, tete, "§e§l" + joueur.getName(), List.of(
                "§7Argent : §6" + ps.getMoney() + " $FB",
                "§7WorldEdit : " + CreativePlotScoreboard.formatWE(ps),
                "§7Plot : §f" + bordure(ps) + " blocs",
                "§7Jetons à vendre : §b" + jetons + (jetons > 0 ? " §7(≈ §6" + Math.round(valeur) + " $FB§7)" : ""),
                "",
                "§a➜ Prochain objectif :",
                "§f" + GuideTextes.prochainObjectif(aUnOutil, jetons, ps.getMoney(), cout)
        ), null);

        bouton(19, CustomItemType.GUIDE_PLOT.create(), "§2§lTon plot", List.of("§7Ton monde, sa taille, l'agrandir", "", "§eClic pour voir"), "page:PLOT");
        bouton(21, CustomItemType.GUIDE_METIERS.create(), "§6§lLes métiers", List.of("§7Tes niveaux, tes outils", "", "§eClic pour voir"), "page:METIERS");
        bouton(23, CustomItemType.GUIDE_VENDRE.create(), "§3§lVendre", List.of("§7Tes jetons et les prix du marché", "", "§eClic pour voir"), "page:VENDRE");
        bouton(25, CustomItemType.GUIDE_DEPENSER.create(), "§5§lDépenser", List.of("§7WorldEdit, cosmétiques, plot", "", "§eClic pour voir"), "page:DEPENSER");
        bouton(30, CustomItemType.GUIDE_ACTIVITES.create(), "§4§lActivités", List.of("§7Course, vote, calcul, classement", "", "§eClic pour voir"), "page:ACTIVITES");
        bouton(32, CustomItemType.GUIDE_REGLES.create(), "§8§lRègles", List.of("§7À lire une fois", "", "§eClic pour voir"), "page:REGLES");
        bouton(40, new ItemStack(Material.BARRIER), "§cFermer", List.of(), "fermer");
    }

    private void plot(PlayerServer ps) {
        int cout = MenuPlotUpgrade.getCost(ps.getUpgrade());
        boolean prive = ps.getPlotdata() != null && ps.getPlotdata().getPrivateplot();
        bouton(4, CustomItemType.GUIDE_PLOT.create(), "§2§lTon plot", List.of(
                "§7Ton monde à toi, créé à ta 1re connexion.",
                "",
                "§7Taille : §f" + bordure(ps) + " blocs",
                "§7Prochain agrandissement : §6" + cout + " $FB §7(+5 blocs)",
                "§7Plot privé : " + (prive ? "§coui" : "§anon"),
                "",
                "§f/plot visit <joueur> §7visiter un plot",
                "§f/plot add <joueur> §7il peut construire",
                "§f/plot trust <joueur> §7confiance totale (WorldEdit)"
        ), null);
        bouton(20, new ItemStack(Material.GRASS_BLOCK), "§a§lAller à mon plot", List.of("§7/plot home"), "cmd:plot home");
        bouton(22, CustomItemType.CLOCK_DAYNIGHT.create(), "§e§lConfigurer", List.of("§7Jour, météo, mobs, eau/lave, privé", "§7/plot config"), "cmd:plot config");
        bouton(24, CustomItemType.UPGRADE_BOUGHT.create(), "§6§lAgrandir", List.of("§7Prochain : §6" + cout + " $FB", "§7/plot buy"), "cmd:plot buy");
    }

    private void metiers(PlayerServer ps) {
        bouton(4, CustomItemType.GUIDE_METIERS.create(), "§6§lLes métiers", List.of(
                "§7Travaille avec ton outil : §eXP §7+ §bjetons§7.",
                "§7Plus ton niveau monte, plus tu gagnes de jetons.",
                "",
                "§7Récompenses : niv 10, 20, 40 (§6$FB§7),",
                "§7niv 50 (balise), niv 100 (outil de Maître).",
                "§7Farmeur : §fMaj + clic droit §7avec la houe = récolte auto."
        ), null);
        metier(20, Material.NETHERITE_PICKAXE, "Mineur", JobType.MINEUR, ps, "Casse les minerais de ton générateur de pierre", "pioche");
        metier(21, Material.NETHERITE_HOE, "Farmeur", JobType.FARMEUR, ps, "Récolte tes cultures mûres", "houe");
        metier(22, Material.FISHING_ROD, "Pêcheur", JobType.PECHEUR, ps, "Pêche (chaque prise vaut 7 actions)", "peche");
        metier(23, Material.NETHERITE_AXE, "Agriculteur", JobType.AGRICULTEUR, ps, "Tue des animaux (mobs activés sur ton plot)", "hache");
        metier(24, Material.NETHERITE_SWORD, "Tueur", JobType.TUEUR, ps, "Tue des monstres (mobs activés sur ton plot)", "epee");
        bouton(33, new ItemStack(Material.EXPERIENCE_BOTTLE), "§e§lMes niveaux en détail", List.of("§7/metiers"), "cmd:metiers");
    }

    private void metier(int slot, Material icone, String nom, JobType job, PlayerServer ps, String comment, String commande) {
        int niveau = RecompenseUtil.getLevel(ps, job);
        int xp = ps.getXp(cleXp(job));
        int requise = RecompenseUtil.getXpNeeded(niveau);
        int jetons = jetonsDe(ps, metierMarche(job));
        bouton(slot, new ItemStack(icone), "§6§l" + nom + " §7— niv §f" + niveau, List.of(
                "§7" + comment,
                "",
                "§7XP : " + barre(xp, requise) + " §7" + xp + "/" + requise,
                "§7Jetons en stock : §b" + jetons,
                "",
                "§eClic : recevoir ton outil §7(/" + commande + ")"
        ), "cmd:" + commande);
    }

    private void vendre(PlayerServer ps) {
        bouton(4, CustomItemType.GUIDE_VENDRE.create(), "§3§lVendre", List.of(
                "§7Les prix suivent l'§foffre et la demande §7:",
                "§c▼ §7beaucoup de ventes = le prix baisse",
                "§a▲ §7peu de ventes = il remonte (jusqu'à +90 %)",
                "",
                "§e➜ Vends petit à petit et attends que ça remonte."
        ), null);
        Market market = MarketHolder.get();
        int slot = 20;
        for (String metier : MarketCalc.METIERS) {
            int stock = jetonsDe(ps, metier);
            int coef = market.getCoefficient(metier);
            String couleur = coef >= 100 ? "§a" : coef >= 75 ? "§e" : "§c";
            List<String> lore = new ArrayList<>(List.of(
                    "§7Prix : §f" + PRIX.format(MarketCalc.getPrixActuel(metier, market)) + " $FB " + couleur + "(" + coef + "%)",
                    "§7Tu en as : §b" + stock));
            if (stock > 0) {
                lore.add("§7Tout vendre ≈ §6" + MarketCalc.estimer(metier, market, stock).gain() + " $FB");
                lore.add("");
                lore.add("§eClic : choisir combien vendre");
            }
            bouton(slot++, iconeJeton(metier), "§3§lJeton " + MarketCalc.nomAffiche(metier), lore, stock > 0 ? "vente:" + metier : null);
        }
        bouton(33, new ItemStack(Material.CHEST), "§b§lOuvrir mon sac", List.of("§7/bag"), "cmd:bag");
    }

    private void depenser(PlayerServer ps) {
        bouton(4, CustomItemType.GUIDE_DEPENSER.create(), "§5§lDépenser", List.of(
                "§7Ton argent : §6" + ps.getMoney() + " $FB",
                "",
                "§7Donner des $FB : §f/pay <joueur> <montant>",
                "§7Bonus : §6+25 $FB §7tous les 150 blocs posés."
        ), null);
        bouton(20, new ItemStack(Material.WOODEN_AXE), "§d§lWorldEdit 1 h", List.of(
                "§7Prix : §6" + BuyCommands.WE_COST + " $FB",
                "§7Temps restant : " + CreativePlotScoreboard.formatWE(ps),
                "§7Sur ton plot et ceux où tu es trust.",
                "§7Gratuit : 1 h par vote (§f/vote§7).",
                "",
                "§eClic : acheter (à confirmer)"), "cmd:buy worldedit");
        bouton(22, new ItemStack(Material.LEATHER_HELMET), "§d§lCosmétiques", List.of(
                "§7Chapeaux et accessoires", "§7de §6250 §7à §6500 $FB§7.", "", "§eClic : voir la boutique"), "cmd:buy cosmetic");
        bouton(24, CustomItemType.UPGRADE_BOUGHT.create(), "§d§lAgrandir ton plot", List.of(
                "§7Prochain : §6" + MenuPlotUpgrade.getCost(ps.getUpgrade()) + " $FB §7(+5 blocs)", "", "§eClic : ouvrir"), "cmd:plot buy");
    }

    private void activites() {
        bouton(4, CustomItemType.GUIDE_ACTIVITES.create(), "§4§lActivités", List.of(
                "§fCalcul : §7toutes les 5 min dans le chat,",
                "§7le 1er qui répond gagne §6+75 $FB§7."
        ), null);
        bouton(20, new ItemStack(Material.OAK_BOAT), "§c§lCourse de bateaux", List.of(
                "§7Passe les 3 points de contrôle puis l'arrivée.",
                "§7Podium du jour : §6500 / 250 / 100 $FB",
                "", "§eClic : rejoindre §7(/joinboat pour quitter)"), "cmd:joinboat");
        bouton(22, new ItemStack(Material.NAME_TAG), "§c§lVoter", List.of(
                "§7Chaque vote : §f1 h de WorldEdit gratuit§7.", "", "§eClic : liens de vote"), "cmd:vote");
        bouton(24, new ItemStack(Material.GOLD_INGOT), "§c§lClassements", List.of(
                "§7Les meilleurs joueurs.", "", "§eClic : voir"), "cmd:classement");
    }

    private void regles() {
        bouton(13, CustomItemType.GUIDE_REGLES.create(), "§8§lRègles du serveur", GuideTextes.REGLES, null);
    }

    // ===== Outils =====

    private static int bordure(PlayerServer ps) {
        return ps.getPlotdata() == null ? 0 : ps.getPlotdata().getWorldborder();
    }

    /** Barre de progression en 10 cases. */
    static String barre(int valeur, int maximum) {
        int pleins = maximum <= 0 ? 0 : Math.max(0, Math.min(10, (int) ((long) valeur * 10 / maximum)));
        return "§a" + "■".repeat(pleins) + "§8" + "■".repeat(10 - pleins);
    }

    private static String cleXp(JobType job) {
        return switch (job) {
            case MINEUR -> "Mineur";
            case FARMEUR -> "Farmeur";
            case PECHEUR -> "Pecheur";
            case AGRICULTEUR -> "Agriculteur";
            case TUEUR -> "Tueur";
        };
    }

    private static String metierMarche(JobType job) {
        return switch (job) {
            case MINEUR -> MarketCalc.MINEUR;
            case FARMEUR -> MarketCalc.FARMEUR;
            case PECHEUR -> MarketCalc.PECHEUR;
            case AGRICULTEUR -> MarketCalc.AGRICULTEUR;
            case TUEUR -> MarketCalc.TUEUR;
        };
    }

    static int jetonsDe(PlayerServer ps, String metier) {
        return switch (metier) {
            case MarketCalc.MINEUR -> ps.getJetonMineur();
            case MarketCalc.FARMEUR -> ps.getJetonFarmeur();
            case MarketCalc.PECHEUR -> ps.getJetonPecheur();
            case MarketCalc.AGRICULTEUR -> ps.getJetonAgriculteur();
            case MarketCalc.TUEUR -> ps.getJetonTueur();
            default -> 0;
        };
    }

    private static ItemStack iconeJeton(String metier) {
        return new ItemStack(fr.kevyn.farmland.directives.gameplay.menus.MenuBag.iconeDe(metier));
    }
}
