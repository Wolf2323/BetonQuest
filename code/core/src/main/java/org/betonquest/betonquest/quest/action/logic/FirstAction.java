package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The First action. Similar to the folder, except it runs linearly through a list of actions and
 * stops after the first one succeeds. This is intended to be used with condition: syntax in actions.
 */
public class FirstAction implements NullableAction {

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The actions to run.
     */
    private final Argument<List<ActionIdentifier>> actions;

    /**
     * Makes a new first action.
     *
     * @param actionManager The action manager.
     * @param actions       A list of actions to execute in order.
     */
    public FirstAction(final ActionManager actionManager, final Argument<List<ActionIdentifier>> actions) {
        this.actionManager = actionManager;
        this.actions = actions;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ActionIdentifier action : actions.getValue(profile)) {
            if (actionManager.run(profile, action)) {
                break;
            }
        }
    }
}
