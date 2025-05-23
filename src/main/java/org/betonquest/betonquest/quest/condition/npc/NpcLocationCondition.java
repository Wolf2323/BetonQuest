package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a npc is at a specific location.
 */
public class NpcLocationCondition implements NullableCondition {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Id of the npc.
     */
    private final Variable<NpcID> npcId;

    /**
     * The location where the NPC has to be around.
     */
    private final Variable<Location> location;

    /**
     * The maximal distance between the NPC and the radius location.
     */
    private final Variable<Number> radius;

    /**
     * Create a new NPCLocationCondition.
     *
     * @param featureAPI the Quest Type API
     * @param npcId      the id of the npc
     * @param location   the location where the npc has to be around
     * @param radius     the maximal distance between the npc and the radius location
     */
    public NpcLocationCondition(final FeatureAPI featureAPI, final Variable<NpcID> npcId,
                                final Variable<Location> location, final Variable<Number> radius) {
        this.featureAPI = featureAPI;
        this.npcId = npcId;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Npc<?> npc = featureAPI.getNpc(npcId.getValue(profile), profile);
        final Location location = this.location.getValue(profile);
        final Location npcLocation = npc.getLocation();
        if (!location.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(location) <= radius * radius;
    }
}
