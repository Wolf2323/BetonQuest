package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.ActionManager;
import org.betonquest.betonquest.api.service.ConditionManager;

/**
 * Factory to create if-else actions from {@link Instruction}s.
 */
public class IfElseActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The keyword to identify an else action following.
     */
    private static final String ELSE_KEYWORD = "else";

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * The action constructor.
     *
     * @param actionManager    the action manager
     * @param conditionManager the condition manager
     */
    public IfElseActionFactory(final ActionManager actionManager, final ConditionManager conditionManager) {
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createIfElseAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createIfElseAction(instruction);
    }

    private NullableActionAdapter createIfElseAction(final Instruction instruction) throws QuestException {
        final Argument<ConditionIdentifier> condition = instruction.identifier(ConditionIdentifier.class).get();
        final Argument<ActionIdentifier> action = instruction.identifier(ActionIdentifier.class).get();
        if (!ELSE_KEYWORD.equalsIgnoreCase(instruction.nextElement())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final Argument<ActionIdentifier> elseAction = instruction.identifier(ActionIdentifier.class).get();
        return new NullableActionAdapter(new IfElseAction(condition, action, actionManager, conditionManager, elseAction));
    }
}
