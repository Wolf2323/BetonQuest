package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Stores the action types that can be used in BetonQuest.
 */
public class ActionTypeRegistry extends QuestTypeRegistry<PlayerAction, PlayerlessAction, ActionAdapter>
        implements ActionRegistry {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The condition manager to check conditions.
     */
    private final Supplier<ConditionManager> conditionManager;

    /**
     * Create a new action type registry.
     *
     * @param log              the logger that will be used for logging
     * @param loggerFactory    the logger factory to create a new custom logger
     * @param conditionManager the condition manager
     */
    public ActionTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final Supplier<ConditionManager> conditionManager) {
        super(log, "action");
        this.loggerFactory = loggerFactory;
        this.conditionManager = conditionManager;
    }

    @Override
    protected TypeFactory<ActionAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerAction> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessAction> playerlessFactory) {
        return new ActionAdapterFactory(loggerFactory, conditionManager.get(), playerFactory, playerlessFactory);
    }
}
