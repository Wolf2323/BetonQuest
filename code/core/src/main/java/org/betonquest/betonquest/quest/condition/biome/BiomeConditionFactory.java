package org.betonquest.betonquest.quest.condition.biome;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.block.Biome;

/**
 * Factory for {@link BiomeCondition}s.
 */
public class BiomeConditionFactory implements PlayerConditionFactory {

    /**
     * Create the biome factory.
     */
    public BiomeConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Biome> biome = instruction.enumeration(Biome.class).get();
        return new OnlineConditionAdapter(new BiomeCondition(biome));
    }
}
