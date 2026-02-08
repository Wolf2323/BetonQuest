package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.service.ObjectiveManager;

/**
 * A factory for creating ObjectiveConditions.
 */
public class ObjectiveConditionFactory implements PlayerConditionFactory {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * Creates a new ObjectiveConditionFactory.
     *
     * @param objectiveManager the objective manager
     */
    public ObjectiveConditionFactory(final ObjectiveManager objectiveManager) {
        this.objectiveManager = objectiveManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new ObjectiveCondition(objectiveManager, instruction.identifier(ObjectiveIdentifier.class).get());
    }
}
