package org.betonquest.betonquest.quest.action.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.ActionManager;
import org.betonquest.betonquest.api.service.ConditionManager;

import java.util.List;

/**
 * Fires specified actions for every player in the party.
 */
public class PartyActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a PartyActionFactory instance.
     *
     * @param loggerFactory    the logger factory to create a logger for the actions
     * @param profileProvider  the profile provider instance
     * @param actionManager    the action manager
     * @param conditionManager the condition manager
     */
    public PartyActionFactory(final BetonQuestLoggerFactory loggerFactory, final ProfileProvider profileProvider,
                              final ActionManager actionManager, final ConditionManager conditionManager) {
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> range = instruction.number().get();
        final Argument<Number> amount = instruction.number().get("amount").orElse(null);
        final Argument<List<ConditionIdentifier>> conditions = instruction.identifier(ConditionIdentifier.class).list().get();
        final Argument<List<ActionIdentifier>> actions = instruction.identifier(ActionIdentifier.class).list().get();
        return new OnlineActionAdapter(
                new PartyAction(profileProvider, range, actionManager, conditionManager, amount, conditions, actions),
                loggerFactory.create(PartyAction.class),
                instruction.getPackage()
        );
    }
}
