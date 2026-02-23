package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link DrunkCondition}s from {@link Instruction}s.
 */
public class DrunkConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new Factory to create Drunk Conditions.
     */
    public DrunkConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> drunk = instruction.number().get();
        return new OnlineConditionAdapter(new DrunkCondition(drunk));
    }
}
