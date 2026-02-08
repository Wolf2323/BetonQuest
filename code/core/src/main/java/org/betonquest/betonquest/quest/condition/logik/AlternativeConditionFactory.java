package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.ConditionManager;

import java.util.List;

/**
 * Factory for {@link AlternativeCondition}s.
 */
public class AlternativeConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create the alternative condition factory.
     *
     * @param conditionManager the condition manager
     */
    public AlternativeConditionFactory(final ConditionManager conditionManager) {
        this.conditionManager = conditionManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseAlternative(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseAlternative(instruction));
    }

    private AlternativeCondition parseAlternative(final Instruction instruction) throws QuestException {
        final Argument<List<ConditionIdentifier>> conditionIDs = instruction.identifier(ConditionIdentifier.class).list().get();
        return new AlternativeCondition(conditionManager, conditionIDs);
    }
}
