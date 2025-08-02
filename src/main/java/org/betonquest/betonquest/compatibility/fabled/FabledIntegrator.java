package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledClassConditionFactory;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledLevelConditionFactory;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for Fabled.
 */
public class FabledIntegrator implements Integrator {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The default constructor.
     *
     * @param plugin          the plugin instance
     * @param server          the server instance
     * @param profileProvider the profile provider instance
     */
    public FabledIntegrator(final Plugin plugin, final Server server, final ProfileProvider profileProvider) {
        this.plugin = plugin;
        this.server = server;
        this.profileProvider = profileProvider;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final ConditionTypeRegistry conditionTypes = questTypeRegistries.condition();
        conditionTypes.register("fabledclass", new FabledClassConditionFactory(data));
        conditionTypes.register("fabledlevel", new FabledLevelConditionFactory(data));
        server.getPluginManager().registerEvents(new FabledKillListener(profileProvider), plugin);
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
