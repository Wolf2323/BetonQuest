package org.betonquest.betonquest.quest.condition.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.Location;

/**
 * Factory for {@link LocationCondition}s from {@link Instruction}s.
 */
public class LocationConditionFactory implements PlayerConditionFactory {

    /**
     * Create the test for location condition factory.
     */
    public LocationConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        return new OnlineConditionAdapter(new LocationCondition(loc, range));
    }
}
