package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Cancels the conversation.
 */
public class CancelConversationAction implements OnlineAction {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create a new conversation cancel action.
     *
     * @param conversations the Conversation API
     */
    public CancelConversationAction(final Conversations conversations) {
        this.conversations = conversations;
    }

    @Override
    public void execute(final OnlineProfile profile) {
        conversations.cancel(profile);
    }
}
