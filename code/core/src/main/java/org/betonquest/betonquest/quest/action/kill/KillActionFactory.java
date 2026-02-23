package org.betonquest.betonquest.quest.action.kill;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory for the kill action.
 */
public class KillActionFactory implements PlayerActionFactory {

    /**
     * Creates the kill action factory.
     */
    public KillActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(new KillAction());
    }
}
