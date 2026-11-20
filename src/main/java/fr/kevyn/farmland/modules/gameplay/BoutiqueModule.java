package fr.kevyn.farmland.modules.gameplay;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.directives.cosmetic.titres.Titres;

/**
 * Boutique : titres du chat (titres.json).
 */
public final class BoutiqueModule {

    private final FarmlandMain plugin;

    public BoutiqueModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Titres.init(plugin);
        plugin.getLogger().info("[Boutique] Module Boutique activé (titres)");
    }
}
