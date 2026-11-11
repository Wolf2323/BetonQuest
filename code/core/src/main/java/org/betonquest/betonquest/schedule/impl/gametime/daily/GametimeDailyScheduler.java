package org.betonquest.betonquest.schedule.impl.gametime.daily;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * The scheduler for {@link GametimeDailySchedule}.
 * Uses a Bukkit repeating task to check every tick if the target time has been reached.
 */
public class GametimeDailyScheduler extends Scheduler<GametimeDailySchedule, Long> {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Map of schedule IDs to their last execution time in ticks.
     */
    private final Map<String, Long> lastExecutionTimes;

    /**
     * The Bukkit task that checks for schedule execution.
     */
    @SuppressWarnings("NullAway.Init")
    private BukkitTask checkTask;

    /**
     * Create a new gametime daily scheduler.
     *
     * @param log           the logger that will be used for logging
     * @param actionManager the class for executing actions
     */
    public GametimeDailyScheduler(final BetonQuestLogger log, final ActionManager actionManager) {
        super(log, actionManager);
        this.log = log;
        this.lastExecutionTimes = new HashMap<>();
    }

    @Override
    public void start(final Long now) {
        log.debug("Starting gametime daily scheduler.");
        catchupMissedSchedules();
        super.start(now);
        // Schedule a repeating task that runs every tick
        checkTask = Bukkit.getScheduler()
                .runTaskTimer(org.betonquest.betonquest.BetonQuest.getInstance(), this::checkSchedules, 0L, 1L);
        log.debug("Gametime daily scheduler start complete.");
    }

    @Override
    protected Long getNow() {
        return 0L;
    }

    /**
     * Check all schedules to see if any should be executed.
     */
    private void checkSchedules() {
        for (final GametimeDailySchedule schedule : schedules.values()) {
            final World world = Bukkit.getWorld(schedule.getWorldName());
            if (world == null) {
                log.warn("World '" + schedule.getWorldName() + "' not found for schedule '" + schedule.getId() + "'");
                continue;
            }

            final String scheduleKey = schedule.getId().getFull();
            final long currentTime = world.getTime();

            // Check if we've already executed this schedule for the current day
            if (lastExecutionTimes.containsKey(scheduleKey)) {
                final long lastExecution = lastExecutionTimes.get(scheduleKey);
                // If the current time is before the last execution time, a new day has started
                if (currentTime >= lastExecution) {
                    // Same day, not yet wrapped around
                    continue;
                }
            }

            // Check if the target time has been reached
            if (currentTime == schedule.getTargetTicks()) {
                lastExecutionTimes.put(scheduleKey, currentTime);
                executeActions(schedule);
                log.debug(schedule.getId().getPackage(),
                        "Schedule '" + schedule.getId() + "' executed at time " + currentTime);
            }
        }
    }

    /**
     * Search for missed schedule runs during shutdown and rerun them if the catchup strategy says so.
     */
    private void catchupMissedSchedules() {
        log.debug("Collecting missed gametime schedules...");
        int caughtUp = 0;
        for (final GametimeDailySchedule schedule : schedules.values()) {
            if (schedule.getCatchup() == CatchupStrategy.NONE) {
                continue;
            }
            final World world = Bukkit.getWorld(schedule.getWorldName());
            if (world == null) {
                continue;
            }
            final long currentTime = world.getTime();
            final long targetTime = schedule.getTargetTicks();

            // For catchup, we check if the target time is within a small window of the current time
            // This handles the case where the server was restarted just after the target time
            if (currentTime == targetTime || currentTime == targetTime + 1) {
                executeActions(schedule);
                lastExecutionTimes.put(schedule.getId().getFull(), currentTime);
                caughtUp++;
                log.debug(schedule.getId().getPackage(),
                        "Schedule '" + schedule.getId() + "' caught up at time " + currentTime);
            }
        }
        log.debug("Found " + caughtUp + " missed schedule runs that will be caught up.");
    }

    @Override
    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
        lastExecutionTimes.clear();
        super.stop();
        log.debug("Gametime daily scheduler stopped.");
    }
}
