package fr.kevyn.farmland.directives.infrastructure;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Logs de diagnostic : écrits en console seulement si "debug: true" dans config.yml.
 * Désactivé par défaut, réglable sans recompiler (redémarrage nécessaire).
 */
public final class DebugLog {

    private DebugLog() {
    }

    public static void debug(JavaPlugin plugin, String message) {
        if (plugin == null) return;
        if (!plugin.getConfig().getBoolean("debug", false)) return;
        plugin.getLogger().info("[Debug] " + message);
    }
}
