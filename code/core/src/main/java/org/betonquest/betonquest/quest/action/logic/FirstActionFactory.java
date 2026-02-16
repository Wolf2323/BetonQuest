package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.ActionManager;

import java.util.List;

/**
 * Factory to create FirstActions from actions from {@link Instruction}s.
 */
public class FirstActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The action manager to handle actions.
     */
    private final ActionManager actionManager;

    /**
     * Sole constructor.
     *
     * @param actionManager The action manager.
     */
    public FirstActionFactory(final ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createFirstAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createFirstAction(instruction);
    }

    private NullableActionAdapter createFirstAction(final Instruction instruction) throws QuestException {
        final Argument<List<ActionIdentifier>> list = instruction.identifier(ActionIdentifier.class).list().get();
        return new NullableActionAdapter(new FirstAction(actionManager, list));
    }
}
