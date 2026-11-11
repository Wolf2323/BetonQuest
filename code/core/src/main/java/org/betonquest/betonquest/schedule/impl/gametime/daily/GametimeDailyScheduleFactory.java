package org.betonquest.betonquest.schedule.impl.gametime.daily;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Factory to create {@link GametimeDailySchedule}s.
 * Time can be specified in ticks (0-24000) or in HH:mm format.
 */
public class GametimeDailyScheduleFactory extends BaseScheduleFactory<GametimeDailySchedule> {

    /**
     * Ticks in a full Minecraft day.
     */
    private static final long TICKS_PER_DAY = 24_000;

    /**
     * Ticks per Minecraft hour.
     */
    private static final long TICKS_PER_HOUR = 1_000;

    /**
     * Create a new Gametime Daily Schedule Factory.
     */
    public GametimeDailyScheduleFactory() {
        super();
    }

    @Override
    public GametimeDailySchedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction)
            throws QuestException {
        final ScheduleData scheduleData = parseScheduleData(instruction);
        final String time = scheduleData.time();
        final long targetTicks;

        // Parse time - support both ticks and HH:mm format
        try {
            targetTicks = parseTime(time);
        } catch (final Exception e) {
            throw new QuestException("Unable to parse time '" + time + "': " + e.getMessage(), e);
        }

        // Validate ticks range
        if (targetTicks < 0 || targetTicks >= TICKS_PER_DAY) {
            throw new QuestException("Time must be between 0 and " + (TICKS_PER_DAY - 1) + " ticks (or HH:mm format)");
        }

        // Get world - default to first world if not specified
        final String worldName = instruction.getSection().getString("world");
        if (worldName == null) {
            throw new QuestException("Missing world instruction. Specify the world this schedule applies to.");
        }

        // Validate world exists
        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new QuestException("World '" + worldName + "' does not exist");
        }

        return new GametimeDailySchedule(scheduleID, scheduleData.actions(), scheduleData.catchup(), worldName, targetTicks);
    }

    /**
     * Parse a time string into ticks.
     * Supports:
     * - Pure number: treated as ticks (0-24000)
     * - HH:mm format: converted to ticks (e.g., "14:30" = 14500 ticks)
     * - HH format: converted to ticks (e.g., "14" = 14000 ticks)
     *
     * @param time the time string
     * @return the time in ticks
     */
    private long parseTime(final String time) {
        // Check if it's a pure number (ticks)
        try {
            final long ticks = Long.parseLong(time);
            return ticks;
        } catch (final NumberFormatException e) {
            // Not a pure number, try HH:mm format
        }

        // Try HH:mm format
        final String[] parts = time.split(":");
        if (parts.length == 2) {
            final int hours = Integer.parseInt(parts[0]);
            final int minutes = Integer.parseInt(parts[1]);
            validateTime(hours, minutes);
            return hours * TICKS_PER_HOUR + minutes * TICKS_PER_HOUR / 60;
        }

        // Try HH format (just hours)
        if (parts.length == 1) {
            final int hours = Integer.parseInt(parts[0]);
            validateTime(hours, 0);
            return hours * TICKS_PER_HOUR;
        }

        throw new IllegalArgumentException("Invalid time format. Expected ticks (0-24000) or HH:mm format");
    }

    /**
     * Validate hour and minute values.
     *
     * @param hours   the hour (0-23)
     * @param minutes the minute (0-59)
     */
    private void validateTime(final int hours, final int minutes) {
        if (hours < 0 || hours > 24) {
            throw new IllegalArgumentException("Hour must be between 0 and 24");
        }
        if (hours == 24 && minutes != 0) {
            throw new IllegalArgumentException("Hour 24 cannot have any minutes");
        }
        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Minute must be between 0 and 59");
        }
    }
}
