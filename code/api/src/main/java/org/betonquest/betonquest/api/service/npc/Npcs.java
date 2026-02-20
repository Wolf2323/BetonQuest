package org.betonquest.betonquest.api.service.npc;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new npcs with {@link NpcRegistry} or access existing ones with {@link NpcManager}.
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Npcs extends ServiceFeature<NpcManager, NpcRegistry> {

}
