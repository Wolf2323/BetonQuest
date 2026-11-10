package org.betonquest.betonquest.schedule.impl.gametime.cron;

/**
 * Options for skipping catchup executions based on the cause of a time jump.
 *
 * @param skipBed     whether to skip catchup when time was changed by sleeping in a bed
 * @param skipCommand whether to skip catchup when time was changed by a command (e.g., /time set)
 * @param skipPlugin  whether to skip catchup when time was changed by a plugin
 */
public record CatchupSkipOptions(boolean skipBed, boolean skipCommand, boolean skipPlugin) {

    /**
     * Default options that skip no time jumps.
     */
    public static final CatchupSkipOptions NONE = new CatchupSkipOptions(false, false, false);

    /**
     * Parse skip options from a comma-separated string.
     * Supported options: -bed, -command, -plugin.
     *
     * @param options the options string, e.g., "-bed,-command"
     * @return the parsed skip options
     */
    public static CatchupSkipOptions parse(final String options) {
        boolean skipBed = false;
        boolean skipCommand = false;
        boolean skipPlugin = false;
        for (final String option : options.split(",")) {
            final String trimmed = option.trim();
            switch (trimmed) {
                case "-bed" -> skipBed = true;
                case "-command" -> skipCommand = true;
                case "-plugin" -> skipPlugin = true;
                default -> {
                    // ignore unknown options
                }
            }
        }
        return new CatchupSkipOptions(skipBed, skipCommand, skipPlugin);
    }
}
