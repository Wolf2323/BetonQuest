package org.betonquest.betonquest.quest.placeholder.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.config.PluginMessage;

/**
 * Factory to create {@link ConditionPlaceholder}s from {@link Instruction}s.
 */
public class ConditionPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create the Condition Placeholder Factory.
     *
     * @param conditionManager the condition manager
     * @param pluginMessage    the {@link PluginMessage} instance
     */
    public ConditionPlaceholderFactory(final ConditionManager conditionManager, final PluginMessage pluginMessage) {
        this.conditionManager = conditionManager;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ConditionIdentifier> conditionId = instruction.identifier(ConditionIdentifier.class).get();
        final FlagArgument<Boolean> papiMode = instruction.bool().getFlag("papiMode", true);
        return new ConditionPlaceholder(pluginMessage, conditionId, conditionManager, papiMode);
    }
}
