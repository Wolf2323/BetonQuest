package org.betonquest.betonquest.quest.condition.burning;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link BurningCondition}s from {@link Instruction}s.
 */
public class BurningConditionFactory implements PlayerConditionFactory {

    /**
     * Create the burning factory.
     */
    public BurningConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) {
        return new OnlineConditionAdapter(new BurningCondition());
    }
}
