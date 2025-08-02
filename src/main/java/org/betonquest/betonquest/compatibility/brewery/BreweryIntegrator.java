package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.HasBrewConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.event.GiveBrewEventFactory;
import org.betonquest.betonquest.compatibility.brewery.event.TakeBrewEventFactory;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for the Brewery plugin.
 */
public class BreweryIntegrator implements Integrator {
    /**
     * The {@link BetonQuest} plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Brewery Integrator.
     *
     * @param plugin        the plugin instance
     * @param loggerFactory the logger factory used by BetonQuest
     */
    public BreweryIntegrator(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final EventTypeRegistry eventTypes = questTypeRegistries.event();
        eventTypes.register("givebrew", new GiveBrewEventFactory(loggerFactory, data));
        eventTypes.register("takebrew", new TakeBrewEventFactory(loggerFactory, data));

        final ConditionTypeRegistry conditionTypes = questTypeRegistries.condition();
        conditionTypes.register("drunk", new DrunkConditionFactory(loggerFactory, data));
        conditionTypes.register("drunkquality", new DrunkQualityConditionFactory(loggerFactory, data));
        conditionTypes.register("hasbrew", new HasBrewConditionFactory(loggerFactory, data));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
