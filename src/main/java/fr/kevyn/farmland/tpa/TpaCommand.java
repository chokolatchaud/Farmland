package fr.kevyn.farmland.tpa;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Systeme de teleportation entre joueurs.
 *   /tpa <joueur>      -> demande a aller vers <joueur>
 *   /tpahere <joueur>  -> demande a faire venir <joueur> vers soi
 *   /tpaccept          -> accepte la derniere demande recue
 *   /tpdeny            -> refuse la derniere demande recue
 *
 * Expiration : 60 secondes. Cooldown entre 2 demandes du meme joueur : 10 secondes.
 */
public class TpaCommand implements CommandExecutor {

    private static final long EXPIRATION_TICKS = 20L * 60; // 60 secondes
    private static final long COOLDOWN_MS = 10_000; // 10 secondes

    private enum Type { TPA, TPAHERE }

    private static class Demande {
        final UUID demandeur;
        final Type type;
        final BukkitTask expirationTask;

        Demande(UUID demandeur, Type type, BukkitTask expirationTask) {
            this.demandeur = demandeur;
            this.type = type;
            this.expirationTask = expirationTask;
        }
    }

    // cible -> demande en attente (une seule demande active a la fois par cible)
    private static final Map<UUID, Demande> demandesEnAttente = new HashMap<>();
    // demandeur -> timestamp de sa derniere demande envoyee (cooldown)
    private static final Map<UUID, Long> derniereDemande = new HashMap<>();

    private final JavaPlugin plugin;

    public TpaCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande !");
            return true;
        }
        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "tpa": return envoyerDemande(player, args, Type.TPA);
            case "tpahere": return envoyerDemande(player, args, Type.TPAHERE);
            case "tpaccept": return repondreDemande(player, true);
            case "tpdeny": return repondreDemande(player, false);
            default: return false;
        }
    }

    private boolean envoyerDemande(Player demandeur, String[] args, Type type) {
        if (args.length < 1) {
            sender_usage(demandeur, type);
            return true;
        }

        Player cible = Bukkit.getPlayerExact(args[0]);
        if (cible == null) {
            demandeur.sendMessage("§cCe joueur n'est pas en ligne !");
            return true;
        }
        if (cible.equals(demandeur)) {
            demandeur.sendMessage("§cTu ne peux pas te téléporter à toi-même !");
            return true;
        }

        // cooldown anti-spam
        Long derniere = derniereDemande.get(demandeur.getUniqueId());
        long maintenant = System.currentTimeMillis();
        if (derniere != null && (maintenant - derniere) < COOLDOWN_MS) {
            long resteMs = COOLDOWN_MS - (maintenant - derniere);
            demandeur.sendMessage("§cAttends encore " + (resteMs / 1000 + 1) + "s avant une nouvelle demande !");
            return true;
        }
        derniereDemande.put(demandeur.getUniqueId(), maintenant);

        // remplace une eventuelle demande deja en attente pour cette cible
        annulerDemandeExistante(cible.getUniqueId());

        BukkitTask expirationTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Demande d = demandesEnAttente.remove(cible.getUniqueId());
            if (d != null) {
                if (cible.isOnline()) cible.sendMessage("§7La demande de téléportation a expiré.");
                if (demandeur.isOnline()) demandeur.sendMessage("§7Ta demande de téléportation a expiré.");
            }
        }, EXPIRATION_TICKS);

        demandesEnAttente.put(cible.getUniqueId(), new Demande(demandeur.getUniqueId(), type, expirationTask));

        if (type == Type.TPA) {
            demandeur.sendMessage("§aDemande de téléportation envoyée à " + cible.getName() + " !");
            cible.sendMessage("§e" + demandeur.getName() + " §fdemande à se téléporter vers toi.");
        } else {
            demandeur.sendMessage("§aDemande envoyée à " + cible.getName() + " pour qu'il vienne à toi !");
            cible.sendMessage("§e" + demandeur.getName() + " §fdemande à ce que tu viennes à lui.");
        }
        cible.sendMessage("§7Tape §a/tpaccept §7pour accepter ou §c/tpdeny §7pour refuser. (60s)");

        return true;
    }

    private boolean repondreDemande(Player cible, boolean accepter) {
        Demande demande = demandesEnAttente.remove(cible.getUniqueId());
        if (demande == null) {
            cible.sendMessage("§cTu n'as aucune demande de téléportation en attente !");
            return true;
        }
        demande.expirationTask.cancel();

        Player demandeur = Bukkit.getPlayer(demande.demandeur);
        if (demandeur == null || !demandeur.isOnline()) {
            cible.sendMessage("§cCe joueur n'est plus en ligne !");
            return true;
        }

        if (!accepter) {
            cible.sendMessage("§7Demande refusée.");
            demandeur.sendMessage("§c" + cible.getName() + " a refusé ta demande de téléportation.");
            return true;
        }

        if (demande.type == Type.TPA) {
            demandeur.teleport(cible.getLocation());
        } else {
            cible.teleport(demandeur.getLocation());
        }

        cible.sendMessage("§aTéléportation acceptée !");
        demandeur.sendMessage("§a" + cible.getName() + " a accepté ta demande de téléportation !");
        return true;
    }

    private void annulerDemandeExistante(UUID cible) {
        Demande ancienne = demandesEnAttente.remove(cible);
        if (ancienne != null) ancienne.expirationTask.cancel();
    }

    private void sender_usage(Player player, Type type) {
        player.sendMessage("§cUsage : /" + (type == Type.TPA ? "tpa" : "tpahere") + " <joueur>");
    }
}
