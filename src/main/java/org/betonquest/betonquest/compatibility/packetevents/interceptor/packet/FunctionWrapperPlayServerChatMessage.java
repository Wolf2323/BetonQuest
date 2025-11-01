package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_1;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import net.kyori.adventure.text.Component;

public class FunctionWrapperPlayServerChatMessage implements PacketWrapperFunction<WrapperPlayServerChatMessage> {
    @Override
    public WrapperPlayServerChatMessage getPacketWrapper(final PacketSendEvent event) {
        return new WrapperPlayServerChatMessage(event);
    }

    @Override
    public Component getMessage(final WrapperPlayServerChatMessage packetWrapper) {
        return packetWrapper.getMessage().getChatContent();
    }

    @Override
    public WrapperPlayServerChatMessage setMessage(final WrapperPlayServerChatMessage packetWrapper, final Component message) {
        packetWrapper.getMessage().setChatContent(message);
        return packetWrapper;
    }

    @Override
    public WrapperPlayServerDisguisedChat transfrom(final WrapperPlayServerChatMessage packetWrapper) {
        final ChatMessage message = packetWrapper.getMessage();
        final ChatType.Bound bound;
        if (message instanceof final ChatMessage_v1_19_3 dotThree) {
            bound = dotThree.getChatFormatting();
        } else if (message instanceof final ChatMessage_v1_19_1 dotOne) {
            bound = dotOne.getChatFormatting();
        } else if (message instanceof final ChatMessage_v1_19 dotNot) {
            bound = new ChatType.Bound(message.getType(), dotNot.getSenderDisplayName(), null);
        } else {
            bound = new ChatType.Bound(message.getType(), Component.text("From: " + ((ChatMessage_v1_16) message).getSenderUUID()), null);
        }
        return new WrapperPlayServerDisguisedChat(message.getChatContent(), bound);
    }
}
