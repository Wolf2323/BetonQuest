package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.QuestManager;
import org.betonquest.betonquest.config.patcher.migration.QuestMigrator;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.notify.Notify;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link QuestManager}.
 */
public class QuestPackageManagerComponent extends AbstractCoreComponent {

    /**
     * Create a new QuestPackageManagerComponent.
     */
    public QuestPackageManagerComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginDescriptionFile.class,
                BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, ConfigAccessor.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(QuestManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final PluginDescriptionFile descriptionFile = getDependency(PluginDescriptionFile.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);

        final QuestManager questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), configAccessorFactory,
                plugin.getDataFolder(), new QuestMigrator(loggerFactory.create(QuestMigrator.class), descriptionFile));
        Notify.load(config, questManager.getPackages().values());

        dependencyProvider.take(QuestManager.class, questManager);
    }
}
