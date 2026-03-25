package dev.tabplus.easyhologram.manager;

import dev.tabplus.easyhologram.EasyHologram;
import dev.tabplus.easyhologram.api.Hologram;
import dev.tabplus.easyhologram.event.HologramEvents;
import dev.tabplus.easyhologram.util.HologramSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Central registry for all holograms.
 * <p>
 * Thread-safe: holograms can be registered/unregistered from any thread,
 * but entity operations (spawn/destroy) are batched and executed on the
 * main server thread during {@link #tick()}.
 */
public class HologramManager {

    private static final String SAVE_KEY = "easyhologram_data";

    private final MinecraftServer server;
    private final ConcurrentHashMap<String, Hologram> holograms = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Hologram> pendingSpawn = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Hologram> pendingRemoval = new CopyOnWriteArrayList<>();

    private long tickCount = 0;

    public HologramManager(MinecraftServer server) {
        this.server = server;
    }

    // ===================================================================
    //  Public API
    // ===================================================================

    /**
     * Registers a hologram and queues it for spawning on the next tick.
     * Safe to call from any thread.
     */
    public void register(Hologram hologram) {
        if (holograms.containsKey(hologram.getId())) {
            EasyHologram.LOGGER.warn("Hologram '{}' already exists, replacing.", hologram.getId());
            unregister(hologram.getId());
        }

        holograms.put(hologram.getId(), hologram);
        pendingSpawn.add(hologram);
    }

    /**
     * Queues a hologram for removal on the next tick.
     * Safe to call from any thread.
     */
    public void unregister(String id) {
        Hologram hologram = holograms.remove(id);
        if (hologram != null) {
            HologramEvents.BEFORE_REMOVE.invoker().onRemove(hologram);
            pendingRemoval.add(hologram);
        }
    }

    /**
     * Returns a hologram by its ID, or null if not found.
     */
    public Hologram get(String id) {
        return holograms.get(id);
    }

    /**
     * Returns an unmodifiable view of all registered holograms.
     */
    public Collection<Hologram> getAll() {
        return Collections.unmodifiableCollection(holograms.values());
    }

    /**
     * Returns the total number of registered holograms.
     */
    public int count() {
        return holograms.size();
    }

    /**
     * Removes all holograms in a specific world.
     */
    public void clearWorld(ServerWorld world) {
        List<String> toRemove = holograms.values().stream()
                .filter(h -> h.getWorld() == world)
                .map(Hologram::getId)
                .collect(Collectors.toList());

        toRemove.forEach(this::unregister);
    }

    /**
     * Removes all holograms across all worlds.
     */
    public void clearAll() {
        List<String> ids = new ArrayList<>(holograms.keySet());
        ids.forEach(this::unregister);
    }

    // ===================================================================
    //  Tick Processing (main thread only)
    // ===================================================================

    /**
     * Called every server tick from the main thread.
     * Processes pending spawns, removals, and animations.
     */
    public void tick() {
        tickCount++;

        // Spawn queued holograms
        if (!pendingSpawn.isEmpty()) {
            for (Hologram hologram : pendingSpawn) {
                hologram.spawn();
                HologramEvents.AFTER_CREATE.invoker().onCreate(hologram);
            }
            pendingSpawn.clear();
        }

        // Destroy queued holograms
        if (!pendingRemoval.isEmpty()) {
            for (Hologram hologram : pendingRemoval) {
                hologram.destroy();
            }
            pendingRemoval.clear();
        }

        // Tick animations and fire events
        for (Hologram hologram : holograms.values()) {
            if (hologram.isAlive()) {
                hologram.tickAnimation(tickCount);
                if (hologram.getUpdateInterval() > 0 && tickCount % hologram.getUpdateInterval() == 0) {
                    if (hologram.getUpdateListener() != null) {
                        hologram.getUpdateListener().accept(hologram);
                    }
                }
                HologramEvents.ON_TICK.invoker().onTick(hologram, tickCount);
            }
        }
    }

    // ===================================================================
    //  Persistence (NBT via PersistentState)
    // ===================================================================

    /**
     * Saves all persistent holograms to the Overworld's PersistentState.
     */
    public void saveAll() {
        ServerWorld overworld = server.getOverworld();
        if (overworld == null) return;

        PersistentStateManager stateManager = overworld.getPersistentStateManager();
        HologramPersistentState state = stateManager.getOrCreate(
                HologramPersistentState.getPersistentStateType(),
                SAVE_KEY
        );

        state.setHolograms(HologramSerializer.toNbtList(holograms.values()));
        state.markDirty();
    }

    /**
     * Loads all saved holograms from the Overworld's PersistentState.
     */
    public void loadAll() {
        ServerWorld overworld = server.getOverworld();
        if (overworld == null) return;

        PersistentStateManager stateManager = overworld.getPersistentStateManager();
        HologramPersistentState state = stateManager.getOrCreate(
                HologramPersistentState.getPersistentStateType(),
                SAVE_KEY
        );

        NbtList saved = state.getHolograms();
        if (saved.isEmpty()) return;

        List<Hologram> loaded = HologramSerializer.fromNbtList(saved, overworld);
        for (Hologram hologram : loaded) {
            register(hologram);
        }
    }

    // ===================================================================
    //  JSON Export/Import
    // ===================================================================

    /**
     * Exports all persistent holograms to a JSON file for manual editing.
     */
    public void exportToJson(Path file) throws IOException {
        HologramSerializer.exportToJson(holograms.values(), file);
    }

    /**
     * Imports holograms from a JSON file and registers them.
     */
    public void importFromJson(Path file, ServerWorld world) throws IOException {
        List<Hologram> imported = HologramSerializer.importFromJson(file, world);
        for (Hologram hologram : imported) {
            register(hologram);
        }
    }

    // ===================================================================
    //  Shutdown
    // ===================================================================

    /**
     * Called when the server is stopping. Destroys all entities.
     */
    public void shutdown() {
        for (Hologram hologram : holograms.values()) {
            hologram.destroy();
        }
        holograms.clear();
        pendingSpawn.clear();
        pendingRemoval.clear();
    }

    // ===================================================================
    //  PersistentState inner class
    // ===================================================================

    /**
     * Stores hologram data in the world save directory.
     */
    public static class HologramPersistentState extends PersistentState {

        private NbtList hologramData = new NbtList();

        public HologramPersistentState() {}

        public static HologramPersistentState fromNbt(NbtCompound nbt) {
            HologramPersistentState state = new HologramPersistentState();
            state.hologramData = nbt.getList("holograms", NbtCompound.COMPOUND_TYPE);
            return state;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
            nbt.put("holograms", hologramData);
            return nbt;
        }

        public NbtList getHolograms() {
            return hologramData;
        }

        public void setHolograms(NbtList data) {
            this.hologramData = data;
        }

        public static PersistentState.Type<HologramPersistentState> getPersistentStateType() {
            return new PersistentState.Type<>(
                    HologramPersistentState::new,
                    (nbt, registryLookup) -> HologramPersistentState.fromNbt(nbt),
                    null
            );
        }
    }
}
