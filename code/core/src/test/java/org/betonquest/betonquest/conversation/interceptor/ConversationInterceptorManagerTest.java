package org.betonquest.betonquest.conversation.interceptor;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ConversationInterceptorManager}.
 */
@ExtendWith(MockitoExtension.class)
class ConversationInterceptorManagerTest {

    private static Server server;

    private static Logger logger;

    @Mock
    private Plugin plugin;

    @Mock
    private ProfileProvider profileProvider;

    @Mock
    private OnlineProfile onlineProfile;

    @Mock
    private Player player;

    @Mock
    private Interceptor interceptor;

    @Mock
    private Interceptor nextInterceptor;

    private BukkitSchedulerMock scheduler;

    private ConversationInterceptorManager manager;

    @BeforeAll
    static void initializeBukkit() {
        if (Bukkit.getServer() == null) {
            logger = mock(Logger.class);
            server = mock(Server.class);
            when(server.getLogger()).thenReturn(logger);
            Bukkit.setServer(server);
        } else {
            server = Bukkit.getServer();
        }
    }

    @BeforeEach
    void setUp() {
        final UUID uuid = UUID.randomUUID();
        lenient().when(onlineProfile.getPlayerUUID()).thenReturn(uuid);
        lenient().when(onlineProfile.getProfileUUID()).thenReturn(uuid);

        scheduler = new BukkitSchedulerMock();
        when(server.getScheduler()).thenReturn(scheduler);

        manager = new ConversationInterceptorManager(plugin, profileProvider);
    }

    @AfterEach
    void tearDown() {
        scheduler.close();
    }

    @Test
    void scheduleInterceptorEndImmediatelyWhenDelayZero() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 0);
        verify(interceptor).end();
    }

    @Test
    void scheduleInterceptorEndImmediatelyWhenDelayNegative() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, -5);
        verify(interceptor).end();
    }

    @Test
    void scheduleInterceptorEndAfterDelay() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        verify(interceptor, never()).end();

        scheduler.performTicks(10);
        scheduler.waitAsyncTasksFinished();

        verify(interceptor).end();
    }

    @Test
    void scheduleInterceptorEndTransfersExistingPendingInterceptor() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        manager.scheduleInterceptorEnd(onlineProfile, nextInterceptor, 10);

        verify(interceptor).transferTo(nextInterceptor);
        verify(nextInterceptor, never()).end();

        scheduler.performTicks(10);
        scheduler.waitAsyncTasksFinished();

        verify(nextInterceptor).end();
    }

    @Test
    void transferPendingInterceptorTransfersAndCancelsTask() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        manager.transferPendingInterceptor(onlineProfile, nextInterceptor);

        verify(interceptor).transferTo(nextInterceptor);

        scheduler.performTicks(10);
        scheduler.waitAsyncTasksFinished();

        verify(interceptor, never()).end();
    }

    @Test
    void endPendingInterceptorEndsImmediately() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        manager.endPendingInterceptor(onlineProfile, interceptor);

        verify(interceptor).end();

        scheduler.performTicks(10);
        scheduler.waitAsyncTasksFinished();

        verify(interceptor, times(1)).end();
    }

    @Test
    void cancelPendingInterceptorCancelsAndEnds() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        manager.cancelPendingInterceptor(onlineProfile);

        verify(interceptor).end();

        scheduler.performTicks(10);
        scheduler.waitAsyncTasksFinished();

        verify(interceptor, times(1)).end();
    }

    @Test
    void sendBypassMessageHandlesWhenPendingExists() {
        final Component message = Component.text("Test message");
        assertFalse(manager.sendBypassMessage(onlineProfile, message));

        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        assertTrue(manager.sendBypassMessage(onlineProfile, message));
        verify(interceptor).sendMessage(message);
    }

    @Test
    void clearEndsAllPendingInterceptors() {
        manager.scheduleInterceptorEnd(onlineProfile, interceptor, 10);
        manager.clear();

        verify(interceptor).end();

        scheduler.performTicks(10);
        scheduler.waitAsyncTasksFinished();

        verify(interceptor, times(1)).end();
    }
}
