package org.betonquest.betonquest.quest.condition.biome;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.block.Biome;

/**
 * Requires the player to be in a specified biome.
 */
public class BiomeCondition implements OnlineCondition {

    /**
     * The biome to check for.
     */
    private final Biome biome;

    /**
     * Creates a new BiomeCondition.
     *
     * @param biome The biome to check for
     */
    public BiomeCondition(final Biome biome) {
        this.biome = biome;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return profile.getPlayer().getLocation().getBlock().getBiome() == biome;
    }
}
