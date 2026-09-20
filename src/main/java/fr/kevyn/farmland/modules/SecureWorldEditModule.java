package fr.kevyn.farmland.modules;

import discordwebhook.messagediscord;
import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.worldeditgestion.WorldEditSecureListener;

/**
 * Charge la protection WorldEdit uniquement si WorldEdit est présent.
 */
public final class SecureWorldEditModule {

    private final FarmlandMain plugin;

    public SecureWorldEditModule(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        if (plugin.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            plugin.getLogger().warning("[WorldEdit] WorldEdit non installé, le module sera ignoré.");
            messagediscord.sendmessage("[WorldEdit] WorldEdit non installé, le module sera ignoré.", "statut");
            return;
        }

        try {
            new WorldEditSecureListener();
            plugin.getLogger().info("[WorldEdit] Module chargé avec succès.");
            messagediscord.sendmessage("Module WorldEditSecure bien lancé", "statut");
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement du module WorldEditSecure !");
            messagediscord.sendmessage("Module WorldEditSecure erreur: " + e, "statut");
            e.printStackTrace();
        }
    }
}
