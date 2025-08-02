package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.denizen.event.DenizenTaskScriptEventFactory;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for Denizen.
 */
public class DenizenIntegrator implements Integrator {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * The default constructor.
     *
     * @param plugin the plugin instance
     * @param server the server instance
     */
    public DenizenIntegrator(final Plugin plugin, final Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        questTypeRegistries.event().register("script", new DenizenTaskScriptEventFactory(data));
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
