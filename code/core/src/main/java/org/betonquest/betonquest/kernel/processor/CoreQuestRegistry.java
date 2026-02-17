package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.service.DefaultObjectiveServiceProvider;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.quest.BaseQuestTypeRegistries;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

/**
 * Stores the active core quest type Processors to store and execute type logic.
 *
 * @param conditions   Condition logic.
 * @param actions      Action logic.
 * @param objectives   Objective logic.
 * @param placeholders Placeholder logic.
 */
public record CoreQuestRegistry(
        ConditionProcessor conditions,
        ActionProcessor actions,
        PlaceholderProcessor placeholders,
        ObjectiveProcessor objectives
) {

    /**
     * Create a new Registry for storing and using Core Quest Types.
     *
     * @param loggerFactory       the logger factory used for new custom logger instances
     * @param packManager         the quest package manager to get quest packages from
     * @param questTypeRegistries the available quest types
     * @param pluginManager       the manager to register listener
     * @param instructionApi      the instructionApi to use
     * @param scheduler           the bukkit scheduler to run sync tasks
     * @param profileProvider     the profile provider instance
     * @param plugin              the plugin instance to associate registered listener with
     * @return the newly created quest type api
     * @throws QuestException if creating the processors failed
     */
    public static CoreQuestRegistry create(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                           final BaseQuestTypeRegistries questTypeRegistries, final PluginManager pluginManager,
                                           final BukkitScheduler scheduler, final ProfileProvider profileProvider, final Plugin plugin,
                                           final Instructions instructionApi) throws QuestException {
        final IdentifierFactory<ObjectiveIdentifier> objectiveIdentifierFactory = questTypeRegistries.identifier().getFactory(ObjectiveIdentifier.class);
        final IdentifierFactory<ActionIdentifier> actionIdentifierFactory = questTypeRegistries.identifier().getFactory(ActionIdentifier.class);
        final IdentifierFactory<ConditionIdentifier> conditionIdentifierFactory = questTypeRegistries.identifier().getFactory(ConditionIdentifier.class);
        final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory = questTypeRegistries.identifier().getFactory(PlaceholderIdentifier.class);

        final PlaceholderProcessor placeholderProcessor = new PlaceholderProcessor(loggerFactory.create(PlaceholderProcessor.class),
                packManager, questTypeRegistries.placeholder(), scheduler, placeholderIdentifierFactory, instructionApi, plugin);
        final ActionProcessor actionProcessor = new ActionProcessor(loggerFactory.create(ActionProcessor.class),
                actionIdentifierFactory, questTypeRegistries.action(), scheduler, instructionApi, plugin);
        final ConditionProcessor conditionProcessor = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class),
                questTypeRegistries.condition(), scheduler, conditionIdentifierFactory, plugin, instructionApi);
        final DefaultObjectiveServiceProvider objectiveService = new DefaultObjectiveServiceProvider(plugin, conditionProcessor,
                actionProcessor, loggerFactory, profileProvider, instructionApi);
        return new CoreQuestRegistry(conditionProcessor, actionProcessor, placeholderProcessor,
                new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class),
                        questTypeRegistries.objective(), objectiveIdentifierFactory, pluginManager, objectiveService, instructionApi, plugin));
    }

    /**
     * Clears the loaded Core Quest Types. Used before reloading all QuestPackages.
     */
    public void clear() {
        conditions.clear();
        actions.clear();
        objectives.clear();
        placeholders.clear();
    }

    /**
     * Load all Core Quest Types from the QuestPackage.
     *
     * @param pack to load the core quest types from
     */
    public void load(final QuestPackage pack) {
        actions.load(pack);
        conditions.load(pack);
        objectives.load(pack);
        placeholders.load(pack);
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for core quest types
     */
    public Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> metricsSupplier() {
        return Map.ofEntries(
                conditions.metricsSupplier(),
                actions.metricsSupplier(),
                objectives.metricsSupplier(),
                placeholders.metricsSupplier()
        );
    }

    /**
     * Gets the amount of current loaded Core Quest Types with their readable name.
     *
     * @return the value size with the identifier
     */
    public String readableSize() {
        return String.join(", ", conditions.readableSize(), actions.readableSize(),
                objectives.readableSize(), placeholders.readableSize());
    }
}
