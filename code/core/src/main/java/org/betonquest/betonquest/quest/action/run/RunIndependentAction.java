package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.quest.action.CallPlayerlessActionAdapter;

import java.util.List;

/**
 * Runs specified actions player independently.
 * <p>
 * Although the implementation is a {@link PlayerlessAction}, using it in a playerless context does not make much sense.
 * Recommended usage is to wrap it in a {@link CallPlayerlessActionAdapter} and using it to call playerless actions
 * from non-playerless context.
 */
public class RunIndependentAction implements PlayerlessAction {

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * List of Actions to run.
     */
    private final Argument<List<ActionIdentifier>> actions;

    /**
     * Create a new RunIndependentAction instance.
     *
     * @param actionManager the action manager
     * @param actions       the actions to run
     */
    public RunIndependentAction(final ActionManager actionManager, final Argument<List<ActionIdentifier>> actions) {
        this.actionManager = actionManager;
        this.actions = actions;
    }

    @Override
    public void execute() throws QuestException {
        actionManager.run(null, actions.getValue(null));
    }
}
