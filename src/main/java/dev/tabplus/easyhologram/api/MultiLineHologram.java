package dev.tabplus.easyhologram.api;

import dev.tabplus.easyhologram.EasyHologram;
import dev.tabplus.easyhologram.manager.HologramManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class that automatically creates and manages multiple TextHologram 
 * instances to simulate multi-line text with calculated Y-axis offsets.
 */
public class MultiLineHologram {
    private final String baseId;
    private Vec3d position;
    private ServerWorld world;
    private float lineSpacing;
    private float scale;
    private boolean billboard;
    private boolean shadow;
    
    private final List<TextHologram> lines = new ArrayList<>();

    public MultiLineHologram(String id, ServerWorld world, Vec3d position) {
        this.baseId = id;
        this.world = world;
        this.position = position;
        this.lineSpacing = 0.3f; // Default spacing between lines
        this.scale = 1.0f;
        this.billboard = true;
        this.shadow = false;
    }

    /**
     * Rebuilds the holograms to match the provided text lines.
     * Top-most line is placed at the base position, and subsequent lines go downwards.
     */
    public void setLines(List<Text> textLines) {
        HologramManager manager = EasyHologram.getManager();
        
        // Remove existing lines
        for (TextHologram line : lines) {
            if (manager != null) {
                manager.unregister(line.getId());
            } else {
                line.destroy();
            }
        }
        lines.clear();

        // Create new lines from top to bottom
        for (int i = 0; i < textLines.size(); i++) {
            double yOffset = - (i * lineSpacing);
            Vec3d linePos = position.add(0, yOffset, 0);
            
            TextHologram lineHologram = TextHologram.builder(linePos)
                    .id(baseId + "_line_" + i)
                    .text(textLines.get(i))
                    .scale(scale)
                    .billboard(billboard)
                    .shadow(shadow)
                    .build(world);
                    
            lines.add(lineHologram);
        }
    }

    /**
     * Registers all line holograms with the manager or spawns them directly.
     */
    public void spawn() {
        HologramManager manager = EasyHologram.getManager();
        for (TextHologram line : lines) {
            if (manager != null && manager.get(line.getId()) == null) {
                manager.register(line);
            } else if (manager == null) {
                line.spawn();
            }
        }
    }

    /**
     * Unregisters/destroys all line holograms.
     */
    public void destroy() {
        HologramManager manager = EasyHologram.getManager();
        for (TextHologram line : lines) {
            if (manager != null) {
                manager.unregister(line.getId());
            } else {
                line.destroy();
            }
        }
    }

    /**
     * Teleports all lines collectively, respecting their Y offsets.
     */
    public void teleport(Vec3d newPosition) {
        this.position = newPosition;
        for (int i = 0; i < lines.size(); i++) {
            double yOffset = - (i * lineSpacing);
            lines.get(i).teleport(newPosition.add(0, yOffset, 0));
        }
    }

    public String getId() {
        return baseId;
    }

    public List<TextHologram> getHolograms() {
        return lines;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isBillboard() {
        return billboard;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }
}
