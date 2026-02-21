package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.compass.CompassIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link CompassProcessor}.
 */
public class CompassComponent extends AbstractCoreComponent {

    /**
     * The compass processor to load.
     */
    @Nullable
    private CompassProcessor compassProcessor;

    /**
     * Create a new CompassComponent.
     */
    public CompassComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, Identifiers.class, Instructions.class,
                ParsedSectionTextCreator.class);
    }

    @Override
    public boolean isLoaded() {
        return compassProcessor != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);

        final CompassIdentifierFactory compassIdentifierFactory = new CompassIdentifierFactory(questPackageManager);
        identifiers.register(CompassIdentifier.class, compassIdentifierFactory);
        this.compassProcessor = new CompassProcessor(loggerFactory.create(CompassProcessor.class),
                instructions, parsedSectionTextCreator, compassIdentifierFactory);

        providerCallback.take(CompassIdentifierFactory.class, compassIdentifierFactory);
        providerCallback.take(CompassProcessor.class, compassProcessor);
    }
}
