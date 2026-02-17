package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;

/**
 * Checks if the player has specified objective active.
 */
public class ObjectiveCondition implements PlayerCondition {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The objective ID.
     */
    private final Argument<ObjectiveIdentifier> objectiveId;

    /**
     * Creates a new ObjectiveCondition.
     *
     * @param objectiveManager the objective manager
     * @param objectiveId      the objective ID
     */
    public ObjectiveCondition(final ObjectiveManager objectiveManager, final Argument<ObjectiveIdentifier> objectiveId) {
        this.objectiveManager = objectiveManager;
        this.objectiveId = objectiveId;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return objectiveManager.getObjective(objectiveId.getValue(profile)).getService().containsProfile(profile);
    }
}
