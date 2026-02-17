package org.betonquest.betonquest.api.service.placeholder;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;

/**
 * Stores the placeholder factories.
 */
public interface PlaceholderRegistry extends CoreQuestRegistry<PlayerPlaceholder, PlayerlessPlaceholder> {

}
