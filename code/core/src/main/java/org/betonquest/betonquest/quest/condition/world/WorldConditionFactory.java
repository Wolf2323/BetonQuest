package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.World;

/**
 * Factory to create world conditions from {@link Instruction}s.
 */
public class WorldConditionFactory implements PlayerConditionFactory {

    /**
     * Create the test for block condition factory.
     */
    public WorldConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<World> world = instruction.world().get();
        return new OnlineConditionAdapter(new WorldCondition(world));
    }
}
