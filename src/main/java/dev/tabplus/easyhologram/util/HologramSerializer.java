package dev.tabplus.easyhologram.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.tabplus.easyhologram.api.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles serialization of holograms to NBT (for PersistentState)
 * and JSON (for manual editing / export-import).
 */
public final class HologramSerializer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private HologramSerializer() {}

    // ===================================================================
    //  NBT Serialization (used by PersistentState for auto-save)
    // ===================================================================

    public static NbtCompound toNbt(Hologram hologram) {
        NbtCompound tag = new NbtCompound();
        tag.putString("id", hologram.getId());
        tag.putString("type", hologram.getType());
        tag.putDouble("x", hologram.getPosition().x);
        tag.putDouble("y", hologram.getPosition().y);
        tag.putDouble("z", hologram.getPosition().z);
        tag.putBoolean("persistent", hologram.isPersistent());
        tag.putString("animation", hologram.getAnimation().name());

        switch (hologram) {
            case TextHologram text -> {
                tag.putString("text", Text.Serialization.toJsonString(text.getText(), hologram.getWorld().getRegistryManager()));
                tag.putFloat("scale", text.getScale());
                tag.putBoolean("billboard", text.isBillboard());
                tag.putBoolean("shadow", text.hasShadow());
                tag.putInt("bgColor", text.getBackgroundColor());
                tag.putInt("lineWidth", text.getLineWidth());
            }
            case BlockHologram block -> {
                tag.putString("block", Registries.BLOCK.getId(block.getBlockState().getBlock()).toString());
                tag.putBoolean("billboard", block.isBillboard());
            }
            case ItemHologram item -> {
                tag.putString("item", Registries.ITEM.getId(item.getItemStack().getItem()).toString());
                tag.putInt("count", item.getItemStack().getCount());
                tag.putBoolean("billboard", item.isBillboard());
            }
            default -> {}
        }

        return tag;
    }

    public static Hologram fromNbt(NbtCompound tag, ServerWorld world) {
        String type = tag.getString("type");
        Vec3d pos = new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        String id = tag.getString("id");
        boolean persistent = tag.getBoolean("persistent");
        HologramAnimation animation = parseAnimation(tag.getString("animation"));

        Hologram hologram;

        switch (type) {
            case "text" -> {
                Text text = Text.Serialization.fromJson(tag.getString("text"), world.getRegistryManager());
                if (text == null) text = Text.literal("???");

                hologram = TextHologram.builder(pos)
                        .id(id)
                        .text(text)
                        .scale(tag.getFloat("scale"))
                        .billboard(tag.getBoolean("billboard"))
                        .shadow(tag.getBoolean("shadow"))
                        .backgroundColor(tag.getInt("bgColor"))
                        .lineWidth(tag.getInt("lineWidth"))
                        .build(world);
            }
            case "block" -> {
                Identifier blockId = Identifier.tryParse(tag.getString("block"));
                Block block = blockId != null ? Registries.BLOCK.get(blockId) : null;
                if (block == null) return null;

                hologram = BlockHologram.builder(pos)
                        .id(id)
                        .block(block)
                        .billboard(tag.getBoolean("billboard"))
                        .build(world);
            }
            case "item" -> {
                Identifier itemId = Identifier.tryParse(tag.getString("item"));
                Item item = itemId != null ? Registries.ITEM.get(itemId) : null;
                if (item == null) return null;

                hologram = ItemHologram.builder(pos)
                        .id(id)
                        .item(item)
                        .billboard(tag.getBoolean("billboard"))
                        .build(world);
            }
            default -> {
                return null;
            }
        }

        hologram.setPersistent(persistent);
        hologram.setAnimation(animation);
        return hologram;
    }

    public static NbtList toNbtList(Collection<Hologram> holograms) {
        NbtList list = new NbtList();
        for (Hologram hologram : holograms) {
            if (hologram.isPersistent()) {
                list.add(toNbt(hologram));
            }
        }
        return list;
    }

    public static List<Hologram> fromNbtList(NbtList list, ServerWorld world) {
        List<Hologram> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound tag = list.getCompound(i);
            Hologram hologram = fromNbt(tag, world);
            if (hologram != null) {
                result.add(hologram);
            }
        }
        return result;
    }

    // ===================================================================
    //  JSON Export/Import (for manual editing)
    // ===================================================================

    public static void exportToJson(Collection<Hologram> holograms, Path file) throws IOException {
        JsonArray array = new JsonArray();
        for (Hologram hologram : holograms) {
            if (!hologram.isPersistent()) continue;

            JsonObject obj = new JsonObject();
            obj.addProperty("id", hologram.getId());
            obj.addProperty("type", hologram.getType());
            obj.addProperty("x", hologram.getPosition().x);
            obj.addProperty("y", hologram.getPosition().y);
            obj.addProperty("z", hologram.getPosition().z);
            obj.addProperty("animation", hologram.getAnimation().name());

            switch (hologram) {
                case TextHologram text -> {
                    obj.addProperty("text", Text.Serialization.toJsonString(text.getText(), hologram.getWorld().getRegistryManager()));
                    obj.addProperty("scale", text.getScale());
                    obj.addProperty("billboard", text.isBillboard());
                    obj.addProperty("shadow", text.hasShadow());
                    obj.addProperty("backgroundColor", text.getBackgroundColor());
                    obj.addProperty("lineWidth", text.getLineWidth());
                }
                case BlockHologram block -> {
                    obj.addProperty("block", Registries.BLOCK.getId(block.getBlockState().getBlock()).toString());
                    obj.addProperty("billboard", block.isBillboard());
                }
                case ItemHologram item -> {
                    obj.addProperty("item", Registries.ITEM.getId(item.getItemStack().getItem()).toString());
                    obj.addProperty("billboard", item.isBillboard());
                }
                default -> {}
            }

            array.add(obj);
        }

        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(array, writer);
        }
    }

    public static List<Hologram> importFromJson(Path file, ServerWorld world) throws IOException {
        List<Hologram> result = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(file)) {
            JsonArray array = GSON.fromJson(reader, JsonArray.class);
            if (array == null) return result;

            for (var element : array) {
                JsonObject obj = element.getAsJsonObject();
                String type = obj.get("type").getAsString();
                String id = obj.get("id").getAsString();
                Vec3d pos = new Vec3d(
                        obj.get("x").getAsDouble(),
                        obj.get("y").getAsDouble(),
                        obj.get("z").getAsDouble()
                );
                HologramAnimation animation = parseAnimation(
                        obj.has("animation") ? obj.get("animation").getAsString() : "NONE"
                );

                Hologram hologram;

                switch (type) {
                    case "text" -> {
                        Text text = Text.Serialization.fromJson(
                                obj.get("text").getAsString(), world.getRegistryManager()
                        );
                        if (text == null) text = Text.literal("???");

                        hologram = TextHologram.builder(pos)
                                .id(id)
                                .text(text)
                                .scale(obj.has("scale") ? obj.get("scale").getAsFloat() : 1.0f)
                                .billboard(obj.has("billboard") && obj.get("billboard").getAsBoolean())
                                .shadow(obj.has("shadow") && obj.get("shadow").getAsBoolean())
                                .backgroundColor(obj.has("backgroundColor") ? obj.get("backgroundColor").getAsInt() : 0)
                                .lineWidth(obj.has("lineWidth") ? obj.get("lineWidth").getAsInt() : 200)
                                .build(world);
                    }
                    case "block" -> {
                        Identifier blockId = Identifier.tryParse(obj.get("block").getAsString());
                        Block block = blockId != null ? Registries.BLOCK.get(blockId) : null;
                        if (block == null) continue;

                        hologram = BlockHologram.builder(pos)
                                .id(id)
                                .block(block)
                                .billboard(obj.has("billboard") && obj.get("billboard").getAsBoolean())
                                .build(world);
                    }
                    case "item" -> {
                        Identifier itemId = Identifier.tryParse(obj.get("item").getAsString());
                        Item item = itemId != null ? Registries.ITEM.get(itemId) : null;
                        if (item == null) continue;

                        hologram = ItemHologram.builder(pos)
                                .id(id)
                                .item(item)
                                .billboard(obj.has("billboard") && obj.get("billboard").getAsBoolean())
                                .build(world);
                    }
                    default -> {
                        continue;
                    }
                }

                hologram.setAnimation(animation);
                result.add(hologram);
            }
        }

        return result;
    }

    // -------------------------------------------------------------------
    //  Helpers
    // -------------------------------------------------------------------

    private static HologramAnimation parseAnimation(String name) {
        try {
            return HologramAnimation.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return HologramAnimation.NONE;
        }
    }
}
