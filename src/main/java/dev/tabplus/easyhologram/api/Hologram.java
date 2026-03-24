package dev.tabplus.easyhologram.api;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/**
 * Base hologram class. Every hologram type (text, block, item) extends this.
 * <p>
 * A hologram is essentially a wrapper around a vanilla Display Entity with
 * some extra features: persistence, animations, lifecycle management.
 */
public abstract class Hologram {

    protected final String id;
    protected Vec3d position;
    protected ServerWorld world;
    protected boolean persistent;
    protected boolean alive;
    protected HologramAnimation animation;

    // The underlying vanilla entity. Null until spawn() is called.
    protected Entity displayEntity;

    protected Hologram(String id, ServerWorld world, Vec3d position) {
        this.id = id;
        this.world = world;
        this.position = position;
        this.persistent = true;
        this.alive = false;
        this.animation = HologramAnimation.NONE;
    }

    // -------------------------------------------------------------------
    //  Lifecycle
    // -------------------------------------------------------------------

    /**
     * Spawns the underlying Display Entity into the world.
     * This should only be called from the server's main thread
     * (the HologramManager handles this via its tick queue).
     */
    public void spawn() {
        if (alive) return;

        displayEntity = createEntity();
        if (displayEntity == null) return;

        displayEntity.refreshPositionAndAngles(
                position.x, position.y, position.z, 0f, 0f
        );

        world.spawnEntity(displayEntity);
        alive = true;
    }

    /**
     * Removes the Display Entity from the world.
     * Must be called from the main server thread.
     */
    public void destroy() {
        if (!alive) return;

        if (displayEntity != null) {
            displayEntity.discard();
            displayEntity = null;
        }
        alive = false;
    }

    /**
     * Teleports the hologram to a new position.
     */
    public void teleport(Vec3d newPosition) {
        this.position = newPosition;
        if (displayEntity != null && alive) {
            displayEntity.refreshPositionAndAngles(
                    newPosition.x, newPosition.y, newPosition.z,
                    displayEntity.getYaw(), displayEntity.getPitch()
            );
        }
    }

    /**
     * Called every server tick if an animation is set.
     * Override in subclasses for type-specific animation behavior.
     */
    public void tickAnimation(long tickCount) {
        if (animation == HologramAnimation.NONE || !alive || displayEntity == null) {
            return;
        }

        switch (animation) {
            case FLOAT -> {
                // Gentle bobbing motion: 0.05 blocks amplitude, 2-second period
                double offset = Math.sin(tickCount * 0.08) * 0.05;
                displayEntity.refreshPositionAndAngles(
                        position.x, position.y + offset, position.z,
                        displayEntity.getYaw(), displayEntity.getPitch()
                );
            }
            case ROTATE -> {
                float yaw = (tickCount * 3) % 360;
                displayEntity.refreshPositionAndAngles(
                        position.x, position.y, position.z,
                        yaw, displayEntity.getPitch()
                );
            }
            case PULSE -> {
                // Pulse is handled differently per type — subclasses override this
            }
        }
    }

    // -------------------------------------------------------------------
    //  Abstract
    // -------------------------------------------------------------------

    /**
     * Creates the underlying vanilla Display Entity.
     * Implemented by TextHologram, BlockHologram, ItemHologram.
     */
    protected abstract Entity createEntity();

    /**
     * Returns the type name (e.g., "text", "block", "item").
     * Used for serialization.
     */
    public abstract String getType();

    // -------------------------------------------------------------------
    //  Getters / Setters
    // -------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public Vec3d getPosition() {
        return position;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isAlive() {
        return alive;
    }

    public HologramAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(HologramAnimation animation) {
        this.animation = animation;
    }

    public Entity getDisplayEntity() {
        return displayEntity;
    }

    public UUID getEntityUuid() {
        return displayEntity != null ? displayEntity.getUuid() : null;
    }
}
