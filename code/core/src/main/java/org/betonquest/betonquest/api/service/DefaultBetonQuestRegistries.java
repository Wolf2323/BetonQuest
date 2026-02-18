package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.identifier.IdentifierRegistry;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestRegistries}.
 */
public class DefaultBetonQuestRegistries implements BetonQuestRegistries {

    /**
     * The {@link ActionRegistry} supplier.
     */
    private final Supplier<ActionRegistry> actionRegistry;

    /**
     * The {@link ConditionRegistry} supplier.
     */
    private final Supplier<ConditionRegistry> conditionRegistry;

    /**
     * The {@link ObjectiveRegistry} supplier.
     */
    private final Supplier<ObjectiveRegistry> objectiveRegistry;

    /**
     * The {@link ItemRegistry} supplier.
     */
    private final Supplier<ItemRegistry> itemRegistry;

    /**
     * The {@link NpcRegistry} supplier.
     */
    private final Supplier<NpcRegistry> npcRegistry;

    /**
     * The {@link PlaceholderRegistry} supplier.
     */
    private final Supplier<PlaceholderRegistry> placeholderRegistry;

    /**
     * The {@link IdentifierRegistry} supplier.
     */
    private final Supplier<IdentifierRegistry> identifierRegistry;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestRegistries}.
     *
     * @param actionRegistry      the {@link ActionRegistry} supplier
     * @param conditionRegistry   the {@link ConditionRegistry} supplier
     * @param objectiveRegistry   the {@link ObjectiveRegistry} supplier
     * @param itemRegistry        the {@link ItemRegistry} supplier
     * @param npcRegistry         the {@link NpcRegistry} supplier
     * @param placeholderRegistry the {@link PlaceholderRegistry} supplier
     * @param identifierRegistry  the {@link IdentifierRegistry} supplier
     */
    public DefaultBetonQuestRegistries(final Supplier<ActionRegistry> actionRegistry, final Supplier<ConditionRegistry> conditionRegistry,
                                       final Supplier<ObjectiveRegistry> objectiveRegistry, final Supplier<ItemRegistry> itemRegistry,
                                       final Supplier<NpcRegistry> npcRegistry, final Supplier<PlaceholderRegistry> placeholderRegistry,
                                       final Supplier<IdentifierRegistry> identifierRegistry) {
        this.actionRegistry = actionRegistry;
        this.conditionRegistry = conditionRegistry;
        this.objectiveRegistry = objectiveRegistry;
        this.itemRegistry = itemRegistry;
        this.npcRegistry = npcRegistry;
        this.placeholderRegistry = placeholderRegistry;
        this.identifierRegistry = identifierRegistry;
    }

    @Override
    public ActionRegistry actions() {
        return actionRegistry.get();
    }

    @Override
    public ConditionRegistry conditions() {
        return conditionRegistry.get();
    }

    @Override
    public ObjectiveRegistry objectives() {
        return objectiveRegistry.get();
    }

    @Override
    public ItemRegistry items() {
        return itemRegistry.get();
    }

    @Override
    public NpcRegistry npcs() {
        return npcRegistry.get();
    }

    @Override
    public PlaceholderRegistry placeholders() {
        return placeholderRegistry.get();
    }

    @Override
    public IdentifierRegistry identifiers() {
        return identifierRegistry.get();
    }
}
