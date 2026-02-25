package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ConfigAccessorFactory}.
 */
public class ConfigAccessorFactoryComponent extends AbstractCoreComponent {

    /**
     * Create a new ConfigAccessorFactoryComponent.
     */
    public ConfigAccessorFactoryComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final DefaultConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));

        dependencyProvider.take(DefaultConfigAccessorFactory.class, configAccessorFactory);
    }
}
