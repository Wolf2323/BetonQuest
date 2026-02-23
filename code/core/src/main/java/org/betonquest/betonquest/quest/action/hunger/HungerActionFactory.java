package org.betonquest.betonquest.quest.action.hunger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory for the hunger action.
 */
public class HungerActionFactory implements PlayerActionFactory {

    /**
     * Create the hunger action factory.
     */
    public HungerActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Hunger> hunger = instruction.enumeration(Hunger.class).get();
        final Argument<Number> amount = instruction.number().get();
        return new OnlineActionAdapter(new HungerAction(hunger, amount));
    }
}
