package fr.kevyn.farmland.directives.marché.boutique;

import fr.kevyn.farmland.directives.cosmetic.CosmeticShop;
import fr.kevyn.farmland.directives.cosmetic.titres.Titre;
import fr.kevyn.farmland.directives.cosmetic.titres.Titres;
import fr.kevyn.farmland.directives.gameplay.objet.CustomItemType;
import fr.kevyn.farmland.directives.gameplay.recompense.RecompenseUtil;
import fr.kevyn.farmland.directives.gameplay.scoreboard.CreativePlotScoreboard;
import fr.kevyn.farmland.directives.infrastructure.Glyphes;
import fr.kevyn.farmland.directives.marché.commands.BuyCommands;
import fr.kevyn.farmland.directives.plot.interfaces.CacheProfils;
import fr.kevyn.farmland.directives.plot.interfaces.MenuPlotUpgrade;
import fr.kevyn.farmland.directives.plot.une.PlotUne;
import fr.kevyn.farmland.directives.plot.une.UneCalc;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.TypeMenu;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * /shop : boutique réunissant WorldEdit, chapeaux, titres, plot à la une et agrandissement.
 * Chaque page a son fond illustré (resource pack) ; les cases vides restent transparentes.
 *
 * Actions des boutons (lues par BoutiqueListener) :
 *   "page:<PAGE>"     ouvre une page de la boutique
 *   "cmd:<commande>"  ferme le menu et lance la commande
 *   "worldedit"       /buy worldedit (2 clics : demande puis confirmation)
 *   "titre:<NOM>"     porte le titre s'il est possédé, sinon l'achète (2 clics)
 *   "retirer"         ne plus porter de titre
 *   "miser:<montant>" mise sur son plot (2 clics)
 */
public final class BoutiqueMenu extends GameMenu {

    public enum Page { ACCUEIL, TITRES, UNE }

    static final int[] SLOTS_TITRES_ACHAT = {19, 20, 21, 23, 24, 25};
    static final int[] SLOTS_TITRES_MERITE = {29, 30, 31, 32, 33};
    static final int[] SLOTS_TOP = {11, 12, 13, 14, 15};
    static final long[] MISES = {1_000, 10_000, 100_000};
    static final int[] SLOTS_MISES = {28, 30, 32};
    private static final Material[] ICONES_MISES = {Material.GOLD_NUGGET, Material.GOLD_INGOT, Material.GOLD_BLOCK};

    private final Page page;
    private final Map<Integer, String> actions = new HashMap<>();

    private BoutiqueMenu(Inventory inventory, Page page) {
        super(inventory, TypeMenu.SHOP);
        this.page = page;
    }

    public Page getPageBoutique() { return page; }

    public String actionDuSlot(int slot) { return actions.get(slot); }

    public static Inventory ouvrir(Player joueur, PlayerServer ps, Page page) {
        Inventory inv = switch (page) {
            case ACCUEIL -> Bukkit.createInventory(null, 36, Glyphes.titreAvecFond(Glyphes.FOND_BOUTIQUE, "Boutique FarmLand"));
            case TITRES -> Bukkit.createInventory(null, 54, Glyphes.titreAvecFond(Glyphes.FOND_TITRES, "Boutique ▸ Titres"));
            case UNE -> Bukkit.createInventory(null, 36, Glyphes.titreAvecFond(Glyphes.FOND_UNE, "Boutique ▸ Plot à la une"));
        };
        BoutiqueMenu menu = new BoutiqueMenu(inv, page);
        switch (page) {
            case ACCUEIL -> menu.accueil(joueur, ps);
            case TITRES -> menu.titres(joueur, ps);
            case UNE -> menu.une(joueur, ps);
        }
        return inv;
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

    private void retour(int slot) {
        bouton(slot, CustomItemType.ARROW_PREV.create(), "§7← Retour à la boutique", List.of(), "page:ACCUEIL");
    }

    // ===== Accueil =====

    private void accueil(Player joueur, PlayerServer ps) {
        Titre porte = Titres.titrePorte(joueur.getUniqueId());
        bouton(4, tete(joueur.getUniqueId(), joueur.getName()), "§e§lBoutique FarmLand", List.of(
                "§7Ton argent : " + Glyphes.argent(ps.getMoney(), "§6"),
                "§7Titre porté : " + (porte == null ? "§8aucun" : porte.getAffichage()),
                "§7WorldEdit : " + CreativePlotScoreboard.formatWE(ps)
        ), null);

        bouton(19, CustomItemType.BOUTIQUE_WORLDEDIT.create(), "§d§lWorldEdit 1 h", List.of(
                "§7Prix : §6" + BuyCommands.WE_COST + " $FB",
                "§7Temps restant : " + CreativePlotScoreboard.formatWE(ps),
                "§7Sur ton plot et ceux où tu es trust.",
                "",
                "§eClic : acheter §7(clique 2 fois pour confirmer)"), "worldedit");

        int min = Integer.MAX_VALUE, max = 0, nombre = 0;
        for (CosmeticShop.Cosmetic c : CosmeticShop.COSMETICS) {
            if (c.price >= 1_000_000) continue; // emplacement « A venir »
            nombre++;
            min = Math.min(min, c.price);
            max = Math.max(max, c.price);
        }
        bouton(21, CustomItemType.BOUTIQUE_CHAPEAU.create(), "§d§lChapeaux", List.of(
                "§7" + nombre + " chapeaux, de §6" + (min == Integer.MAX_VALUE ? 0 : min) + " §7à §6" + max + " $FB",
                "§7Tu en as : §f" + ps.getCosmeticsOwned().size(),
                "",
                "§eClic : voir les chapeaux"), "cmd:buy cosmetic");

        bouton(23, CustomItemType.BOUTIQUE_TITRE.create(), "§d§lTitres", List.of(
                "§7Affiché avant ton grade dans le chat.",
                "§7Porté : " + (porte == null ? "§8aucun" : porte.getAffichage()),
                "§7À acheter : de §62 000 §7à §6100 000 $FB",
                "§7Au mérite : niveau " + Titre.NIVEAU_MERITE + " d'un métier",
                "",
                "§eClic : voir les titres"), "page:TITRES");

        long mise = PlotUne.mise(joueur.getUniqueId());
        int rang = PlotUne.rang(joueur.getUniqueId());
        bouton(25, CustomItemType.BOUTIQUE_UNE.create(), "§6§lPlot à la une", List.of(
                "§7Mise des $FB : les plus grosses mises",
                "§7passent en tête de §f/plot visit§7.",
                "§7Ta mise : " + (mise > 0 ? "§6" + UneCalc.montant(mise) + " $FB §7(" + rang + (rang == 1 ? "er" : "e") + ")" : "§8aucune"),
                "§7Remise à zéro dans §f" + PlotUne.tempsAvantRemiseAZero(),
                "",
                "§eClic : voir les enchères"), "page:UNE");

        bouton(31, CustomItemType.UPGRADE_BOUGHT.create(), "§d§lAgrandir ton plot", List.of(
                "§7Prochain agrandissement : §6" + MenuPlotUpgrade.getCost(ps.getUpgrade()) + " $FB §7(+5 blocs)",
                "",
                "§eClic : ouvrir"), "cmd:plot buy");
    }

    // ===== Titres =====

    private void titres(Player joueur, PlayerServer ps) {
        UUID id = joueur.getUniqueId();
        Titre porte = Titres.titrePorte(id);
        bouton(4, CustomItemType.BOUTIQUE_TITRE.create(), "§d§lTitres", List.of(
                "§7Ton titre s'affiche avant ton grade :",
                "§f" + (porte == null ? "§7[Titre]" : porte.getAffichage()) + " §7[Grade] " + joueur.getName() + " : salut",
                "",
                "§7Ton argent : " + Glyphes.argent(ps.getMoney(), "§6"),
                "§7Un seul titre porté à la fois."
        ), null);

        List<Titre> achat = new ArrayList<>();
        List<Titre> merite = new ArrayList<>();
        for (Titre t : Titre.values()) (t.auMerite() ? merite : achat).add(t);
        for (int i = 0; i < achat.size() && i < SLOTS_TITRES_ACHAT.length; i++) {
            boutonTitre(SLOTS_TITRES_ACHAT[i], achat.get(i), id, ps, porte);
        }
        for (int i = 0; i < merite.size() && i < SLOTS_TITRES_MERITE.length; i++) {
            boutonTitre(SLOTS_TITRES_MERITE[i], merite.get(i), id, ps, porte);
        }

        if (porte != null) {
            bouton(40, new ItemStack(Material.BARRIER), "§c§lRetirer mon titre", List.of("§7Ne plus afficher de titre dans le chat."), "retirer");
        }
        retour(49);
    }

    private void boutonTitre(int slot, Titre t, UUID id, PlayerServer ps, Titre porte) {
        boolean possede = Titres.possede(id, t);
        boolean estPorte = t == porte && possede;
        List<String> lore = new ArrayList<>();
        lore.add("§7Aperçu : " + t.getAffichage() + " §7" + ps.getName());
        lore.add("");
        if (estPorte) {
            lore.add("§a✔ Porté");
        } else if (possede) {
            lore.add("§a✔ Possédé");
            lore.add("§eClic : le porter");
        } else if (t.auMerite()) {
            int niveau = RecompenseUtil.getLevel(ps, t.getMetier());
            lore.add("§7Au mérite : niveau §f" + Titre.NIVEAU_MERITE + "§7 (tu es niveau §f" + niveau + "§7)");
            lore.add("§8Ne s'achète pas.");
        } else {
            lore.add("§7Prix : §6" + UneCalc.montant(t.getPrix()) + " $FB");
            lore.add(ps.getMoney() >= t.getPrix() ? "§eClic : acheter §7(clique 2 fois pour confirmer)" : "§cPas assez d'argent");
        }
        ItemStack item = new ItemStack(possede ? Material.NAME_TAG : (t.auMerite() ? Material.GRAY_DYE : Material.PAPER));
        ItemMeta meta = item.getItemMeta();
        meta.setEnchantmentGlintOverride(estPorte);
        item.setItemMeta(meta);
        String action = estPorte ? null : (possede || !t.auMerite() ? "titre:" + t.name() : null);
        bouton(slot, item, t.getAffichage(), lore, action);
    }

    // ===== Plot à la une =====

    private void une(Player joueur, PlayerServer ps) {
        UUID id = joueur.getUniqueId();
        long mise = PlotUne.mise(id);
        int rang = PlotUne.rang(id);
        bouton(4, CustomItemType.BOUTIQUE_UNE.create(), "§6§lPlot à la une", List.of(
                "§7Mise des $FB sur ton plot : les plus grosses",
                "§7mises passent en tête de §f/plot visit§7.",
                "§7Les mises s'additionnent. L'argent misé",
                "§7est dépensé (jamais rendu).",
                "",
                "§7Ta mise : " + (mise > 0 ? "§6" + UneCalc.montant(mise) + " $FB §7(" + rang + (rang == 1 ? "er" : "e") + ")" : "§8aucune"),
                "§7Ton argent : " + Glyphes.argent(ps.getMoney(), "§6"),
                "§7Remise à zéro dans §f" + PlotUne.tempsAvantRemiseAZero() + " §7(lundi)",
                "§7Autre montant : §f/plot miser <montant>"
        ), null);

        List<UUID> top = PlotUne.classement();
        for (int i = 0; i < SLOTS_TOP.length; i++) {
            if (i >= top.size()) break;
            UUID p = top.get(i);
            PlayerServer autre = PlayerserverHashMap.getInstance().getHashMapPlayer().get(p);
            String nom = autre != null && autre.getName() != null ? autre.getName() : "?";
            bouton(SLOTS_TOP[i], tete(p, nom), (i == 0 ? "§6§l#1 " : "§e#" + (i + 1) + " ") + "§f" + nom,
                    List.of("§6★ " + UneCalc.montant(PlotUne.mise(p)) + " $FB"), null);
        }

        boolean aUnPlot = ps.getPlotdata() != null;
        for (int i = 0; i < MISES.length; i++) {
            long m = MISES[i];
            List<String> lore = new ArrayList<>();
            lore.add("§7Ta mise passera à §6" + UneCalc.montant(mise + m) + " $FB");
            lore.add("");
            if (!aUnPlot) lore.add("§cIl te faut un plot.");
            else if (ps.getMoney() < m) lore.add("§cPas assez d'argent");
            else lore.add("§eClic : miser §7(clique 2 fois pour confirmer)");
            bouton(SLOTS_MISES[i], new ItemStack(ICONES_MISES[i]), "§6§lMiser " + UneCalc.montant(m) + " $FB", lore, "miser:" + m);
        }
        retour(34);
    }

    private static ItemStack tete(UUID id, String nom) {
        ItemStack tete = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) tete.getItemMeta();
        PlayerProfile profil = CacheProfils.profilAvecSkin(id, nom);
        if (profil != null) meta.setPlayerProfile(profil);
        else meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
        tete.setItemMeta(meta);
        return tete;
    }
}
