package org.betonquest.betonquest.quest.action.cancel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;

/**
 * The cancel action.
 */
public class CancelAction implements OnlineAction {

    /**
     * The canceler processor.
     */
    private final CancelerProcessor cancelerProcessor;

    /**
     * The canceler to use.
     */
    private final Argument<QuestCancelerIdentifier> cancelerID;

    /**
     * Whether the canceler conditions should be ignored for canceling.
     */
    private final FlagArgument<Boolean> bypass;

    /**
     * Creates a new cancel action.
     *
     * @param cancelerProcessor the canceler processor
     * @param cancelerID        the canceler to use
     * @param bypass            whether the canceler conditions should be ignored for canceling
     */
    public CancelAction(final CancelerProcessor cancelerProcessor, final Argument<QuestCancelerIdentifier> cancelerID, final FlagArgument<Boolean> bypass) {
        this.cancelerProcessor = cancelerProcessor;
        this.cancelerID = cancelerID;
        this.bypass = bypass;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        cancelerProcessor.get(cancelerID.getValue(profile)).cancel(profile, bypass.getValue(profile).orElse(false));
    }
}
