package org.betonquest.betonquest.quest.placeholder.name;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.legacy.LegacyConversations;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.conversation.Conversation;

/**
 * This placeholder resolves into the name of the Npc in the conversation.
 */
public class QuesterPlaceholder implements PlayerPlaceholder {

    /**
     * Conversation API.
     */
    private final LegacyConversations conversationApi;

    /**
     * Create a NpcName placeholder.
     *
     * @param conversationApi the Conversation API
     */
    public QuesterPlaceholder(final LegacyConversations conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Conversation conv = conversationApi.getActive(profile);
        if (conv == null) {
            return "";
        }
        return LegacyComponentSerializer.legacySection().serialize(conv.getData().getPublicData().quester().asComponent(profile));
    }
}
