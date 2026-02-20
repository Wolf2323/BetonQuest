package org.betonquest.betonquest.quest.action.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Starts a conversation.
 */
public class ConversationAction implements OnlineAction {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * The conversation to start.
     */
    private final Argument<Pair<ConversationIdentifier, String>> conversation;

    /**
     * Creates a new ConversationAction.
     *
     * @param conversations the Conversation API
     * @param conversation  the conversation and option to start as a pair
     */
    public ConversationAction(final Conversations conversations, final Argument<Pair<ConversationIdentifier, String>> conversation) {
        this.conversations = conversations;
        this.conversation = conversation;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Pair<ConversationIdentifier, String> conversation = this.conversation.getValue(profile);
        conversations.start(profile, conversation.getKey(), profile.getPlayer().getLocation(), conversation.getValue());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
