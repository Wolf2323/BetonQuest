package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ConversationColors}.
 */
public class ConversationColorsComponent extends AbstractCoreComponent {

    /**
     * Create a new ConversationColorsComponent.
     */
    public ConversationColorsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(FileConfigAccessor.class, TextParser.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final FileConfigAccessor config = getDependency(FileConfigAccessor.class);
        final TextParser textParser = getDependency(TextParser.class);

        final ConversationColors conversationColors = new ConversationColors(textParser, config);

        dependencyProvider.take(ConversationColors.class, conversationColors);
    }
}
