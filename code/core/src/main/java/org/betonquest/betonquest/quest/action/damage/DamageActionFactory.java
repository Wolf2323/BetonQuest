package org.betonquest.betonquest.quest.action.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create damage actions from {@link Instruction}s.
 */
public class DamageActionFactory implements PlayerActionFactory {

    /**
     * Create the damage action factory.
     */
    public DamageActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> damage = instruction.number().get();
        return new OnlineActionAdapter(new DamageAction(damage));
    }
}
