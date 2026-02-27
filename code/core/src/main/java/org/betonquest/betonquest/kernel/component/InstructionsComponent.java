package org.betonquest.betonquest.kernel.component;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.DefaultInstructions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Instructions}.
 */
public class InstructionsComponent extends AbstractCoreComponent {

    /**
     * Create a new InstructionsComponent.
     */
    public InstructionsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class);
    }

    @Override
    public boolean requires(final Class<?> type) {
        return ArgumentParsers.class.isAssignableFrom(type) || PlaceholderProcessor.class.isAssignableFrom(type) || super.requires(type);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultInstructions.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final DefaultInstructions instructions = new DefaultInstructions(loggerFactory, questPackageManager,
                Suppliers.memoize(() -> getDependency(PlaceholderProcessor.class)),
                Suppliers.memoize(() -> getDependency(ArgumentParsers.class)));

        dependencyProvider.take(DefaultInstructions.class, instructions);
    }
}
