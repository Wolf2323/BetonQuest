package org.betonquest.betonquest.quest.action.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create burn actions from {@link Instruction}s.
 */
public class BurnActionFactory implements PlayerActionFactory {

    /**
     * Create the burn action factory.
     */
    public BurnActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> duration = instruction.number().get("duration").orElse(null);
        if (duration == null) {
            throw new QuestException("Missing duration!");
        }
        return new OnlineActionAdapter(new BurnAction(duration));
    }
}
