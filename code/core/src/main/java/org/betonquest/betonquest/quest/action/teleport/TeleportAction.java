package org.betonquest.betonquest.quest.action.teleport;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.bukkit.Location;

/**
 * Teleports the player to specified location.
 */
public class TeleportAction implements OnlineAction {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Location to teleport to.
     */
    private final Argument<Location> location;

    /**
     * Create a new teleport action that teleports the player to the given location.
     *
     * @param conversations the Conversation API
     * @param location      location to teleport to
     */
    public TeleportAction(final Conversations conversations, final Argument<Location> location) {
        this.conversations = conversations;
        this.location = location;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        conversations.cancel(profile);
        final Location playerLocation = location.getValue(profile);
        profile.getPlayer().teleport(playerLocation);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
