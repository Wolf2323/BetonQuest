package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.config.patcher.migration.Migrator;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.CoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link FileConfigAccessor}.
 */
public class ConfigComponent extends AbstractCoreComponent {

    /**
     * The configuration file name.
     */
    public static final String CONFIG_FILE = "config.yml";

    /**
     * Create a new ConfigComponent.
     */
    public ConfigComponent() {
        super();
    }

    /**
     * It does not require the Migrator component, however, it needs to be loaded after it.
     *
     * @see CoreComponent#requires()
     */
    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, ConfigAccessorFactory.class, Migrator.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);

        final File dataFolder = plugin.getDataFolder();
        final File configurationFile = new File(dataFolder, CONFIG_FILE);

        try {
            final FileConfigAccessor config = configAccessorFactory.createPatching(configurationFile, plugin, CONFIG_FILE);
            dependencyProvider.take(FileConfigAccessor.class, config);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            throw new IllegalStateException("Could not load the %s file!".formatted(CONFIG_FILE), e);
        }
    }
}
