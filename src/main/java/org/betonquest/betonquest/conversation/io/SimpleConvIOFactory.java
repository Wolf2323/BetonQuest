package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Simple chat-based conversation output.
 */
public class SimpleConvIOFactory implements ConversationIOFactory {
    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new Simple conversation IO factory.
     *
     * @param colors the colors used for the conversation
     */
    public SimpleConvIOFactory(final ConversationColors colors) {
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        return new SimpleConvIO(conversation, onlineProfile, colors);
    }
}
