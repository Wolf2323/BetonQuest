package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.schedule.ScheduleIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.schedule.ActionScheduling;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ScheduleRegistry}.
 */
public class SchedulesComponent extends AbstractCoreComponent {

    /**
     * The schedule registry to load.
     */
    @Nullable
    private ScheduleRegistry scheduleRegistry;

    /**
     * The schedule processor to load.
     */
    @Nullable
    private ActionScheduling scheduleProcessor;

    /**
     * Create a new SchedulesComponent.
     */
    public SchedulesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ScheduleIdentifierFactory.class, ScheduleRegistry.class, ActionScheduling.class);
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, Identifiers.class, Instructions.class);
    }

    @Override
    public boolean isLoaded() {
        return scheduleRegistry != null && scheduleProcessor != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);

        final ScheduleIdentifierFactory scheduleIdentifierFactory = new ScheduleIdentifierFactory(questPackageManager);
        identifiers.register(ScheduleIdentifier.class, scheduleIdentifierFactory);
        this.scheduleRegistry = new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class));
        this.scheduleProcessor = new ActionScheduling(loggerFactory.create(ActionScheduling.class, "Schedules"),
                instructions, scheduleRegistry, scheduleIdentifierFactory);

        providerCallback.take(ScheduleIdentifierFactory.class, scheduleIdentifierFactory);
        providerCallback.take(ScheduleRegistry.class, scheduleRegistry);
        providerCallback.take(ActionScheduling.class, scheduleProcessor);
    }
}
