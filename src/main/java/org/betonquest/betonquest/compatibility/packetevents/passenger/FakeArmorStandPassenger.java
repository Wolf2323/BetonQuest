package org.betonquest.betonquest.compatibility.packetevents.passenger;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Created a fake armor stand amd mounts the player on it.
 */
public class FakeArmorStandPassenger {
    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * The player to mount.
     */
    private final Player player;

    /**
     * The armor stand entity ID.
     */
    private final int armorStandId;

    /**
     * Constructs a new FakeArmorStandPassenger that also created a new entity ID for the armor stand.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param player          the player to mount
     */
    public FakeArmorStandPassenger(final PacketEventsAPI<?> packetEventsAPI, final Player player) {
        this.packetEventsAPI = packetEventsAPI;
        this.player = player;
        this.armorStandId = Bukkit.getUnsafe().nextEntityId();
    }

    /**
     * Spawns a fake armor stand and mounts the player on it.
     *
     * @param location the location to mount at
     */
    public void mount(final Location location) {
        // TODO version switch:
        //  Remove this code when only 1.20.2+ is supported
        final double heightFix = PaperLib.isVersion(20, 2) ? -0.375 : -0.131_25;
        final Vector3d position = new Vector3d(location.getX(), location.getY() + heightFix, location.getZ());

        final WrapperPlayServerSpawnEntity standSpawnPacket = new WrapperPlayServerSpawnEntity(armorStandId,
                Optional.empty(), EntityTypes.ARMOR_STAND, position, 0, 0, 0, 0,
                Optional.empty());

        final WrapperPlayServerEntityMetadata standMetadataPacket = new WrapperPlayServerEntityMetadata(armorStandId,
                List.of(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20)));

        final WrapperPlayServerSetPassengers standPassengersPacket = new WrapperPlayServerSetPassengers(
                armorStandId, new int[]{player.getEntityId()});

        packetEventsAPI.getPlayerManager().sendPacket(player, standSpawnPacket);
        packetEventsAPI.getPlayerManager().sendPacket(player, standMetadataPacket);
        packetEventsAPI.getPlayerManager().sendPacket(player, standPassengersPacket);

        player.sendActionBar(Component.empty());
    }

    /**
     * Unmounts the player and destroys the fake armor stand.
     */
    public void unmount() {
        final WrapperPlayServerDestroyEntities standDestroyPacket = new WrapperPlayServerDestroyEntities(armorStandId);
        packetEventsAPI.getPlayerManager().sendPacket(player, standDestroyPacket);
    }
}
