package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link McMMOAddExpAction}s from {@link Instruction}s.
 */
public class McMMOAddExpActionFactory implements PlayerActionFactory {

    /**
     * Create a new factory for mc mmo level conditions.
     */
    public McMMOAddExpActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> skillType = instruction.string().get();
        final Argument<Number> exp = instruction.number().get();
        return new OnlineActionAdapter(new McMMOAddExpAction(skillType, exp));
    }
}
