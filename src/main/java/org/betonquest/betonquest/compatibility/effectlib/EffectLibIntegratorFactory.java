package org.betonquest.betonquest.compatibility.effectlib;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link EffectLibIntegrator} instances.
 */
public class EffectLibIntegratorFactory implements IntegratorFactory {
    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

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
     * @param betonQuest    the BetonQuest instance
     * @param plugin        the plugin instance
     * @param loggerFactory the logger factory used by BetonQuest
     */
    public EffectLibIntegratorFactory(final BetonQuest betonQuest, final Plugin plugin, final BetonQuestLoggerFactory loggerFactory) {
        this.betonQuest = betonQuest;
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Integrator getIntegrator() {
        return new EffectLibIntegrator(betonQuest, plugin, loggerFactory);
    }
}
