package org.betonquest.betonquest.compatibility.packetevents.action;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory to create {@link FreezeAction}s from {@link Instruction}s.
 */
public class FreezeActionFactory implements PlayerActionFactory {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * Create a new freeze action factory.
     *
     * @param plugin          the plugin instance
     * @param packetEventsAPI the PacketEvents API instance
     */
    public FreezeActionFactory(final Plugin plugin, final PacketEventsAPI<?> packetEventsAPI) {
        this.plugin = plugin;
        this.packetEventsAPI = packetEventsAPI;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> ticks = instruction.number().atLeast(1).get();
        return new OnlineActionAdapter(new FreezeAction(plugin, packetEventsAPI, ticks));
    }
}
