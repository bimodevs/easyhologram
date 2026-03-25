package dev.tabplus.easyhologram.mixin;

import dev.tabplus.easyhologram.util.VisibilityFilter;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.network.ServerCommonNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void easyhologram$onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayNetworkHandler handler) {
            if (VisibilityFilter.shouldIntercept(packet, handler.player)) {
                ci.cancel();
            }
        }
    }
}
