package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link ArmorRatingCondition}s from {@link Instruction}s.
 */
public class ArmorRatingConditionFactory implements PlayerConditionFactory {

    /**
     * Create the armor rating factory.
     */
    public ArmorRatingConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> required = instruction.number().get();
        return new OnlineConditionAdapter(new ArmorRatingCondition(required));
    }
}
