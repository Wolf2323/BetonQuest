package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.ActionManager;
import org.betonquest.betonquest.api.service.ConditionManager;

import java.util.Collections;
import java.util.List;

/**
 * Create new {@link RunForAllAction} from instruction.
 */
public class RunForAllActionFactory implements PlayerlessActionFactory {

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create new {@link RunForAllActionFactory}.
     *
     * @param profileProvider  the profile provider instance
     * @param actionManager    the action manager
     * @param conditionManager the condition manager
     */
    public RunForAllActionFactory(final ProfileProvider profileProvider, final ActionManager actionManager, final ConditionManager conditionManager) {
        this.profileProvider = profileProvider;
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<List<ActionIdentifier>> actions = instruction.identifier(ActionIdentifier.class)
                .list().get("actions", Collections.emptyList());
        final Argument<List<ConditionIdentifier>> conditions = instruction.identifier(ConditionIdentifier.class)
                .list().get("where", Collections.emptyList());
        return new RunForAllAction(profileProvider::getOnlineProfiles, actions, actionManager, conditionManager, conditions);
    }
}
