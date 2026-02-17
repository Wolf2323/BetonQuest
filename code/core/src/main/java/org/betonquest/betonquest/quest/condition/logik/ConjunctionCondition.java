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
 * All specified conditions have to be true.
 */
public class ConjunctionCondition implements NullableCondition {

    /**
     * All specified conditions have to be true.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Constructor for the {@link ConjunctionCondition} class.
     *
     * @param conditions       All specified conditions have to be true.
     * @param conditionManager the condition manager
     */
    public ConjunctionCondition(final Argument<List<ConditionIdentifier>> conditions, final ConditionManager conditionManager) {
        this.conditions = conditions;
        this.conditionManager = conditionManager;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return conditionManager.testAll(profile, conditions.getValue(profile));
    }
}
