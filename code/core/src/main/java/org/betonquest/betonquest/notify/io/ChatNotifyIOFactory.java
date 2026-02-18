package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link ChatNotifyIO}s.
 */
public class ChatNotifyIOFactory implements NotifyIOFactory {

    /**
     * The {@link PlaceholderManager} to create and resolve placeholders.
     */
    private final PlaceholderManager placeholders;

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create a new Chat Notify IO.
     *
     * @param placeholders  the {@link PlaceholderManager} to create and resolve placeholders
     * @param conversations the Conversation API
     */
    public ChatNotifyIOFactory(final PlaceholderManager placeholders, final Conversations conversations) {
        this.placeholders = placeholders;
        this.conversations = conversations;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new ChatNotifyIO(placeholders, pack, categoryData, conversations);
    }
}
