package org.betonquest.betonquest.quest.placeholder.name;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Factory to create {@link QuesterPlaceholder}s from {@link Instruction}s.
 */
public class QuesterPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create a NpcName placeholder factory.
     *
     * @param conversations the Conversation API
     */
    public QuesterPlaceholderFactory(final Conversations conversations) {
        this.conversations = conversations;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) {
        return new QuesterPlaceholder(conversations);
    }
}
