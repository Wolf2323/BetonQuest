package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.ActionManager;

import java.util.Collections;
import java.util.List;

/**
 * Create new {@link RunIndependentAction} from instruction.
 */
public class RunIndependentActionFactory implements PlayerlessActionFactory {

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * Create new {@link RunIndependentActionFactory}.
     *
     * @param actionManager the action manager
     */
    public RunIndependentActionFactory(final ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<List<ActionIdentifier>> actions = instruction.identifier(ActionIdentifier.class).list().get("actions", Collections.emptyList());
        return new RunIndependentAction(actionManager, actions);
    }
}
