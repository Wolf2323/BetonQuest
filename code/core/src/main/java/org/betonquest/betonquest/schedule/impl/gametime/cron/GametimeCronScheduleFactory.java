package org.betonquest.betonquest.schedule.impl.gametime.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Factory to create {@link GametimeCronSchedule}s.
 */
public class GametimeCronScheduleFactory extends BaseScheduleFactory<GametimeCronSchedule> {

    /**
     * Create a new Gametime Cron Schedule Factory.
     */
    public GametimeCronScheduleFactory() {
        super();
    }

    @Override
    public GametimeCronSchedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction)
            throws QuestException {
        final ScheduleData scheduleData = parseScheduleData(instruction);
        final String time = scheduleData.time();

        // Parse cron expression
        final Cron timeCron;
        final ExecutionTime executionTime;
        try {
            final CronParser parser = new CronParser(GametimeCronSchedule.GAMETIME_CRON_DEFINITION);
            timeCron = parser.parse(time).validate();
            executionTime = ExecutionTime.forCron(timeCron);
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Time is no valid cron syntax: '" + time + "'", e);
        }

        // Get world
        final String worldName = instruction.getSection().getString("world");
        if (worldName == null) {
            throw new QuestException("Missing world instruction. Specify the world this schedule applies to.");
        }

        // Validate world exists
        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new QuestException("World '" + worldName + "' does not exist");
        }

        return new GametimeCronSchedule(scheduleID, scheduleData.actions(), scheduleData.catchup(),
                worldName, timeCron, executionTime);
    }
}
