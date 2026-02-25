package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.profile.UUIDProfileProvider;
import org.bukkit.Server;

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
        return Set.of(Server.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Server server = getDependency(Server.class);

        final UUIDProfileProvider profileProvider = new UUIDProfileProvider(server);

        dependencyProvider.take(UUIDProfileProvider.class, profileProvider);
    }
}
