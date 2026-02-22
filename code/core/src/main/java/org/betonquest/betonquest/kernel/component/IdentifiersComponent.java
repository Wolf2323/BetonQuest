package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link IdentifierTypeRegistry}.
 */
public class IdentifiersComponent extends AbstractCoreComponent {

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
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final IdentifierTypeRegistry identifierTypeRegistry = new IdentifierTypeRegistry(loggerFactory.create(IdentifierTypeRegistry.class));

        dependencyProvider.take(IdentifierTypeRegistry.class, identifierTypeRegistry);
    }
}
