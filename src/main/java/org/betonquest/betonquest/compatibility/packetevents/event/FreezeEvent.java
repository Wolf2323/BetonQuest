package org.betonquest.betonquest.compatibility.packetevents.event;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.packetevents.passenger.FakeArmorStandPassenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Event to block all player entity movement.
 */
public class FreezeEvent implements OnlineEvent {
    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * Freeze duration.
     */
    private final Variable<Number> ticksVar;

    /**
     * Create a new event that freezes a player.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param ticks           the freeze duration
     */
    public FreezeEvent(final PacketEventsAPI<?> packetEventsAPI, final Variable<Number> ticks) {
        this.packetEventsAPI = packetEventsAPI;
        ticksVar = ticks;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final int ticks = ticksVar.getValue(profile).intValue();
        final Player player = profile.getPlayer();

        final FakeArmorStandPassenger armorStandPassenger = new FakeArmorStandPassenger(packetEventsAPI, player);
        armorStandPassenger.mount(player.getLocation().add(0, -1, 0));

        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), armorStandPassenger::unmount, ticks);
    }
}
