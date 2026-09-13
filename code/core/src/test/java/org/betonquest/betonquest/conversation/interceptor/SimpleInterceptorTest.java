package org.betonquest.betonquest.conversation.interceptor;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SimpleInterceptor}.
 */
@ExtendWith(MockitoExtension.class)
class SimpleInterceptorTest {

    @Test
    void removes_player_from_chat_recipients_when_intercepting(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final Player sender
    ) {
        when(profile.getPlayer()).thenReturn(player);
        when(sender.getDisplayName()).thenReturn("Sender");

        final SimpleInterceptor interceptor = new SimpleInterceptor(profile);
        final Set<Player> recipients = new HashSet<>();
        recipients.add(player);
        recipients.add(sender);

        final AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, sender, "Message 1", recipients);
        interceptor.onChat(event);

        assertFalse(recipients.contains(player), "Player should be removed from chat recipients");
    }

    @Test
    void transfers_messages_to_next_interceptor(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final Player sender
    ) {
        when(profile.getPlayer()).thenReturn(player);
        when(sender.getDisplayName()).thenReturn("Sender");

        final SimpleInterceptor interceptor1 = new SimpleInterceptor(profile);
        final SimpleInterceptor interceptor2 = new SimpleInterceptor(profile);

        final Set<Player> recipients = new HashSet<>();
        recipients.add(player);

        final AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, sender, "Message 1", recipients);
        interceptor1.onChat(event);

        interceptor1.transferTo(interceptor2);
        interceptor1.end();

        verify(player, never()).sendMessage(anyString());
    }

    @Test
    void delivers_transferred_messages_on_next_interceptor_end(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final Player sender
    ) {
        when(profile.getPlayer()).thenReturn(player);
        when(sender.getDisplayName()).thenReturn("Sender");

        final SimpleInterceptor interceptor1 = new SimpleInterceptor(profile);
        final SimpleInterceptor interceptor2 = new SimpleInterceptor(profile);

        final Set<Player> recipients = new HashSet<>();
        recipients.add(player);

        final AsyncPlayerChatEvent event1 = new AsyncPlayerChatEvent(true, sender, "Message 1", recipients);
        interceptor1.onChat(event1);

        interceptor1.transferTo(interceptor2);

        final Set<Player> recipients2 = new HashSet<>();
        recipients2.add(player);
        final AsyncPlayerChatEvent event2 = new AsyncPlayerChatEvent(true, sender, "Message 2", recipients2);
        interceptor2.onChat(event2);

        interceptor2.end();
        verify(player, times(2)).sendMessage(anyString());
    }

    @Test
    void flushes_messages_when_transferred_to_incompatible_interceptor(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final Player sender,
            @Mock final Interceptor otherInterceptor
    ) {
        when(profile.getPlayer()).thenReturn(player);
        when(sender.getDisplayName()).thenReturn("Sender");

        final SimpleInterceptor interceptor = new SimpleInterceptor(profile);

        final Set<Player> recipients = new HashSet<>();
        recipients.add(player);
        final AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, sender, "Message 1", recipients);
        interceptor.onChat(event);

        interceptor.transferTo(otherInterceptor);

        verify(player, times(1)).sendMessage(anyString());
    }
}
