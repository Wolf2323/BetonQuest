package org.betonquest.betonquest.schedule.impl.gametime.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.bukkit.World;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * A schedule that runs actions based on Minecraft ingame time using cron syntax.
 * Extends {@link CronSchedule} to reuse cron expression parsing and execution time calculation.
 * The cron expression maps to Minecraft time where seconds=minutes and minutes=hours,
 * and the hours field is ignored (Minecraft has only 24 hours per day).
 */
public class GametimeCronSchedule extends CronSchedule {

    /**
     * Number of Minecraft ticks in one full day (24 hours).
     */
    private static final long TICKS_PER_DAY = 24_000;

    /**
     * Offset added to world time to get the correct hour (6 AM = 0 ticks).
     */
    private static final long TIME_OFFSET = 6000;

    /**
     * The world whose ingame time is used for scheduling.
     */
    private final World world;

    /**
     * Options for skipping catchup based on time jump cause.
     */
    private final CatchupSkipOptions catchupSkipOptions;

    /**
     * Creates a new gametime cron schedule.
     *
     * @param scheduleID         the schedule id
     * @param actions            the actions to execute
     * @param catchup            the catchup strategy
     * @param timeCron           the parsed cron expression
     * @param executionTime      the execution time helper from cron-utils
     * @param world              the world whose ingame time is used
     * @param catchupSkipOptions options for skipping catchup based on time jump cause
     */
    public GametimeCronSchedule(final ScheduleIdentifier scheduleID, final List<ActionIdentifier> actions,
                                final CatchupStrategy catchup, final Cron timeCron, final ExecutionTime executionTime,
                                final World world, final CatchupSkipOptions catchupSkipOptions) {
        super(scheduleID, actions, catchup, timeCron, executionTime);
        this.world = world;
        this.catchupSkipOptions = catchupSkipOptions;
    }

    /**
     * Convert the current world time to hours and minutes.
     * Uses the same logic as {@code world.getTime() + 6000} to handle the Minecraft time offset.
     *
     * @param world the world to get time from
     * @return the current time as hours and minutes
     */
    public static Time getCurrentTime(final World world) {
        final long time = world.getTime() + TIME_OFFSET;
        final int hours = (int) (time / 1000) % 24;
        final int minutes = (int) (time % 1000) * 60 / 1000;
        return new Time(hours, minutes);
    }

    /**
     * Convert hours and minutes to Minecraft ticks.
     *
     * @param hours   the hour (0-23)
     * @param minutes the minute (0-59)
     * @return the corresponding Minecraft tick value
     */
    public static long timeToTicks(final int hours, final int minutes) {
        return ((hours * 60L + minutes) * TICKS_PER_DAY / 1440) - TIME_OFFSET;
    }

    /**
     * Get the world whose ingame time is used for scheduling.
     *
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get the options for skipping catchup based on time jump cause.
     *
     * @return the skip options
     */
    public CatchupSkipOptions getCatchupSkipOptions() {
        return catchupSkipOptions;
    }

    /**
     * Get the current world time in ticks.
     *
     * @return the current world time in ticks
     */
    public long getCurrentTicks() {
        return world.getTime();
    }

    /**
     * Calculate the next execution time in Minecraft ticks based on the cron expression.
     *
     * @return the next execution time in ticks, or empty if no next execution
     */
    public Optional<Long> getNextExecutionTicks() {
        final Time currentTime = getCurrentTime(world);
        // Create a LocalDateTime from the current Minecraft time
        final LocalDateTime now = LocalDateTime.of(2000, 1, 1, currentTime.hour(), currentTime.minute());
        final ZonedDateTime zonedNow = now.atZone(ZoneId.of("UTC"));

        return getExecutionTime().nextExecution(zonedNow).map(nextExec -> {
            // Convert the next execution time back to Minecraft ticks
            return timeToTicks(nextExec.getHour(), nextExec.getMinute());
        });
    }

    /**
     * Check if the current world time matches this schedule's cron expression.
     *
     * @return true if the schedule should be executed now
     */
    public boolean shouldExecuteNow() {
        final Time currentTime = getCurrentTime(world);
        final LocalDateTime now = LocalDateTime.of(2000, 1, 1, currentTime.hour(), currentTime.minute());
        return getExecutionTime().isMatch(now.atZone(ZoneId.of("UTC")));
    }

    /**
     * Time representation with hours and minutes for Minecraft ingame time.
     *
     * @param hour   the hour (0-23)
     * @param minute the minute (0-59)
     */
    public record Time(int hour, int minute) {

    }
}
