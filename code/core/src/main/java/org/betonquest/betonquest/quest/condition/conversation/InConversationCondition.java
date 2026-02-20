package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Condition to check if a player is in a conversation or, if specified, in the specified conversation.
 */
public class InConversationCondition implements PlayerCondition {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Identifier of the conversation.
     */
    @Nullable
    private final Argument<ConversationIdentifier> conversationID;

    /**
     * Constructor of the InConversationCondition.
     *
     * @param conversations  the Conversation API
     * @param conversationID the conversation identifier
     */
    public InConversationCondition(final Conversations conversations, @Nullable final Argument<ConversationIdentifier> conversationID) {
        this.conversations = conversations;
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Optional<ConversationIdentifier> conversation = conversations.getActive(profile);
        return conversation.isPresent() && (conversationID == null || conversation.get().equals(conversationID.getValue(profile)));
    }
}
