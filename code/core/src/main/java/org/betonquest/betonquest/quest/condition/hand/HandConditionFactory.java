package org.betonquest.betonquest.quest.condition.hand;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link HandCondition}s.
 */
public class HandConditionFactory implements PlayerConditionFactory {

    /**
     * Create the hand factory.
     */
    public HandConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final FlagArgument<Boolean> offhand = instruction.bool().getFlag("offhand", true);
        return new OnlineConditionAdapter(new HandCondition(item, offhand));
    }
}
