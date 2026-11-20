package fr.kevyn.farmland.directives.marché.boutique;

import fr.kevyn.farmland.directives.cosmetic.titres.Titre;
import fr.kevyn.farmland.directives.cosmetic.titres.Titres;
import fr.kevyn.farmland.directives.infrastructure.MessageColor;
import fr.kevyn.farmland.directives.plot.une.PlotUne;
import fr.kevyn.farmland.directives.plot.une.UneCalc;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.doonees.menus.GameMenu;
import fr.kevyn.farmland.doonees.menus.GameMenuHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Clics dans la boutique (/shop). Tout achat se fait en 2 clics sur le même
 * bouton dans les 10 secondes : le 1er clic annonce le prix, le 2e achète.
 */
public final class BoutiqueListener implements Listener {

    private static final long CONFIRMATION_MS = 10_000L;

    private final JavaPlugin plugin;
    /** Dernier bouton d'achat cliqué par joueur, avec l'heure du clic. */
    private final Map<UUID, Object[]> confirmations = new HashMap<>();

    public BoutiqueListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Ouvre une page de la boutique (au tick suivant si on est dans un clic). */
    public static void ouvrir(JavaPlugin plugin, Player joueur, BoutiqueMenu.Page page, boolean tickSuivant) {
        PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
        if (ps == null) return;
        Runnable action = () -> {
            if (joueur.isOnline()) joueur.openInventory(BoutiqueMenu.ouvrir(joueur, ps, page));
        };
        if (tickSuivant) Bukkit.getScheduler().runTask(plugin, action);
        else action.run();
    }

    @EventHandler
    public void onClic(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player joueur)) return;
        GameMenu trouve = null;
        for (GameMenu g : GameMenuHashMap.getInstance().getMenulist()) {
            if (event.getInventory().equals(g.getInventory())) { trouve = g; break; }
        }
        if (!(trouve instanceof BoutiqueMenu menu)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) return; // clic dans son inventaire
        String action = menu.actionDuSlot(slot);
        if (action == null) return;
        joueur.playSound(joueur.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        if (action.startsWith("page:")) {
            ouvrir(plugin, joueur, BoutiqueMenu.Page.valueOf(action.substring(5)), true);
        } else if (action.startsWith("cmd:")) {
            String commande = action.substring(4);
            Bukkit.getScheduler().runTask(plugin, () -> {
                joueur.closeInventory();
                joueur.performCommand(commande);
            });
        } else if (action.equals("worldedit")) {
            // /buy worldedit garde sa propre confirmation (2e appel dans les 30 s) et ses vérifications.
            Bukkit.getScheduler().runTask(plugin, () -> {
                PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(joueur.getUniqueId());
                if (ps == null) return;
                long avant = ps.getWeTimeExpiry();
                int soldeAvant = ps.getMoney();
                joueur.performCommand("buy worldedit");
                if (ps.getWeTimeExpiry() == avant && ps.getMoney() == soldeAvant && soldeAvant >= fr.kevyn.farmland.directives.marché.commands.BuyCommands.WE_COST) {
                    joueur.sendMessage(MessageColor.YELLOW.apply("Ou clique encore sur WorldEdit dans la boutique."));
                }
                if (joueur.isOnline()) joueur.openInventory(BoutiqueMenu.ouvrir(joueur, ps, BoutiqueMenu.Page.ACCUEIL));
            });
        } else if (action.equals("retirer")) {
            Titres.retirer(joueur);
            ouvrir(plugin, joueur, BoutiqueMenu.Page.TITRES, true);
        } else if (action.startsWith("titre:")) {
            Titre titre = Titre.depuisNom(action.substring(6));
            if (titre == null) return;
            if (Titres.possede(joueur.getUniqueId(), titre)) {
                Titres.equiper(joueur, titre);
            } else if (confirme(joueur, action)) {
                Titres.acheter(joueur, titre);
            } else {
                joueur.sendMessage(MessageColor.YELLOW.apply("Acheter le titre ") + titre.getAffichage()
                        + MessageColor.YELLOW.apply(" pour " + UneCalc.montant(titre.getPrix()) + " $FB ? Clique encore pour confirmer."));
                return;
            }
            ouvrir(plugin, joueur, BoutiqueMenu.Page.TITRES, true);
        } else if (action.startsWith("miser:")) {
            long montant;
            try {
                montant = Long.parseLong(action.substring(6));
            } catch (NumberFormatException e) {
                return;
            }
            if (!confirme(joueur, action)) {
                joueur.sendMessage(MessageColor.GOLD.apply("★ Miser " + UneCalc.montant(montant)
                        + " $FB sur ton plot ? L'argent misé est dépensé. Clique encore pour confirmer."));
                return;
            }
            PlotUne.miser(joueur, montant);
            ouvrir(plugin, joueur, BoutiqueMenu.Page.UNE, true);
        }
    }

    /** Vrai si le joueur a déjà cliqué ce même bouton d'achat il y a moins de 10 s (et efface la demande). */
    private boolean confirme(Player joueur, String action) {
        long maintenant = System.currentTimeMillis();
        Object[] precedent = confirmations.get(joueur.getUniqueId());
        if (precedent != null && action.equals(precedent[0]) && maintenant - (long) precedent[1] <= CONFIRMATION_MS) {
            confirmations.remove(joueur.getUniqueId());
            return true;
        }
        confirmations.put(joueur.getUniqueId(), new Object[] {action, maintenant});
        return false;
    }
}
