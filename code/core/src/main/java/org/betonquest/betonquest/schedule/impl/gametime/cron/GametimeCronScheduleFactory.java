package org.betonquest.betonquest.schedule.impl.gametime.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;

/**
 * Factory for creating {@link GametimeCronSchedule} instances.
 * Parses the world parameter (required) and catchup_skip options (optional).
 */
public class GametimeCronScheduleFactory extends BaseScheduleFactory<GametimeCronSchedule> {

    /**
     * Create a new gametime cron schedule factory.
     */
    public GametimeCronScheduleFactory() {
        super();
    }

    @Override
    public GametimeCronSchedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction) throws QuestException {
        final BaseScheduleFactory.ScheduleData scheduleData = parseScheduleDataWithWorld(instruction);
        try {
            final Cron timeCron = new CronParser(CronSchedule.DEFAULT_CRON_DEFINITION).parse(scheduleData.time()).validate();
            final ExecutionTime executionTime = ExecutionTime.forCron(timeCron);
            return new GametimeCronSchedule(
                    scheduleID,
                    scheduleData.actions(),
                    scheduleData.catchup(),
                    timeCron,
                    executionTime,
                    scheduleData.world().orElseThrow(() -> new QuestException("World is required for gametime-cron schedules")),
                    scheduleData.skipOptions()
            );
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Time is no valid cron syntax: '" + scheduleData.time() + "'", e);
        }
    }
}
