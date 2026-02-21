package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.DefaultInstructions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
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
        super();
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Instructions.class);
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, ArgumentParsers.class,
                PlaceholderProcessor.class);
    }

    @Override
    public boolean isLoaded() {
        return instructions != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ArgumentParsers argumentParsers = getDependency(ArgumentParsers.class);
        final PlaceholderProcessor placeholderProcessor = getDependency(PlaceholderProcessor.class);

        this.instructions = new DefaultInstructions(() -> placeholderProcessor, () -> questPackageManager, () -> argumentParsers, () -> loggerFactory);

        providerCallback.take(Instructions.class, instructions);
    }
}
