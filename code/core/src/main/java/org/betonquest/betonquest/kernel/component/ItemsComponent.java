package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Items}.
 */
public class ItemsComponent extends AbstractCoreComponent implements Items {

    /**
     * The item type registry to load.
     */
    @Nullable
    private ItemTypeRegistry itemTypeRegistry;

    /**
     * The item processor to load.
     */
    @Nullable
    private ItemProcessor itemProcessor;

    /**
     * Create a new ItemsComponent.
     */
    public ItemsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, Identifiers.class, Instructions.class);
    }

    @Override
    public boolean isLoaded() {
        return itemTypeRegistry != null && itemProcessor != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);

        final ItemIdentifierFactory itemIdentifierFactory = new ItemIdentifierFactory(questPackageManager);
        identifiers.register(ItemIdentifier.class, itemIdentifierFactory);
        this.itemTypeRegistry = new ItemTypeRegistry(loggerFactory.create(ItemTypeRegistry.class));
        this.itemProcessor = new ItemProcessor(loggerFactory.create(ItemProcessor.class),
                itemIdentifierFactory, itemTypeRegistry, instructions);

        dependencyProvider.take(ItemIdentifierFactory.class, itemIdentifierFactory);
        dependencyProvider.take(ItemTypeRegistry.class, itemTypeRegistry);
        dependencyProvider.take(ItemProcessor.class, itemProcessor);
        dependencyProvider.take(Items.class, this);
    }

    @Override
    public ItemManager manager() {
        return Objects.requireNonNull(itemProcessor, "Item processor not loaded yet");
    }

    @Override
    public ItemRegistry registry() {
        return Objects.requireNonNull(itemTypeRegistry, "Item registry not loaded yet");
    }
}
