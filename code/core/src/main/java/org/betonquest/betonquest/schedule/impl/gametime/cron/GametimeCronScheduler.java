package org.betonquest.betonquest.schedule.impl.gametime.cron;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The scheduler for {@link GametimeCronSchedule}.
 * Uses a Bukkit repeating task to check every tick if the cron expression matches the current in-game time.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class GametimeCronScheduler extends Scheduler<GametimeCronSchedule, Long> {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Map of schedule keys to the last executed minute (hour * 60 + minute).
     */
    private final Map<String, Integer> lastExecutedMinute;

    /**
     * The Bukkit task that checks for schedule execution.
     */
    @SuppressWarnings("NullAway.Init")
    private BukkitTask checkTask;

    /**
     * Create a new gametime cron scheduler.
     *
     * @param log           the logger that will be used for logging
     * @param actionManager the class for executing actions
     */
    public GametimeCronScheduler(final BetonQuestLogger log, final ActionManager actionManager) {
        super(log, actionManager);
        this.log = log;
        this.lastExecutedMinute = new HashMap<>();
    }

    @Override
    public void start(final Long now) {
        log.debug("Starting gametime cron scheduler.");
        super.start(now);
        // Schedule a repeating task that runs every tick
        checkTask = Bukkit.getScheduler()
                .runTaskTimer(org.betonquest.betonquest.BetonQuest.getInstance(), this::checkSchedules, 0L, 1L);
        log.debug("Gametime cron scheduler start complete.");
    }

    @Override
    protected Long getNow() {
        return 0L;
    }

    /**
     * Check all schedules to see if any should be executed.
     */
    private void checkSchedules() {
        for (final GametimeCronSchedule schedule : schedules.values()) {
            final World world = Bukkit.getWorld(schedule.getWorldName());
            if (world == null) {
                log.warn("World '" + schedule.getWorldName() + "' not found for schedule '" + schedule.getId() + "'");
                continue;
            }

            final String scheduleKey = schedule.getId().getFull();
            final ZonedDateTime gameTime = GametimeCronSchedule.worldTimeToZonedDateTime(world);
            final int currentMinute = gameTime.getHour() * 60 + gameTime.getMinute();

            // Check if we've already executed this schedule for the current minute
            if (lastExecutedMinute.containsKey(scheduleKey)) {
                final int lastMinute = lastExecutedMinute.get(scheduleKey);
                // If the current minute is the same as the last executed minute, skip
                if (lastMinute == currentMinute) {
                    continue;
                }
                // If the current minute is before the last executed minute, a new day has started
                // This is fine, we can execute again
            }

            // Check if the cron expression matches the current time
            if (schedule.shouldExecute(world)) {
                lastExecutedMinute.put(scheduleKey, currentMinute);
                executeActions(schedule);
                log.debug(schedule.getId().getPackage(),
                        "Schedule '" + schedule.getId() + "' executed at game time " + gameTime.getHour() + ":" + gameTime.getMinute());
            }
        }
    }

    @Override
    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
        lastExecutedMinute.clear();
        super.stop();
        log.debug("Gametime cron scheduler stopped.");
    }
}
