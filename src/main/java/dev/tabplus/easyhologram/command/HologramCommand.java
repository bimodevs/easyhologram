package dev.tabplus.easyhologram.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.tabplus.easyhologram.EasyHologram;
import dev.tabplus.easyhologram.api.*;
import dev.tabplus.easyhologram.manager.HologramManager;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Built-in commands for testing and managing holograms.
 * <p>
 * All commands require OP level 2.
 * <ul>
 *   <li>/hologram create text &lt;pos&gt; &lt;text&gt;</li>
 *   <li>/hologram create block &lt;pos&gt; &lt;block&gt;</li>
 *   <li>/hologram create item &lt;pos&gt; &lt;item&gt;</li>
 *   <li>/hologram list</li>
 *   <li>/hologram remove &lt;id&gt;</li>
 *   <li>/hologram clear</li>
 *   <li>/hologram save &lt;name&gt;</li>
 *   <li>/hologram load &lt;name&gt;</li>
 * </ul>
 */
public final class HologramCommand {

    private HologramCommand() {}

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        dispatcher.register(literal("hologram")
                .requires(source -> source.hasPermissionLevel(2))

                // /hologram create text <pos> <text>
                .then(literal("create")
                        .then(literal("text")
                                .then(argument("pos", Vec3ArgumentType.vec3())
                                        .then(argument("text", StringArgumentType.greedyString())
                                                .executes(HologramCommand::createText)
                                        )
                                )
                        )
                        // /hologram create block <pos> <block>
                        .then(literal("block")
                                .then(argument("pos", Vec3ArgumentType.vec3())
                                        .then(argument("block", BlockStateArgumentType.blockState(registryAccess))
                                                .executes(HologramCommand::createBlock)
                                        )
                                )
                        )
                        // /hologram create item <pos> <item>
                        .then(literal("item")
                                .then(argument("pos", Vec3ArgumentType.vec3())
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .executes(HologramCommand::createItem)
                                        )
                                )
                        )
                )

                // /hologram list
                .then(literal("list").executes(HologramCommand::listHolograms))

                // /hologram remove <id>
                .then(literal("remove")
                        .then(argument("id", StringArgumentType.word())
                                .executes(HologramCommand::removeHologram)
                        )
                )

                // /hologram clear
                .then(literal("clear").executes(HologramCommand::clearHolograms))

                // /hologram save <name>
                .then(literal("save")
                        .then(argument("name", StringArgumentType.word())
                                .executes(HologramCommand::saveToJson)
                        )
                )

                // /hologram load <name>
                .then(literal("load")
                        .then(argument("name", StringArgumentType.word())
                                .executes(HologramCommand::loadFromJson)
                        )
                )

                // /hologram edit <id> settext <text>
                .then(literal("edit")
                        .then(argument("id", StringArgumentType.word())
                                .then(literal("settext")
                                        .then(argument("text", StringArgumentType.greedyString())
                                                .executes(HologramCommand::editText)
                                        )
                                )
                        )
                )
        );
    }

    // -------------------------------------------------------------------
    //  Command handlers
    // -------------------------------------------------------------------

    private static int createText(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        Vec3d pos = Vec3ArgumentType.getVec3(ctx, "pos");
        String rawText = StringArgumentType.getString(ctx, "text");
        ServerWorld world = source.getWorld();

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        TextHologram hologram = TextHologram.builder(pos)
                .text(rawText)
                .billboard(true)
                .shadow(true)
                .build(world);

        manager.register(hologram);
        sendSuccess(source, "Text hologram created: " + hologram.getId());
        return 1;
    }

    private static int createBlock(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        Vec3d pos = Vec3ArgumentType.getVec3(ctx, "pos");
        Block block = BlockStateArgumentType.getBlockState(ctx, "block").getBlockState().getBlock();
        ServerWorld world = source.getWorld();

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        BlockHologram hologram = BlockHologram.builder(pos)
                .block(block)
                .build(world);

        manager.register(hologram);
        sendSuccess(source, "Block hologram created: " + hologram.getId());
        return 1;
    }

    private static int createItem(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        Vec3d pos = Vec3ArgumentType.getVec3(ctx, "pos");
        ItemStack stack = ItemStackArgumentType.getItemStackArgument(ctx, "item").createStack(1, false);
        ServerWorld world = source.getWorld();

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        ItemHologram hologram = ItemHologram.builder(pos)
                .itemStack(stack)
                .billboard(true)
                .build(world);

        manager.register(hologram);
        sendSuccess(source, "Item hologram created: " + hologram.getId());
        return 1;
    }

    private static int listHolograms(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        Collection<Hologram> all = manager.getAll();
        if (all.isEmpty()) {
            sendInfo(source, "No holograms registered.");
            return 1;
        }

        sendInfo(source, "Holograms (" + all.size() + "):");
        for (Hologram h : all) {
            Vec3d p = h.getPosition();
            String line = String.format(
                    "  §e%s §7[%s] §fat %.0f %.0f %.0f §7(%s)",
                    h.getId(), h.getType(), p.x, p.y, p.z,
                    h.isAlive() ? "§aalive" : "§cpending"
            );
            source.sendFeedback(() -> Text.literal(line), false);
        }
        return 1;
    }

    private static int removeHologram(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        String id = StringArgumentType.getString(ctx, "id");

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        if (manager.get(id) == null) {
            sendError(source, "Hologram '" + id + "' not found.");
            return 0;
        }

        manager.unregister(id);
        sendSuccess(source, "Hologram '" + id + "' removed.");
        return 1;
    }

    private static int clearHolograms(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        int count = manager.count();
        manager.clearAll();
        sendSuccess(source, "Cleared " + count + " hologram(s).");
        return 1;
    }

    private static int saveToJson(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        Path dir = source.getServer().getRunDirectory().resolve("config").resolve("easyhologram");
        try {
            java.nio.file.Files.createDirectories(dir);
            Path file = dir.resolve(name + ".json");
            manager.exportToJson(file);
            sendSuccess(source, "Saved " + manager.count() + " hologram(s) to " + file.getFileName());
        } catch (IOException e) {
            sendError(source, "Failed to save: " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static int loadFromJson(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        ServerWorld world = source.getWorld();

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        Path dir = source.getServer().getRunDirectory().resolve("config").resolve("easyhologram");
        Path file = dir.resolve(name + ".json");

        if (!java.nio.file.Files.exists(file)) {
            sendError(source, "File not found: " + file.getFileName());
            return 0;
        }

        try {
            int before = manager.count();
            manager.importFromJson(file, world);
            int loaded = manager.count() - before;
            sendSuccess(source, "Loaded " + loaded + " hologram(s) from " + file.getFileName());
        } catch (IOException e) {
            sendError(source, "Failed to load: " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static int editText(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        String id = StringArgumentType.getString(ctx, "id");
        String rawText = StringArgumentType.getString(ctx, "text");

        HologramManager manager = EasyHologram.getManager();
        if (manager == null) {
            sendError(source, "Hologram manager is not initialized.");
            return 0;
        }

        Hologram hologram = manager.get(id);
        if (hologram == null) {
            sendError(source, "Hologram '" + id + "' not found.");
            return 0;
        }

        if (hologram instanceof TextHologram textHolo) {
            textHolo.setText(rawText);
            sendSuccess(source, "Text for hologram '" + id + "' updated.");
            return 1;
        } else {
            sendError(source, "Hologram '" + id + "' is not a text hologram!");
            return 0;
        }
    }

    // -------------------------------------------------------------------
    //  Message helpers
    // -------------------------------------------------------------------

    private static void sendSuccess(ServerCommandSource source, String msg) {
        source.sendFeedback(
                () -> Text.literal("[EasyHologram] ").formatted(Formatting.GOLD)
                        .append(Text.literal(msg).formatted(Formatting.GREEN)),
                false
        );
    }

    private static void sendError(ServerCommandSource source, String msg) {
        source.sendError(
                Text.literal("[EasyHologram] ").formatted(Formatting.GOLD)
                        .append(Text.literal(msg).formatted(Formatting.RED))
        );
    }

    private static void sendInfo(ServerCommandSource source, String msg) {
        source.sendFeedback(
                () -> Text.literal("[EasyHologram] ").formatted(Formatting.GOLD)
                        .append(Text.literal(msg).formatted(Formatting.WHITE)),
                false
        );
    }
}
