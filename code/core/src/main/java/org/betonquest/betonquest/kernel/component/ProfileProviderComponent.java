package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.profile.UUIDProfileProvider;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ProfileProvider}.
 */
public class ProfileProviderComponent extends AbstractCoreComponent {

    /**
     * Create a new ProfileProviderComponent.
     */
    public ProfileProviderComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, Server.class, ServicesManager.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ProfileProvider.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final ServicesManager servicesManager = getDependency(ServicesManager.class);
        final Server server = getDependency(Server.class);

        final UUIDProfileProvider profileProvider = new UUIDProfileProvider(server);

        servicesManager.register(ProfileProvider.class, profileProvider, plugin, ServicePriority.Lowest);

        dependencyProvider.take(ProfileProvider.class, servicesManager.load(ProfileProvider.class));
    }
}
