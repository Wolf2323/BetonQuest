package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.logger.HandlerFactory;
import org.betonquest.betonquest.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.logger.handler.chat.ChatHandler;
import org.betonquest.betonquest.logger.handler.history.HistoryHandler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.time.InstantSource;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * The implementation of {@link AbstractCoreComponent} for log handlers.
 */
public class LogHandlerComponent extends AbstractCoreComponent {

    /**
     * Create a new LogHandlerComponent.
     */
    public LogHandlerComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, Server.class, BukkitScheduler.class,
                BetonQuestLoggerFactory.class, FileConfigAccessor.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(HistoryHandler.class, AccumulatingReceiverSelector.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final Server server = getDependency(Server.class);
        final BukkitScheduler scheduler = getDependency(BukkitScheduler.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final FileConfigAccessor config = getDependency(FileConfigAccessor.class);

        final HistoryHandler debugHistoryHandler = HandlerFactory.createHistoryHandler(loggerFactory, plugin,
                scheduler, config, new File(plugin.getDataFolder(), "/logs"), InstantSource.system());
        registerLogHandler(server, debugHistoryHandler);

        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final ChatHandler chatHandler = HandlerFactory.createChatHandler(plugin, server, receiverSelector);
        registerLogHandler(server, chatHandler);

        dependencyProvider.take(HistoryHandler.class, debugHistoryHandler);
        dependencyProvider.take(AccumulatingReceiverSelector.class, receiverSelector);
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    private void registerLogHandler(final Server server, final Handler handler) {
        final Logger serverLogger = server.getLogger().getParent();
        serverLogger.addHandler(handler);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serverLogger.removeHandler(handler);
            handler.close();
        }));
    }
}
