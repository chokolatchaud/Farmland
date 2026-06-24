package fr.kevyn.farmland.api;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.kevyn.farmland.FarmlandMain;
import fr.kevyn.farmland.MessageColor;
import fr.kevyn.farmland.playerserver.PlayerServer;
import fr.kevyn.farmland.playerserver.PlayerserverHashMap;
import fr.kevyn.farmland.region.GameRegion;
import fr.kevyn.farmland.structure.GetStructure;

public class LinkAccountCommand implements CommandExecutor {

    private final FarmlandMain plugin;

    public LinkAccountCommand(FarmlandMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(MessageColor.RED.apply("Usage : /linkaccount <code>"));
            player.sendMessage(MessageColor.GRAY.apply("Le code est affiché sur farm-land.fr après l'inscription."));
            return true;
        }

        if (plugin.getWebApi() == null) {
            player.sendMessage(MessageColor.RED.apply("Le module WebAPI n'est pas configuré, contacte un admin."));
            return true;
        }

        String code = args[0].trim();
        String username = player.getName();
        String uuid = player.getUniqueId().toString();

        player.sendMessage(MessageColor.GRAY.apply("Vérification en cours..."));

        // Appel synchrone dans un thread async pour ne pas bloquer le main thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String message = plugin.getWebApi().verifyAccountMessage(username, uuid, code);
            boolean ok = message != null && !message.startsWith("Code") && !message.startsWith("Aucun")
                    && !message.startsWith("Compte non") && !message.startsWith("WebAPI")
                    && !message.startsWith("Erreur");

            // Retour sur le thread principal pour les messages et actions Bukkit
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (ok) {
                    player.sendMessage(MessageColor.GREEN.apply("✔ " + message));

                    // push balance vers le site maintenant que le compte est lié
                    PlayerServer ps = PlayerserverHashMap.getInstance().getplayerHaspMaps(player.getUniqueId());
                    if (ps != null && plugin.getWebApi() != null) {
                        int nbStructures = 0;
                        for (GameRegion r : GetStructure.getallStructure()) {
                            if (r.getPropriétaire().equals(player.getUniqueId())) nbStructures++;
                        }
                        plugin.getWebApi().pushPlayerBalance(ps.getName(), ps.getMoney(), nbStructures);
                    }
                } else {
                    player.sendMessage(MessageColor.RED.apply("✘ " + message));
                }
            });
        });

        return true;
    }
}
