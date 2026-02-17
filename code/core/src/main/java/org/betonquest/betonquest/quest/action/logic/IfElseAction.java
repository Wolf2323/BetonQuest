package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.jetbrains.annotations.Nullable;

/**
 * The if-else action. Either execute the one or the other action, depending on the condition.
 */
public class IfElseAction implements NullableAction {

    /**
     * The condition to check.
     */
    private final Argument<ConditionIdentifier> condition;

    /**
     * The action to run if the condition is true.
     */
    private final Argument<ActionIdentifier> action;

    /**
     * The action to run if the condition is false.
     */
    private final Argument<ActionIdentifier> elseAction;

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Creates a new if-else action.
     *
     * @param condition        the condition to check
     * @param action           the action to run if the condition is true
     * @param actionManager    the action manager
     * @param conditionManager the condition manager
     * @param elseAction       the action to run if the condition is false
     */
    public IfElseAction(final Argument<ConditionIdentifier> condition, final Argument<ActionIdentifier> action,
                        final ActionManager actionManager, final ConditionManager conditionManager,
                        final Argument<ActionIdentifier> elseAction) {
        this.condition = condition;
        this.action = action;
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
        this.elseAction = elseAction;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        if (conditionManager.test(profile, condition.getValue(profile))) {
            actionManager.run(profile, action.getValue(profile));
        } else {
            actionManager.run(profile, elseAction.getValue(profile));
        }
    }
}
