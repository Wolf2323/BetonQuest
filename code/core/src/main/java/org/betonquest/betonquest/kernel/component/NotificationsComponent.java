package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link NotifyIORegistry}.
 */
public class NotificationsComponent extends AbstractCoreComponent {

    /**
     * Create a new NotificationsComponent.
     */
    public NotificationsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(NotifyIORegistry.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final NotifyIORegistry notifyIORegistry = new NotifyIORegistry(loggerFactory.create(NotifyIORegistry.class));

        dependencyProvider.take(NotifyIORegistry.class, notifyIORegistry);
    }
}
