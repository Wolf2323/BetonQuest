package org.betonquest.betonquest.command;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.feature.Backpack;
import org.betonquest.betonquest.feature.Backpack.DisplayType;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The /compass command. It opens the list of quests.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class CompassCommand implements CommandExecutor {
    /**
     * The {@link VariableProcessor} to use.
     */
    private final VariableProcessor variableProcessor;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new executor for the /compass command.
     *
     * @param variableProcessor the {@link VariableProcessor} to use
     * @param pluginMessage   the {@link PluginMessage} instance
     * @param profileProvider the profile provider instance
     */
    public CompassCommand(final VariableProcessor variableProcessor, final PluginMessage pluginMessage, final ProfileProvider profileProvider) {
        this.variableProcessor = variableProcessor;
        this.pluginMessage = pluginMessage;
        this.profileProvider = profileProvider;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("compass".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                new Backpack(variableProcessor, pluginMessage, profileProvider.getProfile((Player) sender), DisplayType.COMPASS);
            }
            return true;
        }
        return false;
    }
}
