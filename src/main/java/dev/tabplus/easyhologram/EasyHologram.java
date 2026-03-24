package dev.tabplus.easyhologram;

import dev.tabplus.easyhologram.command.HologramCommand;
import dev.tabplus.easyhologram.manager.HologramManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EasyHologram — lightweight server-side hologram library for Fabric.
 * <p>
 * Uses vanilla Display Entities (TextDisplayEntity, BlockDisplayEntity,
 * ItemDisplayEntity) to create floating holograms in the world.
 * <p>
 * Other mods can depend on this library and use the API from
 * {@link dev.tabplus.easyhologram.api} package.
 */
public class EasyHologram implements ModInitializer {

    public static final String MOD_ID = "easyhologram";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static HologramManager manager;

    @Override
    public void onInitialize() {
        LOGGER.info("EasyHologram loading...");

        // Регистрируем команды для отладки
        CommandRegistrationCallback.EVENT.register(HologramCommand::register);

        // Когда сервер стартует — создаем менеджер
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            manager = new HologramManager(server);
            manager.loadAll();
            LOGGER.info("EasyHologram ready. Loaded {} hologram(s).", manager.count());
        });

        // Каждый тик обрабатываем очереди спавна/удаления и анимации
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (manager != null) {
                manager.tick();
            }
        });

        // При остановке сервера — сохраняем все голограммы
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (manager != null) {
                manager.saveAll();
                manager.shutdown();
                LOGGER.info("EasyHologram saved all holograms.");
            }
        });
    }

    /**
     * Returns the global hologram manager instance.
     * Available after the server has started.
     *
     * @return the hologram manager, or null if the server isn't running
     */
    public static HologramManager getManager() {
        return manager;
    }
}
