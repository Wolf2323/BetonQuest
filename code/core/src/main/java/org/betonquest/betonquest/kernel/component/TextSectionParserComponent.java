package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ParsedSectionTextCreator}.
 */
public class TextSectionParserComponent extends AbstractCoreComponent {

    /**
     * The parsed section text creator to load.
     */
    @Nullable
    private ParsedSectionTextCreator parsedSectionTextCreator;

    /**
     * Create a new TextSectionParserComponent.
     */
    public TextSectionParserComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(LanguageProvider.class, PlayerDataStorage.class, TextParser.class, PlaceholderProcessor.class);
    }

    @Override
    public boolean isLoaded() {
        return parsedSectionTextCreator != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final TextParser textParser = getDependency(TextParser.class);
        final PlaceholderProcessor placeholderProcessor = getDependency(PlaceholderProcessor.class);

        this.parsedSectionTextCreator = new ParsedSectionTextCreator(textParser, playerDataStorage, languageProvider, placeholderProcessor);

        dependencyProvider.take(ParsedSectionTextCreator.class, parsedSectionTextCreator);
    }
}
