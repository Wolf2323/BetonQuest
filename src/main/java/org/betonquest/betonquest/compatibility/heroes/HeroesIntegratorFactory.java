package org.betonquest.betonquest.compatibility.heroes;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link HeroesIntegrator} instances.
 */
public class HeroesIntegratorFactory implements IntegratorFactory {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin        the plugin instance
     * @param loggerFactory the logger factory used by BetonQuest
     * @param server        the server instance
     */
    public HeroesIntegratorFactory(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory, final Server server) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.server = server;
    }

    @Override
    public Integrator getIntegrator() {
        return new HeroesIntegrator(plugin, loggerFactory, server);
    }
}
