package org.betonquest.betonquest.kernel.component;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.bukkit.Server;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link PlayerDataStorage}.
 */
public class PlayerDataStorageComponent extends AbstractCoreComponent {

    /**
     * The player data factory to load.
     */
    @Nullable
    private PlayerDataFactory playerDataFactory;

    /**
     * The player data storage to load.
     */
    @Nullable
    private PlayerDataStorage playerDataStorage;

    /**
     * Create a new PlayerDataStorageComponent.
     */
    public PlayerDataStorageComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, ConfigAccessor.class, Saver.class, Identifiers.class,
                ProfileProvider.class, ObjectiveProcessor.class, Server.class);
    }

    @Override
    public boolean requires(final Class<?> type) {
        return JournalFactory.class.isAssignableFrom(type) || super.requires(type);
    }

    @Override
    public boolean isLoaded() {
        return playerDataFactory != null && playerDataStorage != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Saver saver = getDependency(Saver.class);
        final ObjectiveProcessor objectiveProcessor = getDependency(ObjectiveProcessor.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Server server = getDependency(Server.class);

        this.playerDataFactory = new PlayerDataFactory(loggerFactory, saver, server,
                identifiers, objectiveProcessor, Suppliers.memoize(() -> getDependency(JournalFactory.class)));
        this.playerDataStorage = new PlayerDataStorage(loggerFactory.create(PlayerDataStorage.class), config,
                playerDataFactory, objectiveProcessor, profileProvider);

        dependencyProvider.take(PlayerDataFactory.class, playerDataFactory);
        dependencyProvider.take(PlayerDataStorage.class, playerDataStorage);
    }
}
