package org.betonquest.betonquest.quest.action.chat;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * The chat action factory.
 */
public class ChatActionFactory implements PlayerActionFactory {

    /**
     * Create the chat action factory.
     */
    public ChatActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        final String[] messages = String.join(" ", instruction.getValueParts()).split("\\|");
        return new OnlineActionAdapter(new ChatAction(messages));
    }
}
