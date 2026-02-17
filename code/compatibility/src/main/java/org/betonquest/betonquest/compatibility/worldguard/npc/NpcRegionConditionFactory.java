package org.betonquest.betonquest.compatibility.worldguard.npc;

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

/**
 * Factory to create {@link NpcRegionCondition}s from {@link Instruction}s.
 */
public class NpcRegionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The npc manager to get npcs.
     */
    private final NpcManager npcManager;

    /**
     * Create a new factory for NPC Region Conditions.
     *
     * @param npcManager the npc manager to get npcs
     */
    public NpcRegionConditionFactory(final NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullableConditionAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.identifier(NpcIdentifier.class).get();
        final Argument<String> region = instruction.string().get();
        return new NullableConditionAdapter(new NpcRegionCondition(npcManager, npcId, region));
    }
}
