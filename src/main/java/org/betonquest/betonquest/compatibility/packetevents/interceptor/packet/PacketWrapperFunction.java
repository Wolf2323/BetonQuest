package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.kyori.adventure.text.Component;

public interface PacketWrapperFunction<T extends PacketWrapper<?>> {
    T getPacketWrapper(PacketSendEvent event);

    Component getMessage(T packetWrapper);

    T setMessage(T packetWrapper, Component message);

    PacketWrapper<?> transfrom(T packetWrapper);
}
