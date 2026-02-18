package org.betonquest.betonquest.quest.action.cancel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;

/**
 * Factory for the cancel action.
 */
public class CancelActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The canceler processor.
     */
    private final CancelerProcessor cancelerProcessor;

    /**
     * Creates a new cancel action factory.
     *
     * @param loggerFactory     the logger factory to create a logger for the actions
     * @param cancelerProcessor the canceler processor
     */
    public CancelActionFactory(final BetonQuestLoggerFactory loggerFactory, final CancelerProcessor cancelerProcessor) {
        this.loggerFactory = loggerFactory;
        this.cancelerProcessor = cancelerProcessor;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<QuestCancelerIdentifier> cancelerID = instruction.identifier(QuestCancelerIdentifier.class).get();
        final FlagArgument<Boolean> bypass = instruction.bool().getFlag("bypass", true);
        return new OnlineActionAdapter(new CancelAction(cancelerProcessor, cancelerID, bypass),
                loggerFactory.create(CancelAction.class), instruction.getPackage());
    }
}
