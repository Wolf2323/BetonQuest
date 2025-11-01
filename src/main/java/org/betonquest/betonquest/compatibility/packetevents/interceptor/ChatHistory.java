package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.google.common.collect.EvictingQueue;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.FunctionWrapperPlayServerChatMessage;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.FunctionWrapperPlayServerDisguisedChat;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.FunctionWrapperPlayServerSystemChatMessage;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatHistory implements PacketListener, Listener {
    /**
     * A prefix that marks messages to be ignored by this interceptor.
     */
    private static final ComponentTagger TAGGER = new ComponentTagger("BetonQuest-Message-Bypass-Tag");

    private final PacketEventsAPI<?> packetEventsAPI;

    private final int cacheSize;

    private final Map<PacketTypeCommon, PacketWrapperFunction<?>> packetTypes;

    private final Map<UUID, EvictingQueue<PacketWrapper<?>>> history;

    public ChatHistory(final PacketEventsAPI<?> packetEventsAPI, final int cacheSize) {
        this.packetEventsAPI = packetEventsAPI;
        this.cacheSize = cacheSize;
        this.packetTypes = getPacketTypes();
        this.history = new HashMap<>();
    }

    private Map<PacketTypeCommon, PacketWrapperFunction<?>> getPacketTypes() {
        return Map.of(
                PacketType.Play.Server.CHAT_MESSAGE, new FunctionWrapperPlayServerChatMessage(),
                PacketType.Play.Server.SYSTEM_CHAT_MESSAGE, new FunctionWrapperPlayServerSystemChatMessage(),
                PacketType.Play.Server.DISGUISED_CHAT, new FunctionWrapperPlayServerDisguisedChat()
        );
    }

    private EvictingQueue<PacketWrapper<?>> getHistory(final UUID uuid) {
        return history.computeIfAbsent(uuid, k -> EvictingQueue.create(cacheSize));
    }

    public void sendHistory(final Player player) {
        final User user = packetEventsAPI.getPlayerManager().getUser(player);
        final EvictingQueue<PacketWrapper<?>> history = getHistory(player.getUniqueId());
        user.sendMessage(Component.text(Component.newline().content().repeat(cacheSize - history.size())));
        for (final PacketWrapper<?> packetWrapper : history) {
            user.sendPacket(packetWrapper);
        }
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final PacketWrapperFunction<?> packetWrapperFunction = packetTypes.get(event.getPacketType());
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
        if (packetWrapper == null) {
            return;
        }
        if (TAGGER.acceptIfTagged(packetWrapperFunction.getMessage(packetWrapper),
                untagged -> packetWrapperFunction.setMessage(packetWrapper, untagged))) {
            return;
        }
        getHistory(((Player) event.getPlayer()).getUniqueId()).add(packetWrapperFunction.transfrom(packetWrapper));
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        history.remove(event.getPlayer().getUniqueId());
    }

    public Component addBypass(final Component component) {
        return TAGGER.prefixTag(component);
    }
}
