package org.betonquest.betonquest.schedule.impl.gametime.cron;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scheduler for {@link GametimeCronSchedule} that uses a Bukkit timer to check
 * all schedules every 10 ticks.
 * Uses Minecraft ticks (long) as the time type.
 */
public class GametimeCronScheduler extends Scheduler<GametimeCronSchedule, Long> {

    /**
     * Number of ticks between schedule checks.
     */
    private static final int TICK_INTERVAL = 10;

    /**
     * Custom logger instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Cache for last execution times.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Map of worlds to their last checked tick values, used to detect time jumps.
     */
    private final Map<World, Long> lastCheckedTicks;

    /**
     * The Bukkit scheduler task for the tick-based checker.
     */
    private BukkitTask tickTask;

    /**
     * Create a new gametime cron scheduler.
     *
     * @param log                the logger that will be used for logging
     * @param actionManager      the class for executing actions
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public GametimeCronScheduler(final BetonQuestLogger log, final ActionManager actionManager,
                                 final LastExecutionCache lastExecutionCache) {
        super(log, actionManager);
        this.log = log;
        this.lastExecutionCache = lastExecutionCache;
        this.lastCheckedTicks = new ConcurrentHashMap<>();
    }

    @Override
    public void start(final Long now) {
        log.debug("Starting gametime cron scheduler.");
        // Start the tick-based checker
        tickTask = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(),
                this::checkSchedules, TICK_INTERVAL, TICK_INTERVAL);
        log.debug("Started tick-based schedule checker (every " + TICK_INTERVAL + " ticks).");
        // Catch up missed schedules
        catchupMissedSchedules();
        super.start(now);
        log.debug("Gametime cron scheduler start complete.");
    }

    @Override
    protected Long getNow() {
        // Return 0 as a placeholder; gametime doesn't use a single "now" value
        return 0L;
    }

    @Override
    public void stop() {
        // Cancel the tick task
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        // Clear the last checked ticks map
        lastCheckedTicks.clear();
        super.stop();
        log.debug("Gametime cron scheduler stopped.");
    }

    /**
     * Check all schedules to see if any should be executed.
     * This method is called every {@link #TICK_INTERVAL} ticks.
     */
    private void checkSchedules() {
        for (final GametimeCronSchedule schedule : new ArrayList<>(schedules.values())) {
            final World world = schedule.getWorld();
            final long currentTick = world.getTime();
            final long lastTick = lastCheckedTicks.getOrDefault(world, -1L);

            // If there was a time jump (tick value decreased), handle catchup
            if (lastTick >= 0 && currentTick < lastTick) {
                handleTimeJump(lastTick, currentTick, schedule);
            }
            lastCheckedTicks.put(world, currentTick);

            // Check if the schedule should execute now
            if (schedule.shouldExecuteNow()) {
                log.debug(schedule.getId().getPackage(),
                        "Schedule '" + schedule.getId() + "' execution time reached at tick " + currentTick);
                lastExecutionCache.cacheRawExecutionTime(schedule.getId(), String.valueOf(currentTick));
                executeActions(schedule);
            }
        }
    }

    /**
     * Handle a time jump in the world, checking for missed schedules.
     *
     * @param lastTick    the last checked tick value
     * @param currentTick the current tick value
     * @param schedule    the schedule to check
     */
    private void handleTimeJump(final long lastTick, final long currentTick, final GametimeCronSchedule schedule) {
        if (schedule.getCatchup() == CatchupStrategy.NONE) {
            return;
        }
        log.debug(schedule.getId().getPackage(),
                "Time jump detected for schedule '" + schedule.getId() + "', checking for missed executions...");
        // Check for missed executions
        checkMissedExecutions(schedule, lastTick, currentTick);
    }

    /**
     * Check for missed executions between two tick values.
     *
     * @param schedule the schedule to check
     * @param fromTick the starting tick
     * @param toTick   the ending tick
     */
    private void checkMissedExecutions(final GametimeCronSchedule schedule, final long fromTick, final long toTick) {
        // Calculate the number of full days that passed
        final long ticksPerDay = 24_000;
        final long fromDay = fromTick / ticksPerDay;
        final long toDay = toTick / ticksPerDay;
        final long daysPassed = toDay - fromDay;

        if (daysPassed <= 0) {
            // No full day passed
            return;
        }

        // For each day that passed, check if the schedule should have executed
        for (long day = fromDay; day <= toDay; day++) {
            final GametimeCronSchedule.Time time = GametimeCronSchedule.getCurrentTime(schedule.getWorld());
            final LocalDateTime checkTime = LocalDateTime.of(2000, 1, 1, time.hour(), time.minute());
            final ZonedDateTime zonedTime = checkTime.atZone(ZoneId.of("UTC"));
            if (schedule.getExecutionTime().isMatch(zonedTime) && (schedule.getCatchup() == CatchupStrategy.ALL || day == toDay)) {
                lastExecutionCache.cacheRawExecutionTime(schedule.getId(), String.valueOf(toTick));
                executeActions(schedule);
                log.debug(schedule.getId().getPackage(),
                        "Caught up missed execution for schedule '" + schedule.getId() + "'");
            }
        }
    }

    /**
     * Catch up missed schedules on startup.
     */
    private void catchupMissedSchedules() {
        for (final GametimeCronSchedule schedule : schedules.values()) {
            if (schedule.getCatchup() == CatchupStrategy.NONE) {
                continue;
            }
            final Optional<String> cachedTime = lastExecutionCache.getRawLastExecutionTime(schedule.getId());
            if (cachedTime.isPresent()) {
                final long cachedTick;
                try {
                    cachedTick = Long.parseLong(cachedTime.get());
                } catch (final NumberFormatException e) {
                    log.warn(schedule.getId().getPackage(),
                            "Could not parse cached execution time for schedule '" + schedule.getId() + "': " + cachedTime.get());
                    continue;
                }
                final long currentTick = schedule.getCurrentTicks();
                if (currentTick != cachedTick && schedule.shouldExecuteNow()) {
                    lastExecutionCache.cacheRawExecutionTime(schedule.getId(), String.valueOf(currentTick));
                    executeActions(schedule);
                    log.debug(schedule.getId().getPackage(),
                            "Caught up schedule '" + schedule.getId() + "' on startup");
                }
            }
        }
    }
}
