package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Displays the message in the chat.
 */
public class ChatNotifyIO extends NotifyIO {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create a new Chat Notify IO.
     *
     * @param placeholders  the {@link PlaceholderManager} to create and resolve placeholders
     * @param pack          the source pack to resolve placeholders
     * @param data          the customization data for notifications
     * @param conversations the Conversation API
     * @throws QuestException when data could not be parsed
     */
    public ChatNotifyIO(final PlaceholderManager placeholders, @Nullable final QuestPackage pack, final Map<String, String> data, final Conversations conversations) throws QuestException {
        super(placeholders, pack, data);
        this.conversations = conversations;
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        conversations.sendBypassMessage(onlineProfile, message);
    }
}
