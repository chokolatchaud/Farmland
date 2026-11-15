package fr.kevyn.farmland.directives.plot.gestion;

import fr.kevyn.farmland.directives.infrastructure.DebugLog;
import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Applique la bordure enregistrée d'un plot à son monde, tout de suite.
 * Sans ça, un achat d'upgrade ou une commande admin ne changeait que le chiffre enregistré :
 * la vraie bordure ne bougeait qu'au prochain chargement du monde (reconnexion).
 */
public final class PlotBordure {

    private PlotBordure() {
    }

    /**
     * Règle la bordure du monde du plot sur la valeur enregistrée, si le monde est chargé.
     * Sinon, elle sera appliquée au prochain chargement (PlotInit).
     * Retourne la taille appliquée, ou -1 si rien n'a été appliqué.
     */
    public static int appliquer(JavaPlugin plugin, PlayerServer ps) {
        if (ps == null || ps.getPlotdata() == null) {
            return -1;
        }
        int taille = ps.getPlotdata().getWorldborder();
        String nomMonde = ps.getPlotdata().getNameWorld();

        if (taille < 1) {
            plugin.getLogger().warning("[Bordure] Taille invalide (" + taille + ") pour " + ps.getName() + " : non appliquée");
            return -1;
        }

        World monde = nomMonde == null ? null : Bukkit.getWorld(nomMonde);
        if (monde == null) {
            DebugLog.debug(plugin, "[Bordure] " + ps.getName() + " : monde non chargé, " + taille + " sera appliquée au prochain chargement");
            return -1;
        }

        double avant = monde.getWorldBorder().getSize();
        monde.getWorldBorder().setSize(taille);
        DebugLog.debug(plugin, "[Bordure] " + ps.getName() + " monde=" + nomMonde + " : " + (int) avant + " -> " + taille);
        return taille;
    }
}
