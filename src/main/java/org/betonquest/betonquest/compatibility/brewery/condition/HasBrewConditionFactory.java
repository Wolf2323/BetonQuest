package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link HasBrewCondition}s from {@link Instruction}s.
 */
public class HasBrewConditionFactory implements PlayerConditionFactory {
    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Has Brew Conditions.
     *
     * @param loggerFactory the logger factory.
     * @param data          the data used for primary server access.
     */
    public HasBrewConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableNumber countVar = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final VariableString nameVar = instruction.get(VariableString::new);
        final BetonQuestLogger logger = loggerFactory.create(HasBrewCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new HasBrewCondition(countVar, nameVar), logger, instruction.getPackage()), data);
    }
}
