package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link PluginMessage}.
 */
public class PluginMessageComponent extends AbstractCoreComponent {

    /**
     * The plugin message processor to load.
     */
    @Nullable
    private PluginMessage pluginMessage;

    /**
     * Create a new PluginMessageComponent.
     */
    public PluginMessageComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, LanguageProvider.class,
                PlayerDataStorage.class, PlaceholderProcessor.class, TextParser.class);
    }

    @Override
    public boolean isLoaded() {
        return pluginMessage != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final PlaceholderProcessor placeholderProcessor = getDependency(PlaceholderProcessor.class);
        final TextParser textParser = getDependency(TextParser.class);
        final Plugin plugin = getDependency(Plugin.class);

        try {
            pluginMessage = new PluginMessage(loggerFactory.create(PluginMessage.class), plugin, placeholderProcessor,
                    playerDataStorage, textParser, configAccessorFactory, languageProvider);
        } catch (final QuestException e) {
            throw new IllegalStateException("Failed to load plugin message component: %s".formatted(e.getMessage()), e);
        }

        dependencyProvider.take(PluginMessage.class, pluginMessage);
    }
}
