package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;

public class PacketEventsInterceptorFactory implements InterceptorFactory {
    private final PacketEventsAPI<?> packetEventsAPI;

    private final ChatHistory chatHistory;

    public PacketEventsInterceptorFactory(final PacketEventsAPI<?> packetEventsAPI, final ChatHistory chatHistory) {
        this.packetEventsAPI = packetEventsAPI;
        this.chatHistory = chatHistory;
    }

    @Override
    public Interceptor create(final OnlineProfile onlineProfile) {
        return new PacketEventsInterceptor(packetEventsAPI, chatHistory, onlineProfile);
    }
}
