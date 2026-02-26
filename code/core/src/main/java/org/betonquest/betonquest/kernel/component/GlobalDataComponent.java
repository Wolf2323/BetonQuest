package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link GlobalData}.
 */
public class GlobalDataComponent extends AbstractCoreComponent {

    /**
     * Create a new GlobalDataComponent.
     */
    public GlobalDataComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, Saver.class, Connector.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(GlobalData.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Saver saver = getDependency(Saver.class);
        final Connector connector = getDependency(Connector.class);

        final GlobalData globalData = new GlobalData(loggerFactory.create(GlobalData.class), saver, connector);

        dependencyProvider.take(GlobalData.class, globalData);
    }
}
