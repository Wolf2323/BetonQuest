package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.registry.feature.TextParserRegistryImpl;
import org.betonquest.betonquest.text.DecidingTextParser;
import org.betonquest.betonquest.text.TagTextParserDecider;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent}
 * for {@link TextParserRegistryImpl} and {@link DecidingTextParser}.
 */
public class TextParserComponent extends AbstractCoreComponent {

    /**
     * The text parser registry to load.
     */
    @Nullable
    private TextParserRegistryImpl textParserRegistry;

    /**
     * The deciding text parser to load.
     */
    @Nullable
    private DecidingTextParser decidingTextParser;

    /**
     * Create a new TextParserComponent.
     */
    public TextParserComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, ConfigAccessor.class);
    }

    @Override
    public boolean isLoaded() {
        return textParserRegistry != null && decidingTextParser != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);

        this.textParserRegistry = new TextParserRegistryImpl(loggerFactory.create(TextParserRegistryImpl.class));
        final String defaultParser = config.getString("text_parser", "legacyminimessage");
        this.decidingTextParser = new DecidingTextParser(textParserRegistry, new TagTextParserDecider(defaultParser));

        providerCallback.take(TextParserRegistryImpl.class, textParserRegistry);
        providerCallback.take(DecidingTextParser.class, decidingTextParser);
    }
}
