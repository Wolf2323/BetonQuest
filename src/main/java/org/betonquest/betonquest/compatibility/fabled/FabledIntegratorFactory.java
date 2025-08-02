package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link FabledIntegrator} instances.
 */
public class FabledIntegratorFactory implements IntegratorFactory {
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
     * Creates a new instance of the factory.
     *
     * @param plugin          the plugin instance
     * @param server          the server instance
     * @param profileProvider the profile provider instance
     */
    public FabledIntegratorFactory(final Plugin plugin, final Server server, final ProfileProvider profileProvider) {
        this.plugin = plugin;
        this.server = server;
        this.profileProvider = profileProvider;
    }

    @Override
    public Integrator getIntegrator() {
        return new FabledIntegrator(plugin, server, profileProvider);
    }
}
