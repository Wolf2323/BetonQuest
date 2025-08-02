package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link BreweryIntegrator} instances.
 */
public class BreweryIntegratorFactory implements IntegratorFactory {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin        the plugin instance
     * @param loggerFactory the logger factory used by BetonQuest
     */
    public BreweryIntegratorFactory(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Integrator getIntegrator() {
        return new BreweryIntegrator(plugin, loggerFactory);
    }
}
