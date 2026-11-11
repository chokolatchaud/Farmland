package fr.kevyn.farmland.directives.plot.evenement;

import dev.rosewood.rosestacker.event.EntityStackEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Règle liée à RoseStacker : les animaux d'élevage ne sont pas regroupés.
 * Séparée d'EventBuildAndUse : si RoseStacker est absent, cette classe n'est pas
 * enregistrée, et les protections des plots restent actives.
 */
public final class RoseStackerListener implements Listener {

    @EventHandler
    public void onEntityStack(EntityStackEvent event) {

        LivingEntity entity = event.getStack().getEntity();

        if (!canBreed(entity.getType())) {
            return;
        }

        event.setCancelled(true);
    }

    private boolean canBreed(EntityType type) {
        return switch (type) {
            case COW,
                 SHEEP,
                 PIG,
                 CHICKEN,
                 RABBIT,
                 GOAT,
                 HORSE,
                 DONKEY,
                 LLAMA,
                 CAMEL,
                 FOX,
                 PANDA,
                 TURTLE,
                 BEE,
                 CAT,
                 WOLF -> true;
            default -> false;
        };
    }
}
