package org.betonquest.betonquest.quest.action.teleport;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.bukkit.Location;

/**
 * Factory to create teleport actions from {@link Instruction}s.
 */
public class TeleportActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create the teleport action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param conversations the Conversation API
     */
    public TeleportActionFactory(final BetonQuestLoggerFactory loggerFactory, final Conversations conversations) {
        this.loggerFactory = loggerFactory;
        this.conversations = conversations;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        return new OnlineActionAdapter(new TeleportAction(conversations, location),
                loggerFactory.create(TeleportAction.class),
                instruction.getPackage());
    }
}
