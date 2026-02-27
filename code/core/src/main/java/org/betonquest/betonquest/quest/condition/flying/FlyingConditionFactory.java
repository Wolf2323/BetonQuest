package org.betonquest.betonquest.quest.condition.flying;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link FlyingCondition}s.
 */
public class FlyingConditionFactory implements PlayerConditionFactory {

    /**
     * Create the flying factory.
     */
    public FlyingConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) {
        return new OnlineConditionAdapter(new FlyingCondition());
    }
}
