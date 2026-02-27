package org.betonquest.betonquest.quest.condition.facing;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link FacingCondition}s.
 */
public class FacingConditionFactory implements PlayerConditionFactory {

    /**
     * Create the facing factory.
     */
    public FacingConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Direction> direction = instruction.enumeration(Direction.class).get();
        return new OnlineConditionAdapter(new FacingCondition(direction));
    }
}
