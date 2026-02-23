package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduleFactory;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduler;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduleFactory;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduler;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading schedule types.
 */
public class ScheduleTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new ScheduleTypesComponent.
     */
    public ScheduleTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, LastExecutionCache.class, ScheduleRegistry.class, ActionManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final LastExecutionCache lastExecutionCache = getDependency(LastExecutionCache.class);
        final ScheduleRegistry scheduleRegistry = getDependency(ScheduleRegistry.class);
        final ActionManager actionManager = getDependency(ActionManager.class);

        scheduleRegistry.register("realtime-daily", new RealtimeDailyScheduleFactory(),
                new RealtimeDailyScheduler(loggerFactory.create(RealtimeDailyScheduler.class, "Schedules"), actionManager, lastExecutionCache)
        );
        scheduleRegistry.register("realtime-cron", new RealtimeCronScheduleFactory(),
                new RealtimeCronScheduler(loggerFactory.create(RealtimeCronScheduler.class, "Schedules"), actionManager, lastExecutionCache)
        );
    }
}
