package org.betonquest.betonquest.quest.placeholder.name;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.service.conversation.Conversations;

import java.util.Optional;

/**
 * This placeholder resolves into the name of the Npc in the conversation.
 */
public class QuesterPlaceholder implements PlayerPlaceholder {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create a NpcName placeholder.
     *
     * @param conversations the Conversation API
     */
    public QuesterPlaceholder(final Conversations conversations) {
        this.conversations = conversations;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Optional<Component> conv = conversations.getActiveQuesterName(profile);
        return conv.map(component -> LegacyComponentSerializer.legacySection().serialize(component)).orElse("");
    }
}
