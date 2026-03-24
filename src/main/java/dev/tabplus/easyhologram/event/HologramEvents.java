package dev.tabplus.easyhologram.event;

import dev.tabplus.easyhologram.api.Hologram;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events fired by the hologram system.
 * Other mods can subscribe to these to react when holograms are
 * created, removed, or ticked.
 *
 * <pre>{@code
 * HologramEvents.AFTER_CREATE.register(hologram -> {
 *     System.out.println("Hologram spawned: " + hologram.getId());
 * });
 * }</pre>
 */
public final class HologramEvents {

    private HologramEvents() {}

    /**
     * Fired after a hologram is registered and spawned.
     */
    public static final Event<AfterCreate> AFTER_CREATE =
            EventFactory.createArrayBacked(AfterCreate.class, callbacks -> hologram -> {
                for (AfterCreate callback : callbacks) {
                    callback.onCreate(hologram);
                }
            });

    /**
     * Fired just before a hologram is destroyed and unregistered.
     */
    public static final Event<BeforeRemove> BEFORE_REMOVE =
            EventFactory.createArrayBacked(BeforeRemove.class, callbacks -> hologram -> {
                for (BeforeRemove callback : callbacks) {
                    callback.onRemove(hologram);
                }
            });

    /**
     * Fired every server tick for each alive hologram.
     * Useful for advanced custom animations or interactions.
     */
    public static final Event<OnTick> ON_TICK =
            EventFactory.createArrayBacked(OnTick.class, callbacks -> (hologram, tickCount) -> {
                for (OnTick callback : callbacks) {
                    callback.onTick(hologram, tickCount);
                }
            });

    // -------------------------------------------------------------------
    //  Functional interfaces
    // -------------------------------------------------------------------

    @FunctionalInterface
    public interface AfterCreate {
        void onCreate(Hologram hologram);
    }

    @FunctionalInterface
    public interface BeforeRemove {
        void onRemove(Hologram hologram);
    }

    @FunctionalInterface
    public interface OnTick {
        void onTick(Hologram hologram, long tickCount);
    }
}
