package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.condition.ConditionIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Conditions}.
 */
public class ConditionsComponent extends AbstractCoreComponent implements Conditions {

    /**
     * The condition type registry to load.
     */
    @Nullable
    private ConditionTypeRegistry conditionTypeRegistry;

    /**
     * The condition processor to load.
     */
    @Nullable
    private ConditionProcessor conditionProcessor;

    /**
     * Create a new ConditionsComponent.
     */
    public ConditionsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class,
                Identifiers.class, Instructions.class);
    }

    @Override
    public boolean isLoaded() {
        return conditionTypeRegistry != null && conditionProcessor != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Instructions instructions = getDependency(Instructions.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final Plugin plugin = getDependency(Plugin.class);

        final ConditionIdentifierFactory conditionIdentifierFactory = new ConditionIdentifierFactory(questPackageManager);
        identifiers.register(ConditionIdentifier.class, conditionIdentifierFactory);
        this.conditionTypeRegistry = new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class));
        this.conditionProcessor = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class),
                conditionTypeRegistry, bukkitScheduler, conditionIdentifierFactory, plugin, instructions);

        providerCallback.take(ConditionIdentifierFactory.class, conditionIdentifierFactory);
        providerCallback.take(ConditionTypeRegistry.class, conditionTypeRegistry);
        providerCallback.take(ConditionProcessor.class, conditionProcessor);
        providerCallback.take(Conditions.class, this);
    }

    @Override
    public ConditionManager manager() {
        return Objects.requireNonNull(conditionProcessor, "Condition processor not loaded yet");
    }

    @Override
    public ConditionRegistry registry() {
        return Objects.requireNonNull(conditionTypeRegistry, "Condition registry not loaded yet");
    }
}
