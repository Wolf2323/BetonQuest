package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.item.BrewItemFactory;
import org.betonquest.betonquest.compatibility.brewery.item.BrewQuestItemSerializer;

/**
 * Integrator for the Brewery plugin.
 */
public class BreweryIntegrator implements Integrator {

    /**
     * Create a new Brewery Integrator.
     */
    public BreweryIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ConditionRegistry conditionRegistry = api.conditions().registry();
        conditionRegistry.register("drunk", new DrunkConditionFactory());
        conditionRegistry.register("drunkquality", new DrunkQualityConditionFactory());

        final ItemRegistry item = api.items().registry();
        item.register("brew", new BrewItemFactory());
        item.registerSerializer("brew", new BrewQuestItemSerializer());
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
