package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;

/**
 * A service feature offers access to a manager and registry for a specific feature type.
 * <br> <br>
 * The registry is responsible for registering custom features.
 * Registries usually implement either the {@link CoreQuestRegistry}, the {@link FeatureTypeRegistry},
 * or the {@link FeatureRegistry} interfaces and offer varying methods for registering custom features with their
 * factories.
 * The manager is responsible for granting access to existing and loaded types
 * previously registered with the registry.
 *
 * @param <Manager>  the manager type
 * @param <Registry> the registry type
 */
public interface ServiceFeature<Manager, Registry> {

    /**
     * Get the manager for this feature.
     *
     * @return the manager instance
     */
    Manager manager();

    /**
     * Get the registry for this feature.
     *
     * @return the registry instance
     */
    Registry registry();
}
