package org.betonquest.betonquest.schedule.impl.gametime.daily;

import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.bukkit.World;

import java.util.List;

/**
 * A schedule that runs at a specific in-game time of day.
 * Time is defined in Minecraft ticks (0-24000), where 24000 ticks = 1 Minecraft day.
 * 1000 ticks = 1 Minecraft hour, so midnight is 0, noon is 6000, etc.
 */
public class GametimeDailySchedule extends Schedule {

    /**
     * Ticks in a full Minecraft day.
     */
    public static final long TICKS_PER_DAY = 24_000;

    /**
     * The world this schedule applies to.
     */
    private final String worldName;

    /**
     * Target time in ticks (0-24000) when the actions should run.
     */
    private final long targetTicks;

    /**
     * Creates new instance of the schedule.
     *
     * @param scheduleID  the schedule id
     * @param actions     the actions to execute
     * @param catchup     the catchup strategy
     * @param worldName   the world this schedule applies to
     * @param targetTicks the target time in ticks (0-24000)
     */
    public GametimeDailySchedule(final ScheduleIdentifier scheduleID, final List<ActionIdentifier> actions,
                                 final CatchupStrategy catchup, final String worldName, final long targetTicks) {
        super(scheduleID, actions, catchup);
        this.worldName = worldName;
        this.targetTicks = targetTicks;
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
     * Get the target time in ticks.
     *
     * @return the target ticks (0-24000)
     */
    public long getTargetTicks() {
        return targetTicks;
    }

    /**
     * Check if the current world time matches the target time.
     *
     * @param world the world to check
     * @return true if the current time matches the target time
     */
    public boolean isTimeReached(final World world) {
        return world.getTime() == targetTicks;
    }

    /**
     * Calculate the number of ticks until the next execution.
     *
     * @param world the world to check
     * @return ticks until next execution (0 if already reached)
     */
    public long getTicksUntilNext(final World world) {
        final long currentTime = world.getTime();
        if (currentTime == targetTicks) {
            return 0;
        }
        long diff = targetTicks - currentTime;
        if (diff < 0) {
            diff += TICKS_PER_DAY;
        }
        return diff;
    }

    /**
     * Get the number of ticks that have passed since the last execution.
     *
     * @param world         the world to check
     * @param lastExecution the last execution time in ticks
     * @return ticks passed since last execution
     */
    public long getTicksPassedSince(final World world, final long lastExecution) {
        final long currentTime = world.getTime();
        long diff = currentTime - lastExecution;
        if (diff < 0) {
            diff += TICKS_PER_DAY;
        }
        return diff;
    }
}
