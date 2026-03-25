package dev.tabplus.easyhologram.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlaceholderUtil {
    private static boolean parsed = false;
    private static boolean loaded = false;

    public static boolean isPlaceholderApiLoaded() {
        if (!parsed) {
            loaded = FabricLoader.getInstance().isModLoaded("placeholder-api");
            parsed = true;
        }
        return loaded;
    }

    public static Text parseText(Text text, ServerPlayerEntity player) {
        if (!isPlaceholderApiLoaded()) return text;
        return Wrapper.parseText(text, player);
    }
    
    public static String parseString(String text, ServerPlayerEntity player) {
        if (!isPlaceholderApiLoaded()) return text;
        return Wrapper.parseString(text, player);
    }

    private static class Wrapper {
        static Text parseText(Text text, ServerPlayerEntity player) {
            try {
                return eu.pb4.placeholders.api.Placeholders.parseText(text, eu.pb4.placeholders.api.PlaceholderContext.of(player));
            } catch (Throwable t) {
                return text;
            }
        }

        static String parseString(String text, ServerPlayerEntity player) {
            try {
                return eu.pb4.placeholders.api.Placeholders.parseText(Text.literal(text), eu.pb4.placeholders.api.PlaceholderContext.of(player)).getString();
            } catch (Throwable t) {
                return text;
            }
        }
    }
}
