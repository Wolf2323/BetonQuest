package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.fakeblock.event.FakeBlockEventFactory;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integrates with FakeBlock.
 */
public class FakeBlockIntegrator implements Integrator {
    /**
     * The minimum required version of FakeBlock.
     */
    public static final String REQUIRED_VERSION = "2.0.1";

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Create the FakeBlock integration.
     *
     * @param plugin the plugin instance
     * @param server the server instance
     */
    public FakeBlockIntegrator(final Plugin plugin, final Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) throws HookException {
        checkRequiredVersion();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final RegisteredServiceProvider<GroupService> groupService = getServiceProvider(GroupService.class);
        final RegisteredServiceProvider<PlayerGroupService> playerGroupService = getServiceProvider(PlayerGroupService.class);

        questTypeRegistries.event().register("fakeblock",
                new FakeBlockEventFactory(groupService, playerGroupService, data));
    }

    private void checkRequiredVersion() throws UnsupportedVersionException {
        final Plugin fakeBlockPlugin = Bukkit.getPluginManager().getPlugin("fake-block");
        if (fakeBlockPlugin != null) {
            final Version version = new Version(fakeBlockPlugin.getDescription().getVersion());
            final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
            if (comparator.isOtherNewerThanCurrent(version, new Version(REQUIRED_VERSION))) {
                throw new UnsupportedVersionException(plugin, REQUIRED_VERSION);
            }
        }
    }

    private <T> RegisteredServiceProvider<T> getServiceProvider(final Class<T> service) throws HookException {
        final RegisteredServiceProvider<T> provider = plugin.getServer().getServicesManager().getRegistration(service);
        if (provider == null) {
            throw new HookException(plugin, "Could not find service provider for " + service.getName());
        }
        return provider;
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
