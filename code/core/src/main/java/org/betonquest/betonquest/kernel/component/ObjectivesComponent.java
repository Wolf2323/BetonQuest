package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.service.DefaultObjectiveServiceProvider;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.id.objective.ObjectiveIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Objectives}.
 */
public class ObjectivesComponent extends AbstractCoreComponent implements Objectives {

    /**
     * The objective type registry to load.
     */
    @Nullable
    private ObjectiveTypeRegistry objectiveTypeRegistry;

    /**
     * The objective processor to load.
     */
    @Nullable
    private ObjectiveProcessor objectiveProcessor;

    /**
     * Create a new ObjectivesComponent.
     */
    public ObjectivesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginManager.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class,
                Identifiers.class, ConditionProcessor.class, ActionProcessor.class, Instructions.class);
    }

    @Override
    public boolean isLoaded() {
        return objectiveTypeRegistry != null && objectiveProcessor != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final ActionProcessor actionProcessor = getDependency(ActionProcessor.class);
        final Instructions instructions = getDependency(Instructions.class);
        final PluginManager pluginManager = getDependency(PluginManager.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Plugin plugin = getDependency(Plugin.class);

        final DefaultObjectiveServiceProvider objectiveServiceProvider = new DefaultObjectiveServiceProvider(plugin, conditionProcessor, actionProcessor,
                loggerFactory, profileProvider, instructions);
        final ObjectiveIdentifierFactory objectiveIdentifierFactory = new ObjectiveIdentifierFactory(questPackageManager);
        identifiers.register(ObjectiveIdentifier.class, objectiveIdentifierFactory);
        this.objectiveTypeRegistry = new ObjectiveTypeRegistry(loggerFactory.create(ObjectiveTypeRegistry.class));
        this.objectiveProcessor = new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class),
                objectiveTypeRegistry, objectiveIdentifierFactory, pluginManager,
                objectiveServiceProvider, instructions, plugin);

        dependencyProvider.take(ObjectiveIdentifierFactory.class, objectiveIdentifierFactory);
        dependencyProvider.take(ObjectiveTypeRegistry.class, objectiveTypeRegistry);
        dependencyProvider.take(ObjectiveProcessor.class, objectiveProcessor);
        dependencyProvider.take(Objectives.class, this);
    }

    @Override
    public ObjectiveManager manager() {
        return Objects.requireNonNull(objectiveProcessor, "Objective processor not loaded yet");
    }

    @Override
    public ObjectiveRegistry registry() {
        return Objects.requireNonNull(objectiveTypeRegistry, "Objective registry not loaded yet");
    }
}
