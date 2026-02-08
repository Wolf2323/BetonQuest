package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.service.ConditionManager;

import java.util.function.Supplier;

/**
 * Registry for quest core elements.
 *
 * @param condition   The Registry holding registered condition types.
 * @param action      The Registry holding registered action types.
 * @param objective   The Registry holding registered objective types.
 * @param placeholder The Registry holding registered placeholder types.
 * @param identifier  The Registry holding registered identifier types.
 */
public record BaseQuestTypeRegistries(
        ConditionTypeRegistry condition,
        ActionTypeRegistry action,
        ObjectiveTypeRegistry objective,
        PlaceholderTypeRegistry placeholder,
        IdentifierTypeRegistry identifier
) implements QuestTypeRegistries {

    /**
     * Create a new quest registry for quest core elements.
     *
     * @param loggerFactory    the logger factory to create individual class logger
     * @param conditionManager the condition manager to use for condition checking
     * @return the newly created registries
     */
    public static BaseQuestTypeRegistries create(final BetonQuestLoggerFactory loggerFactory, final Supplier<ConditionManager> conditionManager) {
        return new BaseQuestTypeRegistries(
                new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class)),
                new ActionTypeRegistry(loggerFactory.create(ActionTypeRegistry.class), loggerFactory, conditionManager),
                new ObjectiveTypeRegistry(loggerFactory.create(ObjectiveTypeRegistry.class)),
                new PlaceholderTypeRegistry(loggerFactory.create(PlaceholderTypeRegistry.class)),
                new IdentifierTypeRegistry(loggerFactory.create(IdentifierTypeRegistry.class))
        );
    }
}
