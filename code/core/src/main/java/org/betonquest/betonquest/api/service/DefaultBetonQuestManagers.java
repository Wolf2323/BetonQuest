package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestManagers}.
 */
public class DefaultBetonQuestManagers implements BetonQuestManagers {

    /**
     * The {@link ActionManager} supplier.
     */
    private final Supplier<ActionManager> actionManager;

    /**
     * The {@link ConditionManager} supplier.
     */
    private final Supplier<ConditionManager> conditionManager;

    /**
     * The {@link ObjectiveManager} supplier.
     */
    private final Supplier<ObjectiveManager> objectiveManager;

    /**
     * The {@link ItemManager} supplier.
     */
    private final Supplier<ItemManager> itemManager;

    /**
     * The {@link NpcManager} supplier.
     */
    private final Supplier<NpcManager> npcManager;

    /**
     * The {@link Placeholders} supplier.
     */
    private final Supplier<Placeholders> placeholderManager;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestManagers}.
     *
     * @param actionManager      the {@link ActionManager} supplier
     * @param conditionManager   the {@link ConditionManager} supplier
     * @param objectiveManager   the {@link ObjectiveManager} supplier
     * @param itemManager        the {@link ItemManager} supplier
     * @param npcManager         the {@link NpcManager} supplier
     * @param placeholderManager the {@link Placeholders} supplier
     */
    public DefaultBetonQuestManagers(final Supplier<ActionManager> actionManager, final Supplier<ConditionManager> conditionManager,
                                     final Supplier<ObjectiveManager> objectiveManager, final Supplier<ItemManager> itemManager,
                                     final Supplier<NpcManager> npcManager, final Supplier<Placeholders> placeholderManager) {
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
        this.objectiveManager = objectiveManager;
        this.itemManager = itemManager;
        this.npcManager = npcManager;
        this.placeholderManager = placeholderManager;
    }

    @Override
    public ActionManager actions() {
        return actionManager.get();
    }

    @Override
    public ConditionManager conditions() {
        return conditionManager.get();
    }

    @Override
    public ObjectiveManager objectives() {
        return objectiveManager.get();
    }

    @Override
    public ItemManager items() {
        return itemManager.get();
    }

    @Override
    public NpcManager npcs() {
        return npcManager.get();
    }

    @Override
    public Placeholders placeholders() {
        return placeholderManager.get();
    }
}
