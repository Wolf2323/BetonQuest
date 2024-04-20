package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public final class DefaultPacketHandler implements PacketHandler {

    private final PacketContainer handle;

    private final PacketType type;

    /**
     * Constructs a new strongly typed wrapper with a new packet.
     *
     * @param type - the packet type.
     */
    private DefaultPacketHandler(final PacketType type) {
        this(new PacketContainer(type), type);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new strongly typed wrapper for the given packet.
     *
     * @param handle - handle to the raw packet data.
     * @param type   - the packet type.
     */
    private DefaultPacketHandler(final PacketContainer handle, final PacketType type) {
        this(handle);
        if (!Objects.equal(handle.getType(), type)) {
            throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
        }
    }

    /**
     * Constructs a new wrapper for the given packet.
     *
     * @param handle - handle to the raw packet data.
     */
    private DefaultPacketHandler(final PacketContainer handle) {
        this.handle = handle;
        this.type = handle.getType();
    }

    @Override
    public PacketContainer getHandle() {
        return handle;
    }

    @Override
    public PacketType getType() {
        return type;
    }

    @Override
    public void sendPacket(final Player receiver) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, getHandle());
    }

    @Override
    public void broadcastPacket() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHandle());
    }

    @Override
    public void receivePacket(final Player sender) {
        ProtocolLibrary.getProtocolManager().receiveClientPacket(sender, getHandle());
    }
}
