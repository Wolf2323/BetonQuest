package org.betonquest.betonquest.quest.action.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fires specified actions for every player in the party.
 */
public class PartyAction implements OnlineAction {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * The range of the party.
     */
    private final Argument<Number> range;

    /**
     * The optional maximum amount of players affected by this party action.
     */
    @Nullable
    private final Argument<Number> amount;

    /**
     * The conditions that must be met by the party members.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * The actions to fire.
     */
    private final Argument<List<ActionIdentifier>> actions;

    /**
     * Creates a new PartyAction instance.
     *
     * @param profileProvider  the profile provider instance
     * @param actionManager    the action manager
     * @param conditionManager the condition manager
     * @param range            the range of the party
     * @param amount           the optional maximum number of players affected by this party,
     *                         null or negative values set no maximum amount
     * @param conditions       the conditions that must be met by the party members
     * @param actions          the actions to fire
     */
    public PartyAction(final ProfileProvider profileProvider, final ActionManager actionManager,
                       final ConditionManager conditionManager, final Argument<Number> range,
                       @Nullable final Argument<Number> amount, final Argument<List<ConditionIdentifier>> conditions,
                       final Argument<List<ActionIdentifier>> actions) {
        this.profileProvider = profileProvider;
        this.range = range;
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
        this.amount = amount;
        this.conditions = conditions;
        this.actions = actions;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        for (final OnlineProfile member : getMemberList(profile)) {
            actionManager.run(member, actions.getValue(profile));
        }
    }

    private Set<OnlineProfile> getMemberList(final OnlineProfile profile) throws QuestException {
        final int toExecute = amount != null ? amount.getValue(profile).intValue() : -1;
        final Map<OnlineProfile, Double> members = Utils.getParty(conditionManager, profileProvider.getOnlineProfiles(),
                profile.getPlayer().getLocation(), range.getValue(profile).doubleValue(), conditions.getValue(profile));

        if (toExecute < 0) {
            return members.keySet();
        }

        return members.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(toExecute)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
