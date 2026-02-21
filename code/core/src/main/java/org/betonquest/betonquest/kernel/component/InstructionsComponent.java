package org.betonquest.betonquest.kernel.component;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.DefaultInstructions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.LoadedDependency;
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
    public <U> void inject(final Class<U> dependencyClass, final U component) {
        this.injectedDependencies.add(new LoadedDependency<>(dependencyClass, component));
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
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        this.instructions = new DefaultInstructions(Suppliers.memoize(() -> getDependency(PlaceholderProcessor.class)),
                () -> questPackageManager,
                Suppliers.memoize(() -> getDependency(ArgumentParsers.class)),
                () -> loggerFactory);

        providerCallback.take(Instructions.class, instructions);
    }
}
