package dev.tabplus.easyhologram.util;

import dev.tabplus.easyhologram.EasyHologram;
import dev.tabplus.easyhologram.api.Hologram;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class VisibilityFilter {

    /**
     * Determines if a packet intended for a player should be dropped because 
     * the player is not allowed to see the hologram it belongs to.
     */
    public static boolean shouldIntercept(Packet<?> packet, ServerPlayerEntity player) {
        if (EasyHologram.getManager() == null) return false;

        int entityId = -1;
        
        if (packet instanceof EntitySpawnS2CPacket spawnPacket) {
            entityId = spawnPacket.getEntityId();
        } else if (packet instanceof EntityTrackerUpdateS2CPacket trackerPacket) {
            entityId = trackerPacket.id();
        }

        if (entityId != -1) {
            for (Hologram hologram : EasyHologram.getManager().getAll()) {
                if (hologram.getDisplayEntity() != null && hologram.getDisplayEntity().getId() == entityId) {
                    if (!hologram.canSee(player.getUuid())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
