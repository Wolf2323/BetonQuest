package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link CancelConversationAction}.
 */
@ExtendWith(MockitoExtension.class)
class CancelConversationActionTest {

    @Test
    void cancels_conversation_without_skip_delay(
            @Mock final Conversations conversations,
            @Mock final OnlineProfile profile
    ) throws QuestException {
        final CancelConversationAction action = new CancelConversationAction(conversations, p -> Optional.empty());
        action.execute(profile);
        verify(conversations).cancel(profile, false);
    }

    @Test
    void cancels_conversation_with_skip_delay(
            @Mock final Conversations conversations,
            @Mock final OnlineProfile profile
    ) throws QuestException {
        final CancelConversationAction action = new CancelConversationAction(conversations, p -> Optional.of(true));
        action.execute(profile);
        verify(conversations).cancel(profile, true);
    }
}
