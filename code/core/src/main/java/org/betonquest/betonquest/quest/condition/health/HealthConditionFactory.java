package org.betonquest.betonquest.quest.condition.health;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link HealthCondition}s.
 */
public class HealthConditionFactory implements PlayerConditionFactory {

    /**
     * Create the health factory.
     */
    public HealthConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> health = instruction.number().get();
        return new OnlineConditionAdapter(new HealthCondition(health));
    }
}
