package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.action.ActionIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ActionTypeRegistry}.
 */
public class ActionsComponent extends AbstractCoreComponent implements Actions {

    /**
     * The action type registry to load.
     */
    @Nullable
    private ActionTypeRegistry actionTypeRegistry;

    /**
     * The action processor to load.
     */
    @Nullable
    private ActionProcessor actionProcessor;

    /**
     * Create a new ActionsComponent.
     */
    public ActionsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ActionIdentifierFactory.class, ActionTypeRegistry.class, ActionProcessor.class, Actions.class);
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class,
                Identifiers.class, ConditionProcessor.class, Instructions.class);
    }

    @Override
    public boolean isLoaded() {
        return actionTypeRegistry != null && actionProcessor != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Plugin plugin = getDependency(Plugin.class);

        final ActionIdentifierFactory actionIdentifierFactory = new ActionIdentifierFactory(questPackageManager);
        identifiers.register(ActionIdentifier.class, actionIdentifierFactory);
        this.actionTypeRegistry = new ActionTypeRegistry(loggerFactory.create(ActionTypeRegistry.class), loggerFactory, conditionProcessor);
        this.actionProcessor = new ActionProcessor(loggerFactory.create(ActionProcessor.class),
                actionIdentifierFactory, actionTypeRegistry, bukkitScheduler, instructions, plugin);

        providerCallback.take(ActionIdentifierFactory.class, actionIdentifierFactory);
        providerCallback.take(ActionTypeRegistry.class, actionTypeRegistry);
        providerCallback.take(ActionProcessor.class, actionProcessor);
        providerCallback.take(Actions.class, this);
    }

    @Override
    public ActionManager manager() {
        return Objects.requireNonNull(actionProcessor, "Action processor not loaded yet");
    }

    @Override
    public ActionRegistry registry() {
        return Objects.requireNonNull(actionTypeRegistry, "Action type registry not loaded yet");
    }
}
