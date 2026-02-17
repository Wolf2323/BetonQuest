package org.betonquest.betonquest.quest.action.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;

/**
 * Factory to create variable actions from {@link Instruction}s.
 */
public class VariableActionFactory implements PlayerActionFactory {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * Create a new factory for {@link VariableAction}s.
     *
     * @param objectiveManager the objective manager
     */
    public VariableActionFactory(final ObjectiveManager objectiveManager) {
        this.objectiveManager = objectiveManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ObjectiveIdentifier> objectiveID = instruction.identifier(ObjectiveIdentifier.class).get();
        final Argument<String> key = instruction.string().get();
        final Argument<String> value = instruction.string().get();
        return new VariableAction(objectiveManager, objectiveID, key, value);
    }
}
