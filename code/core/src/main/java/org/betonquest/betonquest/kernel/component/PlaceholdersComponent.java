package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.id.placeholder.PlaceholderIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Placeholders}.
 */
public class PlaceholdersComponent extends AbstractCoreComponent implements Placeholders {

    /**
     * The placeholder type registry to load.
     */
    @Nullable
    private PlaceholderTypeRegistry placeholderTypeRegistry;

    /**
     * The placeholder processor to load.
     */
    @Nullable
    private PlaceholderProcessor placeholderProcessor;

    /**
     * Create a new PlaceholdersComponent.
     */
    public PlaceholdersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(PlaceholderIdentifierFactory.class, PlaceholderTypeRegistry.class, PlaceholderManager.class, Placeholders.class);
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class,
                Identifiers.class, Instructions.class);
    }

    @Override
    public boolean isLoaded() {
        return placeholderTypeRegistry != null && placeholderProcessor != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final Plugin plugin = getDependency(Plugin.class);

        final PlaceholderIdentifierFactory placeholderIdentifierFactory = new PlaceholderIdentifierFactory(questPackageManager);
        identifiers.register(PlaceholderIdentifier.class, placeholderIdentifierFactory);
        this.placeholderTypeRegistry = new PlaceholderTypeRegistry(loggerFactory.create(PlaceholderTypeRegistry.class));
        this.placeholderProcessor = new PlaceholderProcessor(loggerFactory.create(PlaceholderProcessor.class),
                questPackageManager, placeholderTypeRegistry, bukkitScheduler, placeholderIdentifierFactory, instructions, plugin);

        providerCallback.take(PlaceholderIdentifierFactory.class, placeholderIdentifierFactory);
        providerCallback.take(PlaceholderTypeRegistry.class, placeholderTypeRegistry);
        providerCallback.take(PlaceholderManager.class, placeholderProcessor);
        providerCallback.take(Placeholders.class, this);
    }

    @Override
    public PlaceholderManager manager() {
        return Objects.requireNonNull(placeholderProcessor, "Placeholder processor not loaded yet");
    }

    @Override
    public PlaceholderRegistry registry() {
        return Objects.requireNonNull(placeholderTypeRegistry, "Placeholder registry not loaded yet");
    }
}
