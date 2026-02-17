package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One of the specified conditions has to be true.
 */
public class AlternativeCondition implements NullableCondition {

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * List of condition IDs.
     */
    private final Argument<List<ConditionIdentifier>> conditionIDs;

    /**
     * Create a new alternative condition.
     *
     * @param conditionManager the condition manager
     * @param conditionIDs     the condition IDs
     */
    public AlternativeCondition(final ConditionManager conditionManager, final Argument<List<ConditionIdentifier>> conditionIDs) {
        this.conditionManager = conditionManager;
        this.conditionIDs = conditionIDs;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final List<ConditionIdentifier> conditionIDs = this.conditionIDs.getValue(profile);
        return conditionManager.testAny(profile, conditionIDs);
    }
}
