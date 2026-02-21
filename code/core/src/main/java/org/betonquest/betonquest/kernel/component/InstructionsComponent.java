package org.betonquest.betonquest.kernel.component;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.DefaultInstructions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Instructions}.
 */
public class InstructionsComponent extends AbstractCoreComponent {

    /**
     * The default instructions implementation to load.
     */
    @Nullable
    private DefaultInstructions instructions;

    /**
     * Create a new InstructionsComponent.
     */
    public InstructionsComponent() {
        super(true);
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class);
    }

    @Override
    public boolean isLoaded() {
        return instructions != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        this.instructions = new DefaultInstructions(Suppliers.memoize(() -> getDependency(PlaceholderProcessor.class)),
                () -> questPackageManager,
                Suppliers.memoize(() -> getDependency(ArgumentParsers.class)),
                () -> loggerFactory);

        dependencyProvider.take(DefaultInstructions.class, instructions);
    }
}
