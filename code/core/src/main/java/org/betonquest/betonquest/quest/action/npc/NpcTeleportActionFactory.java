package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.NpcManager;
import org.bukkit.Location;

/**
 * Factory for {@link NpcTeleportAction} from the {@link Instruction}.
 */
public class NpcTeleportActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The npc manager.
     */
    private final NpcManager npcManager;

    /**
     * Create a new factory for Npc Teleport Actions.
     *
     * @param npcManager the npc manager
     */
    public NpcTeleportActionFactory(final NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createNpcTeleportAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createNpcTeleportAction(instruction);
    }

    private NullableActionAdapter createNpcTeleportAction(final Instruction instruction) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.identifier(NpcIdentifier.class).get();
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> spawn = instruction.bool().getFlag("spawn", true);
        return new NullableActionAdapter(new NpcTeleportAction(npcManager, npcId, location, spawn));
    }
}
