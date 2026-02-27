package org.betonquest.betonquest.quest.condition.sneak;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link SneakCondition}s.
 */
public class SneakConditionFactory implements PlayerConditionFactory {

    /**
     * Create the sneak factory.
     */
    public SneakConditionFactory() {

    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) {
        return new OnlineConditionAdapter(new SneakCondition());
    }
}
