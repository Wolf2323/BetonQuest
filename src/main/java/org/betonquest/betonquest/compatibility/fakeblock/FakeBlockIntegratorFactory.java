package org.betonquest.betonquest.compatibility.fakeblock;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link FakeBlockIntegrator} instances.
 */
public class FakeBlockIntegratorFactory implements IntegratorFactory {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin the plugin instance
     * @param server the server instance
     */
    public FakeBlockIntegratorFactory(final Plugin plugin, final Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public Integrator getIntegrator() {
        return new FakeBlockIntegrator(plugin, server);
    }
}
