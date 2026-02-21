package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.bukkit.Server;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultArgumentParsers}.
 */
public class ArgumentParsersComponent extends AbstractCoreComponent {

    /**
     * The default argument parsers to load.
     */
    @Nullable
    private DefaultArgumentParsers defaultArgumentParsers;

    /**
     * Create a new ArgumentParsersComponent.
     */
    public ArgumentParsersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Server.class, ItemManager.class, ItemIdentifierFactory.class, TextParser.class, Identifiers.class);
    }

    @Override
    public boolean isLoaded() {
        return defaultArgumentParsers != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final ItemManager itemManager = getDependency(ItemManager.class);
        final ItemIdentifierFactory itemIdentifierFactory = getDependency(ItemIdentifierFactory.class);
        final TextParser textParser = getDependency(TextParser.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Server server = getDependency(Server.class);

        this.defaultArgumentParsers = new DefaultArgumentParsers(itemManager, itemIdentifierFactory, textParser, server, identifiers);

        providerCallback.take(DefaultArgumentParsers.class, defaultArgumentParsers);
    }
}
