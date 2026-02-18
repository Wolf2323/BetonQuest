package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Factory to create conversation cancel actions from {@link Instruction}s.
 */
public class CancelConversationActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create the conversation cancel action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param conversations the Conversation API
     */
    public CancelConversationActionFactory(final BetonQuestLoggerFactory loggerFactory, final Conversations conversations) {
        this.loggerFactory = loggerFactory;
        this.conversations = conversations;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(
                new CancelConversationAction(conversations),
                loggerFactory.create(CancelConversationAction.class),
                instruction.getPackage()
        );
    }
}
