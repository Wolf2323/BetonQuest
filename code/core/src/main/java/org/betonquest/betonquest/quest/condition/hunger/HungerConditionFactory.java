package org.betonquest.betonquest.quest.condition.hunger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link HungerCondition}s.
 */
public class HungerConditionFactory implements PlayerConditionFactory {

    /**
     * Create the hunger factory.
     */
    public HungerConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> hunger = instruction.number().get();
        return new OnlineConditionAdapter(new HungerCondition(hunger));
    }
}
