package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link DrunkQualityCondition}s from {@link Instruction}s.
 */
public class DrunkQualityConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new Factory to create Drunk Quality Conditions.
     */
    public DrunkQualityConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> quality = instruction.number().get();
        return new OnlineConditionAdapter(new DrunkQualityCondition(quality));
    }
}
