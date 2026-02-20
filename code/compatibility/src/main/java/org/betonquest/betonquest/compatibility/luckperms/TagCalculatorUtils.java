package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Provides all per-player tags and all global tags as LuckPerms
 * contexts.
 */
public final class TagCalculatorUtils {

    /**
     * The BetonQuest tag.
     */
    public static final String KEY_LOCAL = "betonquest:tag:";

    /**
     * The global BetonQuest tag.
     */
    public static final String KEY_GLOBAL = "betonquest:globaltag:";

    private TagCalculatorUtils() {
    }

    /**
     * Get an anonymous ContextCalculator. It has to be anonymous to prevent the loading of the class when no LP is installed.
     *
     * @param globalData the global data
     * @return a ContextCalculator
     */
    public static ContextCalculator<Player> getTagContextCalculator(final GlobalData globalData) {
        return (player, contextConsumer) -> {
            if (player.isOnline()) {
                final PlayerData data = BetonQuest.getInstance().getPlayerDataStorage().get(BetonQuest.getInstance().getProfileProvider().getProfile(player));
                data.getTags().forEach(tag -> contextConsumer.accept(KEY_LOCAL + tag, "true"));
            }
            final Set<String> globalDataTags = globalData.getTags();
            globalDataTags.forEach(tag -> contextConsumer.accept(KEY_GLOBAL + tag, "true"));
        };
    }
}
