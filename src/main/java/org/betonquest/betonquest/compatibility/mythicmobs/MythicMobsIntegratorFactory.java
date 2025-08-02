package org.betonquest.betonquest.compatibility.mythicmobs;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link MythicMobsIntegrator} instances.
 */
public class MythicMobsIntegratorFactory implements IntegratorFactory {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The compatibility instance to use.
     */
    private final Compatibility compatibility;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin          the plugin instance
     * @param profileProvider the profile provider instance
     * @param compatibility   the compatibility instance to use
     * @param server          the server instance
     */
    public MythicMobsIntegratorFactory(final Plugin plugin, final ProfileProvider profileProvider, final Compatibility compatibility, final Server server) {
        this.plugin = plugin;
        this.profileProvider = profileProvider;
        this.compatibility = compatibility;
        this.server = server;
    }

    @Override
    public Integrator getIntegrator() {
        return new MythicMobsIntegrator(plugin, profileProvider, compatibility, server);
    }
}
