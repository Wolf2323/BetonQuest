package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.objective.Objective;

/**
 * The BetonQuest managers are responsible to grant access to existing and loaded types
 * previously registered with {@link BetonQuestRegistries}.
 */
public interface BetonQuestManagers {

    /**
     * Obtain the {@link ActionManager} instance.
     * The action manager offers functionality to execute actions.
     *
     * @return the action manager
     */
    ActionManager actions();

    /**
     * Obtain the {@link ConditionManager} instance.
     * The condition manager offers functionality to evaluate conditions.
     *
     * @return the condition manager
     */
    ConditionManager conditions();

    /**
     * Obtain the {@link ObjectiveManager} instance.
     * The objective manager offers functionality to manage {@link Objective}s,
     * such as starting, pausing, and cancelling them.
     *
     * @return the objective manager
     */
    ObjectiveManager objectives();

    /**
     * Obtain the {@link ItemManager} instance.
     * The item manager offers functionality to get items defined in BetonQuest.
     *
     * @return the item manager
     */
    ItemManager items();

    /**
     * Obtain the {@link NpcManager} instance.
     * The npc manager offers functionality to get NPCs defined in BetonQuest.
     *
     * @return the npc manager
     */
    NpcManager npcs();

    /**
     * Obtain the {@link Placeholders} instance.
     * The placeholder manager offers functionality to resolve placeholders.
     *
     * @return the placeholder manager
     */
    Placeholders placeholders();
}
