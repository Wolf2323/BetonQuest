package org.betonquest.betonquest.compatibility.magic;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link MagicIntegrator} instances.
 */
public class MagicIntegratorFactory implements IntegratorFactory {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The player data storage instance.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The plugin message instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin          the plugin instance
     * @param loggerFactory   the logger factory used by BetonQuest
     * @param profileProvider the profile provider instance
     * @param dataStorage     the player data storage instance
     * @param pluginMessage   the plugin message instance
     * @param server          the server instance
     */
    public MagicIntegratorFactory(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory,
                                  final ProfileProvider profileProvider, final PlayerDataStorage dataStorage,
                                  final PluginMessage pluginMessage, final Server server) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
        this.server = server;
    }

    @Override
    public Integrator getIntegrator() {
        return new MagicIntegrator(plugin, loggerFactory, profileProvider, dataStorage, pluginMessage, server);
    }
}
