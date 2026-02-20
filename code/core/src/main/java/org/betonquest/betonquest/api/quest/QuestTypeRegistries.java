package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;

/**
 * Provides the BetonQuest Quest Type Registries.
 * <p>
 * They are used to add new implementations and access them.
 */
public interface QuestTypeRegistries {

    /**
     * Gets the registry for conditions.
     *
     * @return the condition registry
     */
    ConditionRegistry condition();

    /**
     * Gets the registry for actions.
     *
     * @return the action registry
     */
    ActionRegistry action();

    /**
     * Gets the registry for objectives.
     *
     * @return the objective registry
     */
    ObjectiveRegistry objective();

    /**
     * Gets the registry for placeholders.
     *
     * @return the placeholder registry
     */
    PlaceholderRegistry placeholder();

    /**
     * Gets the registry for identifiers.
     *
     * @return the identifier registry
     */
    Identifiers identifier();
}
