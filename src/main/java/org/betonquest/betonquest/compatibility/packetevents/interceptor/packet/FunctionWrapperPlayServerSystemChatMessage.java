package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;

public class FunctionWrapperPlayServerSystemChatMessage implements PacketWrapperFunction<WrapperPlayServerSystemChatMessage> {
    @Override
    public WrapperPlayServerSystemChatMessage getPacketWrapper(final PacketSendEvent event) {
        return new WrapperPlayServerSystemChatMessage(event);
    }

    @Override
    public Component getMessage(final WrapperPlayServerSystemChatMessage packetWrapper) {
        return packetWrapper.getMessage();
    }

    @Override
    public WrapperPlayServerSystemChatMessage setMessage(final WrapperPlayServerSystemChatMessage packetWrapper, final Component message) {
        packetWrapper.setMessage(message);
        return packetWrapper;
    }

    @Override
    public WrapperPlayServerSystemChatMessage transfrom(final WrapperPlayServerSystemChatMessage packetWrapper) {
        return packetWrapper;
    }
}
