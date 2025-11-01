package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import net.kyori.adventure.text.Component;

public class FunctionWrapperPlayServerDisguisedChat implements PacketWrapperFunction<WrapperPlayServerDisguisedChat> {
    @Override
    public WrapperPlayServerDisguisedChat getPacketWrapper(final PacketSendEvent event) {
        return new WrapperPlayServerDisguisedChat(event);
    }

    @Override
    public Component getMessage(final WrapperPlayServerDisguisedChat packetWrapper) {
        return packetWrapper.getMessage();
    }

    @Override
    public WrapperPlayServerDisguisedChat setMessage(final WrapperPlayServerDisguisedChat packetWrapper, final Component message) {
        packetWrapper.setMessage(message);
        return packetWrapper;
    }

    @Override
    public WrapperPlayServerDisguisedChat transfrom(final WrapperPlayServerDisguisedChat packetWrapper) {
        return packetWrapper;
    }
}
