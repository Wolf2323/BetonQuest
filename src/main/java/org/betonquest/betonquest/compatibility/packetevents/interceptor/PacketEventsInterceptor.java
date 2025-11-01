package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.FunctionWrapperPlayServerChatMessage;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.FunctionWrapperPlayServerDisguisedChat;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.FunctionWrapperPlayServerSystemChatMessage;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketEventsInterceptor implements Interceptor, PacketListener {
    /**
     * A prefix that marks messages to be ignored by this interceptor.
     */
    private static final ComponentTagger TAGGER = new ComponentTagger("BetonQuest-Message-Passthrough-Tag");

    private final PacketEventsAPI<?> packetEventsAPI;

    private final ChatHistory chatHistory;

    private final OnlineProfile onlineProfile;

    private final Map<PacketTypeCommon, PacketWrapperFunction<?>> packetTypes;

    private final PacketListenerCommon packetListenerCommon;

    private final List<PacketWrapper<?>> messages;

    public PacketEventsInterceptor(final PacketEventsAPI<?> packetEventsAPI, final ChatHistory chatHistory, final OnlineProfile onlineProfile) {
        this.packetEventsAPI = packetEventsAPI;
        this.chatHistory = chatHistory;
        this.onlineProfile = onlineProfile;
        this.packetTypes = new HashMap<>();
        packetTypes.put(PacketType.Play.Server.CHAT_MESSAGE, new FunctionWrapperPlayServerChatMessage());
        packetTypes.put(PacketType.Play.Server.SYSTEM_CHAT_MESSAGE, new FunctionWrapperPlayServerSystemChatMessage());
        packetTypes.put(PacketType.Play.Server.DISGUISED_CHAT, new FunctionWrapperPlayServerDisguisedChat());
        packetListenerCommon = packetEventsAPI.getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
        this.messages = new ArrayList<>();
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!((Player) event.getPlayer()).getUniqueId().equals(onlineProfile.getPlayerUUID())) {
            return;
        }
        final PacketWrapperFunction<? extends PacketWrapper<?>> packetWrapperFunction = packetTypes.get(event.getPacketType());
        if (packetWrapperFunction == null) {
            return;
        }
        handlePacketWrapperFunction(packetWrapperFunction, event);
    }

    private <T extends PacketWrapper<?>> void handlePacketWrapperFunction(
            final PacketWrapperFunction<T> packetWrapperFunction,
            final PacketSendEvent event
    ) {
        final T packetWrapper = packetWrapperFunction.getPacketWrapper(event);
        if (TAGGER.acceptIfTagged(packetWrapperFunction.getMessage(packetWrapper),
                untagged -> packetWrapperFunction.setMessage(packetWrapper, untagged))) {
            return;
        }
        event.setCancelled(true);
        messages.add(packetWrapper);
    }

    @Override
    public void sendMessage(final Component component) {
        onlineProfile.getPlayer().sendMessage(TAGGER.prefixTag(chatHistory.addBypass(component)));
    }

    @Override
    public void end() {
        final User user = packetEventsAPI.getPlayerManager().getUser(onlineProfile.getPlayer());
        chatHistory.sendHistory(onlineProfile.getPlayer());
        messages.forEach(user::sendPacket);

        packetEventsAPI.getEventManager().unregisterListener(packetListenerCommon);
    }
}
