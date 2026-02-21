package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link IdentifierTypeRegistry}.
 */
public class IdentifiersComponent extends AbstractCoreComponent {

    /**
     * The identifier type registry to load.
     */
    @Nullable
    private IdentifierTypeRegistry identifierTypeRegistry;

    /**
     * Create a new IdentifiersComponent.
     */
    public IdentifiersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class);
    }

    @Override
    public boolean isLoaded() {
        return identifierTypeRegistry != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        identifierTypeRegistry = new IdentifierTypeRegistry(loggerFactory.create(IdentifierTypeRegistry.class));

        providerCallback.take(IdentifierTypeRegistry.class, identifierTypeRegistry);
    }
}
