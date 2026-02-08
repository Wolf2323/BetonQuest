package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.service.ConditionManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Wrapper for player and playerless actions.
 */
public class ActionAdapter extends QuestAdapter<PlayerAction, PlayerlessAction> implements PrimaryThreadEnforceable {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * QuestTypeApi to check conditions.
     */
    private final Supplier<ConditionManager> conditionManager;

    /**
     * Instruction used to create the types.
     */
    private final Instruction instruction;

    /**
     * Conditions that must be met to execute.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * Create a new Wrapper for placeholders with instruction.
     *
     * @param log              the custom logger for this class
     * @param conditionManager the condition manager
     * @param instruction      the instruction used to create the types
     * @param player           the type requiring a profile for execution
     * @param playerless       the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     * @throws QuestException           when there was an error parsing conditions
     */
    public ActionAdapter(final BetonQuestLogger log, final Supplier<ConditionManager> conditionManager, final Instruction instruction,
                         @Nullable final PlayerAction player, @Nullable final PlayerlessAction playerless) throws QuestException {
        super(instruction.getPackage(), player, playerless);
        this.log = log;
        this.conditionManager = conditionManager;
        this.instruction = instruction;
        conditions = instruction.identifier(ConditionIdentifier.class).list().get("conditions", Collections.emptyList());
    }

    /**
     * Fires an action for the profile if it meets the action's conditions.
     *
     * @param profile the {@link Profile} to execute for
     * @return whether the action was successfully handled or not
     * @throws QuestException if the action could not be executed
     */
    public boolean fire(@Nullable final Profile profile) throws QuestException {
        if (player == null || profile == null) {
            return handleNullProfile();
        }
        log.debug(getPackage(), "Action will be fired for "
                + (profile.getOnlineProfile().isPresent() ? "online" : "offline") + " profile.");

        if (!conditionManager.get().testAll(profile, conditions.getValue(profile))) {
            log.debug(getPackage(), "Action conditions were not met for " + profile);
            return false;
        }
        player.execute(profile);
        return true;
    }

    private boolean handleNullProfile() throws QuestException {
        if (playerless == null) {
            //throw new QuestException("Non-static action '" + instruction + "' cannot be executed without a profile reference!");
            log.warn(getPackage(), "Cannot execute non-static action '" + instruction.getID() + "' without a player!");
            return false;
        }
        log.debug(getPackage(), "Static action will be fired without a profile.");
        if (!conditionManager.get().testAll(null, conditions.getValue(null))) {
            log.debug(getPackage(), "Action conditions were not met");
            return false;
        }
        playerless.execute();
        return true;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return player != null && player.isPrimaryThreadEnforced() || playerless != null && playerless.isPrimaryThreadEnforced();
    }
}
