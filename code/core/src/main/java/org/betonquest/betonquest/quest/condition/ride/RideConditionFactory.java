package org.betonquest.betonquest.quest.condition.ride;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.entity.EntityType;

import java.util.Optional;

/**
 * Factory to create ride conditions from {@link Instruction}s.
 */
public class RideConditionFactory implements PlayerConditionFactory {

    /**
     * The string to match any entity.
     */
    private static final String ANY_ENTITY = "any";

    /**
     * Create the ride condition factory.
     */
    public RideConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Optional<EntityType>> vehicle = instruction.enumeration(EntityType.class)
                .prefilterOptional(ANY_ENTITY, null).get();
        return new OnlineConditionAdapter(new RideCondition(vehicle));
    }
}
