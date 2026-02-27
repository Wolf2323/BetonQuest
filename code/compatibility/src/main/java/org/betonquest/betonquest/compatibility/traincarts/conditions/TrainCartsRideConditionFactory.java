package org.betonquest.betonquest.compatibility.traincarts.conditions;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for the {@link TrainCartsRideCondition}.
 */
public class TrainCartsRideConditionFactory implements PlayerConditionFactory {

    /**
     * Create the ride condition factory.
     */
    public TrainCartsRideConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> trainName = instruction.string().get();
        return new OnlineConditionAdapter(new TrainCartsRideCondition(trainName));
    }
}
