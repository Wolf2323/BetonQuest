package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link NotifyIORegistry}.
 */
public class NotificationsComponent extends AbstractCoreComponent {

    /**
     * The notification IO registry to load.
     */
    @Nullable
    private NotifyIORegistry notifyIORegistry;

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
    public boolean isLoaded() {
        return notifyIORegistry != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        this.notifyIORegistry = new NotifyIORegistry(loggerFactory.create(NotifyIORegistry.class));

        providerCallback.take(NotifyIORegistry.class, notifyIORegistry);
    }
}
