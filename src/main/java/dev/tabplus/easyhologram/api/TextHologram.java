package dev.tabplus.easyhologram.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * A hologram that shows floating text in the world.
 * <p>
 * Example usage:
 * <pre>{@code
 * TextHologram hologram = TextHologram.builder(position)
 *     .text("§6Welcome to the server!")
 *     .scale(1.5f)
 *     .billboard(true)
 *     .shadow(true)
 *     .build(world);
 *
 * EasyHologram.getManager().register(hologram);
 * }</pre>
 */
public class TextHologram extends Hologram {

    private Text text;
    private float scale;
    private boolean billboard;
    private boolean shadow;
    private int backgroundColor;
    private int lineWidth;

    private TextHologram(Builder builder, ServerWorld world) {
        super(builder.id, world, builder.position);
        this.text = builder.text;
        this.scale = builder.scale;
        this.billboard = builder.billboard;
        this.shadow = builder.shadow;
        this.backgroundColor = builder.backgroundColor;
        this.lineWidth = builder.lineWidth;
    }

    @Override
    protected Entity createEntity() {
        TextDisplayEntity entity = new TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
        applyProperties(entity);
        return entity;
    }

    @Override
    public String getType() {
        return "text";
    }

    /**
     * Updates the displayed text without respawning the entity.
     */
    public void setText(Text newText) {
        this.text = newText;
        if (displayEntity instanceof TextDisplayEntity textDisplay) {
            textDisplay.getDataTracker().set(TextDisplayEntity.TEXT, newText);
        }
    }

    /**
     * Updates the displayed text from a raw string (supports § color codes).
     */
    public void setText(String rawText) {
        setText(Text.literal(rawText));
    }

    public void setScale(float scale) {
        this.scale = scale;
        // Scale is part of the transformation, we need to respawn to update it cleanly.
        // For live updates, users should use the entity's data tracker directly.
    }

    public Text getText() {
        return text;
    }

    public float getScale() {
        return scale;
    }

    public boolean isBillboard() {
        return billboard;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    // -------------------------------------------------------------------
    //  Internal
    // -------------------------------------------------------------------

    private void applyProperties(TextDisplayEntity entity) {
        entity.getDataTracker().set(TextDisplayEntity.TEXT, text);

        // Billboard mode: always face the player
        if (billboard) {
            entity.getDataTracker().set(
                    DisplayEntity.BILLBOARD,
                    DisplayEntity.BillboardMode.CENTER.getIndex()
            );
        }

        // Text shadow
        int flags = 0;
        if (shadow) flags |= TextDisplayEntity.SHADOW_FLAG;
        entity.getDataTracker().set(TextDisplayEntity.TEXT_DISPLAY_FLAGS, (byte) flags);

        // Background color (ARGB)
        if (backgroundColor != 0) {
            entity.getDataTracker().set(TextDisplayEntity.BACKGROUND, backgroundColor);
        }

        // Line width
        if (lineWidth > 0) {
            entity.getDataTracker().set(TextDisplayEntity.LINE_WIDTH, lineWidth);
        }
    }

    // -------------------------------------------------------------------
    //  Builder
    // -------------------------------------------------------------------

    public static Builder builder(Vec3d position) {
        return new Builder(position);
    }

    public static class Builder {
        private final Vec3d position;
        private String id;
        private Text text = Text.literal("Hologram");
        private float scale = 1.0f;
        private boolean billboard = true;
        private boolean shadow = false;
        private int backgroundColor = 0; // 0 = default translucent
        private int lineWidth = 200;

        private Builder(Vec3d position) {
            this.position = position;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder text(String text) {
            this.text = Text.literal(text);
            return this;
        }

        public Builder text(Text text) {
            this.text = text;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder billboard(boolean billboard) {
            this.billboard = billboard;
            return this;
        }

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        /**
         * Background color in ARGB format.
         * Use 0x40000000 for a subtle dark background,
         * or 0x00000000 for fully transparent.
         */
        public Builder backgroundColor(int argb) {
            this.backgroundColor = argb;
            return this;
        }

        public Builder lineWidth(int width) {
            this.lineWidth = width;
            return this;
        }

        public TextHologram build(ServerWorld world) {
            if (id == null || id.isEmpty()) {
                id = "text_" + System.currentTimeMillis();
            }
            return new TextHologram(this, world);
        }
    }
}
