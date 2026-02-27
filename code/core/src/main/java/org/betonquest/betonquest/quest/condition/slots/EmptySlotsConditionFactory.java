package org.betonquest.betonquest.quest.condition.slots;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link EmptySlotsCondition}s.
 */
public class EmptySlotsConditionFactory implements PlayerConditionFactory {

    /**
     * Create the empty slots condition factory.
     */
    public EmptySlotsConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> required = instruction.number().get();
        final FlagArgument<Boolean> equal = instruction.bool().getFlag("equal", true);
        return new OnlineConditionAdapter(new EmptySlotsCondition(required, equal));
    }
}
