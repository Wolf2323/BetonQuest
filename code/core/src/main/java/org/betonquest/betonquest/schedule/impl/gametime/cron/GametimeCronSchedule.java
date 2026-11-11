package org.betonquest.betonquest.schedule.impl.gametime.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.bukkit.World;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A schedule that runs at specific in-game times defined by a cron expression.
 * The cron expression is mapped to Minecraft time:
 * - Minutes: 0-59 (each Minecraft minute = 0.8333 real seconds at 20 TPS)
 * - Hours: 0-23 (Minecraft hours)
 * - Day of month: 1-31 (day of the in-game month, based on world full time)
 * - Month: 1-12 (in-game month, based on world full time)
 * - Day of week: 0-7 (in-game day of week, based on world full time, 0 and 7 are Sunday)
 * <p>
 * A Minecraft day is 24000 ticks (20 minutes real time at 20 TPS).
 * The cron expression is evaluated against the in-game time, not real time.
 * The in-game date is calculated from the world's full time (ticks since world creation),
 * starting from January 1, 1970 (Unix epoch).
 */
public class GametimeCronSchedule extends Schedule {

    /**
     * Ticks in a full Minecraft day.
     */
    public static final long TICKS_PER_DAY = 24_000;

    /**
     * Ticks per Minecraft hour.
     */
    public static final long TICKS_PER_HOUR = 1_000;

    /**
     * Ticks per Minecraft minute.
     */
    public static final long TICKS_PER_MINUTE = TICKS_PER_HOUR / 60;

    /**
     * Cron definition for gametime - all fields are meaningful.
     * Minutes, hours, day of month, month, and day of week are all evaluated
     * against the in-game time derived from the world's full time.
     */
    public static final CronDefinition GAMETIME_CRON_DEFINITION = CronDefinitionBuilder.defineCron()
            .withMinutes().withValidRange(0, 59).withStrictRange().and()
            .withHours().withValidRange(0, 23).withStrictRange().and()
            .withDayOfMonth().withValidRange(1, 31).withStrictRange().and()
            .withMonth().withValidRange(1, 12).withStrictRange().and()
            .withDayOfWeek().withValidRange(0, 7).withMondayDoWValue(1).withIntMapping(7, 0).withStrictRange().and()
            .instance();

    /**
     * The world this schedule applies to.
     */
    private final String worldName;

    /**
     * Cron expression that defines when the actions from this schedule shall run.
     */
    private final Cron timeCron;

    /**
     * Provides information when the actions from this schedule shall be executed.
     */
    private final ExecutionTime executionTime;

    /**
     * Creates new instance of the Cron schedule.
     *
     * @param scheduleID    the schedule id
     * @param actions       the actions to execute
     * @param catchup       the catchup strategy
     * @param worldName     the world this schedule applies to
     * @param timeCron      the Cron to schedule execution
     * @param executionTime the time when the schedule should be executed
     */
    public GametimeCronSchedule(final ScheduleIdentifier scheduleID, final List<ActionIdentifier> actions,
                                final CatchupStrategy catchup, final String worldName,
                                final Cron timeCron, final ExecutionTime executionTime) {
        super(scheduleID, actions, catchup);
        this.worldName = worldName;
        this.timeCron = timeCron;
        this.executionTime = executionTime;
    }

    /**
     * Convert world full time to a ZonedDateTime for cron evaluation.
     * Uses the world's full time (ticks since world creation) to calculate
     * the in-game date, starting from January 1, 1970 (Unix epoch).
     * This allows day of month, month, and day of week to be meaningful
     * in the cron expression.
     *
     * @param world the world to get time from
     * @return ZonedDateTime representing the in-game time including date
     */
    public static ZonedDateTime worldTimeToZonedDateTime(final World world) {
        final long fullTime = world.getFullTime();
        final long totalDays = fullTime / TICKS_PER_DAY;
        final long timeOfDay = fullTime % TICKS_PER_DAY;
        final int hours = (int) (timeOfDay / TICKS_PER_HOUR % 24);
        final int minutes = (int) ((timeOfDay % TICKS_PER_HOUR) / TICKS_PER_MINUTE);
        // Calculate date from days since epoch
        final java.time.LocalDate date = java.time.LocalDate.EPOCH.plusDays(totalDays);
        final java.time.LocalDateTime dateTime = date.atTime(hours, minutes, 0, 0);
        return ZonedDateTime.of(dateTime, ZoneId.of("UTC"));
    }

    /**
     * Get the world this schedule applies to.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Get the cron expression that defines when the actions from this schedule shall run.
     *
     * @return parsed cron expression
     */
    public Cron getTimeCron() {
        return timeCron;
    }

    /**
     * Get information when the actions from this schedule shall be executed.
     *
     * @return execution time helper as defined by cron-utils
     */
    public ExecutionTime getExecutionTime() {
        return executionTime;
    }

    /**
     * Check if the cron expression should execute at the current world time.
     *
     * @param world the world to check
     * @return true if the schedule should execute now
     */
    public boolean shouldExecute(final World world) {
        final ZonedDateTime gameTime = worldTimeToZonedDateTime(world);
        return executionTime.isMatch(gameTime);
    }

    /**
     * Get the next execution time as world ticks.
     *
     * @param world the world to check
     * @return the next execution time in ticks, or -1 if no next execution
     */
    public long getNextExecutionTicks(final World world) {
        final ZonedDateTime gameTime = worldTimeToZonedDateTime(world);
        final java.util.Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(gameTime);
        if (nextExecution.isPresent()) {
            final ZonedDateTime next = nextExecution.get();
            final int hours = next.getHour();
            final int minutes = next.getMinute();
            return hours * TICKS_PER_HOUR + minutes * TICKS_PER_MINUTE;
        }
        return -1;
    }
}
