package dev.tabplus.easyhologram.api;

/**
 * Built-in animation presets for holograms.
 * <p>
 * Apply to any hologram via {@code hologram.setAnimation(HologramAnimation.FLOAT)}.
 */
public enum HologramAnimation {
    /** No animation — hologram stays perfectly still. */
    NONE,

    /** Gentle up-and-down bobbing motion. */
    FLOAT,

    /** Slow rotation around the Y axis. */
    ROTATE,

    /** Pulsating scale effect (supported by text holograms). */
    PULSE
}
