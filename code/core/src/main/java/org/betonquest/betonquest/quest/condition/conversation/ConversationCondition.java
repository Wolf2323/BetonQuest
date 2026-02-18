package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Checks if the conversation with a player has at least one possible option.
 */
public class ConversationCondition implements PlayerCondition {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * The conversation to check.
     */
    private final Argument<ConversationIdentifier> conversationID;

    /**
     * Creates a new ConversationCondition.
     *
     * @param conversations  the Conversation API
     * @param conversationID the conversation to check
     */
    public ConversationCondition(final Conversations conversations, final Argument<ConversationIdentifier> conversationID) {
        this.conversations = conversations;
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return conversations.canStart(profile, conversationID.getValue(profile));
    }
}
