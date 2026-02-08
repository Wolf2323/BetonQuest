package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.identifier.IdentifierRegistry;
import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlaceholderRegistry;

/**
 * The BetonQuest registries are responsible for registering custom features.
 * Registries usually implement either the {@link CoreQuestRegistry}, the {@link FeatureTypeRegistry},
 * or the {@link FeatureRegistry} interfaces and offer varying methods for registering custom features with their
 * factories.
 */
public interface BetonQuestRegistries {

    /**
     * Obtains the {@link ActionRegistry} for registering custom actions.
     *
     * @return the action registry
     */
    ActionRegistry actions();

    /**
     * Obtains the {@link ConditionRegistry} for registering custom conditions.
     *
     * @return the condition registry
     */
    ConditionRegistry conditions();

    /**
     * Obtains the {@link ObjectiveRegistry} for registering custom {@link Objective}s.
     *
     * @return the objective registry
     */
    ObjectiveRegistry objectives();

    /**
     * Obtains the {@link ItemRegistry} for registering custom {@link QuestItem}s and {@link QuestItemSerializer}s.
     *
     * @return the item registry
     */
    ItemRegistry items();

    /**
     * Obtains the {@link NpcRegistry} for registering custom NPCs.
     *
     * @return the npc registry
     */
    NpcRegistry npcs();

    /**
     * Obtains the {@link PlaceholderRegistry} for registering custom placeholders.
     *
     * @return the placeholder registry
     */
    PlaceholderRegistry placeholders();

    /**
     * Obtains the {@link IdentifierRegistry} for registering custom identifiers.
     *
     * @return the identifier registry
     */
    IdentifierRegistry identifiers();
}
