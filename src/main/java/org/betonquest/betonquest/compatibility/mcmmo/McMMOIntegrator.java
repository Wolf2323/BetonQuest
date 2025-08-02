package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for McMMO.
 */
public class McMMOIntegrator implements Integrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
     * The server instance.
     */
    private final Server server;

    /**
     * The default constructor.
     *
     * @param plugin          the plugin instance
     * @param loggerFactory   the logger factory used by BetonQuest
     * @param profileProvider the profile provider instance
     * @param server          the server instance
     */
    public McMMOIntegrator(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory, final ProfileProvider profileProvider, final Server server) {
        this.plugin = plugin;
        this.log = loggerFactory.create(getClass());
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
        this.server = server;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        questTypeRegistries.condition().register("mcmmolevel", new McMMOSkillLevelConditionFactory(loggerFactory, data));
        questTypeRegistries.event().register("mcmmoexp", new McMMOAddExpEventFactory(loggerFactory, data));
        try {
            server.getPluginManager().registerEvents(new MCMMOQuestItemHandler(profileProvider), plugin);
            log.debug("Enabled MCMMO QuestItemHandler");
        } catch (final LinkageError e) {
            log.warn("MCMMO version is not compatible with the QuestItemHandler.", e);
        }
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
