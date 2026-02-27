package org.betonquest.betonquest.quest.condition.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link ExperienceCondition}s.
 */
public class ExperienceConditionFactory implements PlayerConditionFactory {

    /**
     * Create the experience factory.
     */
    public ExperienceConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        return new OnlineConditionAdapter(new ExperienceCondition(amount));
    }
}
