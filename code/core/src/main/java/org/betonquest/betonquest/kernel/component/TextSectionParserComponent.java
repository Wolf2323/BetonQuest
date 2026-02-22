package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ParsedSectionTextCreator}.
 */
public class TextSectionParserComponent extends AbstractCoreComponent {

    /**
     * Create a new TextSectionParserComponent.
     */
    public TextSectionParserComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(LanguageProvider.class, PlayerDataStorage.class, TextParser.class, PlaceholderManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final TextParser textParser = getDependency(TextParser.class);
        final PlaceholderManager placeholderManager = getDependency(PlaceholderManager.class);

        final ParsedSectionTextCreator parsedSectionTextCreator = new ParsedSectionTextCreator(textParser, playerDataStorage, languageProvider, placeholderManager);

        dependencyProvider.take(ParsedSectionTextCreator.class, parsedSectionTextCreator);
    }
}
