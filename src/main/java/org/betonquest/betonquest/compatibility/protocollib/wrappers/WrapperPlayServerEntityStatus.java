package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 dmulloy2.net
 * Copyright (C) Kristian S. Strangeland
 * GNU GPL 3 licensed
 */
public class WrapperPlayServerEntityStatus extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_STATUS;

    public WrapperPlayServerEntityStatus() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityStatus(final PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(final int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(final World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(final PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve Entity Status.
     * <p>
     * Notes: see below
     *
     * @return The current Entity Status
     */
    public byte getEntityStatus() {
        return handle.getBytes().read(0);
    }

    /**
     * Set Entity Status.
     *
     * @param value - new value.
     */
    public void setEntityStatus(final byte value) {
        handle.getBytes().write(0, value);
    }
}
