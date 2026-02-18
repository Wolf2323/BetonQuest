package org.betonquest.betonquest.api.service.feature;

import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.api.service.npc.Npcs;

/**
 * Default implementation of the {@link Npcs} service.
 *
 * @param manager  the npc manager
 * @param registry the npc registry
 */
public record DefaultNpcs(NpcManager manager, NpcRegistry registry) implements Npcs {

}
