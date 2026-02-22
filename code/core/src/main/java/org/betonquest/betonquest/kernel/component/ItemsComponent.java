package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.feature.DefaultItems;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Items}.
 */
public class ItemsComponent extends AbstractCoreComponent {

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
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);

        final ItemIdentifierFactory itemIdentifierFactory = new ItemIdentifierFactory(questPackageManager);
        identifiers.register(ItemIdentifier.class, itemIdentifierFactory);
        final ItemTypeRegistry itemTypeRegistry = new ItemTypeRegistry(loggerFactory.create(ItemTypeRegistry.class));
        final ItemProcessor itemProcessor = new ItemProcessor(loggerFactory.create(ItemProcessor.class),
                itemIdentifierFactory, itemTypeRegistry, instructions);

        dependencyProvider.take(ItemIdentifierFactory.class, itemIdentifierFactory);
        dependencyProvider.take(ItemTypeRegistry.class, itemTypeRegistry);
        dependencyProvider.take(ItemProcessor.class, itemProcessor);
        dependencyProvider.take(DefaultItems.class, new DefaultItems(itemProcessor, itemTypeRegistry));
    }
}
