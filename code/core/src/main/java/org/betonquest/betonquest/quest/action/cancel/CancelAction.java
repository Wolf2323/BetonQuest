package org.betonquest.betonquest.quest.action.cancel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.legacy.LegacyFeatures;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * The cancel action.
 */
public class CancelAction implements OnlineAction {

    /**
     * Feature API.
     */
    private final LegacyFeatures featureApi;

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
     * @param featureApi the feature API
     * @param cancelerID the canceler to use
     * @param bypass     whether the canceler conditions should be ignored for canceling
     */
    public CancelAction(final LegacyFeatures featureApi, final Argument<QuestCancelerIdentifier> cancelerID, final FlagArgument<Boolean> bypass) {
        this.featureApi = featureApi;
        this.cancelerID = cancelerID;
        this.bypass = bypass;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        featureApi.getCanceler(cancelerID.getValue(profile)).cancel(profile, bypass.getValue(profile).orElse(false));
    }
}
