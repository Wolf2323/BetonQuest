package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link RegionCondition}s from {@link Instruction}s.
 */
public class RegionConditionFactory implements PlayerConditionFactory {

    /**
     * Create the region condition factory.
     */
    public RegionConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineConditionAdapter(new RegionCondition(instruction.string().get()));
    }
}
