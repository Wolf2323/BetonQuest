package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.bukkit.Location;

/**
 * Factory to create {@link NpcLocationCondition}s from {@link Instruction}s.
 */
public class NpcLocationConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The npc manager.
     */
    private final NpcManager npcManager;

    /**
     * Create a new factory for NPC Location Conditions.
     *
     * @param npcManager the npc manager
     */
    public NpcLocationConditionFactory(final NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseNpcLocationCondition(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseNpcLocationCondition(instruction);
    }

    private NullableConditionAdapter parseNpcLocationCondition(final Instruction instruction) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.identifier(NpcIdentifier.class).get();
        final Argument<Location> location = instruction.location().get();
        final Argument<Number> radius = instruction.number().get();
        return new NullableConditionAdapter(new NpcLocationCondition(npcManager, npcId, location, radius));
    }
}
