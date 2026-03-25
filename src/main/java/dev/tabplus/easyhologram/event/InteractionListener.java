package dev.tabplus.easyhologram.event;

import dev.tabplus.easyhologram.EasyHologram;
import dev.tabplus.easyhologram.api.Hologram;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public final class InteractionListener {

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer && !world.isClient) {
                if (handleInteraction(serverPlayer, entity, Hologram.ClickType.RIGHT)) {
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer && !world.isClient) {
                if (handleInteraction(serverPlayer, entity, Hologram.ClickType.LEFT)) {
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    private static boolean handleInteraction(ServerPlayerEntity player, Entity entity, Hologram.ClickType type) {
        if (EasyHologram.getManager() == null) return false;
        
        for (Hologram hologram : EasyHologram.getManager().getAll()) {
            if (hologram.isAlive() && hologram.getDisplayEntity() != null) {
                if (hologram.getDisplayEntity().getUuid().equals(entity.getUuid())) {
                    if (hologram.getClickListener() != null) {
                        hologram.getClickListener().onClick(player, type);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
