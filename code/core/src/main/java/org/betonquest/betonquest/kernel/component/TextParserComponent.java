package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.registry.feature.TextParserRegistryImpl;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.text.DecidingTextParser;
import org.betonquest.betonquest.text.TagTextParserDecider;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent}
 * for {@link TextParserRegistryImpl} and {@link DecidingTextParser}.
 */
public class TextParserComponent extends AbstractCoreComponent {

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
    public Set<Class<?>> provides() {
        return Set.of(TextParserRegistryImpl.class, DecidingTextParser.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);

        final TextParserRegistryImpl textParserRegistry = new TextParserRegistryImpl(loggerFactory.create(TextParserRegistryImpl.class));
        final String defaultParser = config.getString("text_parser", "legacyminimessage");
        final DecidingTextParser decidingTextParser = new DecidingTextParser(textParserRegistry, new TagTextParserDecider(defaultParser));

        dependencyProvider.take(TextParserRegistryImpl.class, textParserRegistry);
        dependencyProvider.take(DecidingTextParser.class, decidingTextParser);
    }
}
