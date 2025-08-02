package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.event.SpellInventoryEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

/**
 * Integrator for the Magic plugin.
 */
public class MagicIntegrator implements Integrator, Listener {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The player data storage instance.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The plugin message instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * The default constructor.
     *
     * @param plugin          the plugin instance
     * @param loggerFactory   the logger factory used by BetonQuest
     * @param profileProvider the profile provider instance
     * @param dataStorage     the player data storage instance
     * @param pluginMessage   the plugin message instance
     * @param server          the server instance
     */
    public MagicIntegrator(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory,
                           final ProfileProvider profileProvider, final PlayerDataStorage dataStorage,
                           final PluginMessage pluginMessage, final Server server) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
        this.server = server;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        final PluginManager manager = server.getPluginManager();
        final MagicAPI api = Objects.requireNonNull((MagicAPI) manager.getPlugin("Magic"));
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        questTypeRegistries.condition().register("wand", new WandConditionFactory(loggerFactory, api, data));
        manager.registerEvents(this, plugin);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }

    /**
     * Updates the player's journal when the spell inventory closes.
     *
     * @param event the even to listen
     */
    @EventHandler(ignoreCancelled = true)
    public void onSpellInventoryEvent(final SpellInventoryEvent event) {
        if (!event.isOpening()) {
            final OnlineProfile onlineProfile = profileProvider.getProfile(event.getMage().getPlayer());
            dataStorage.get(onlineProfile).getJournal(pluginMessage).update();
        }
    }
}
