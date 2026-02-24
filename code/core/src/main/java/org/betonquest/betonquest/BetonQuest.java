package org.betonquest.betonquest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.QuestManager;
import org.betonquest.betonquest.config.patcher.migration.Migrator;
import org.betonquest.betonquest.config.patcher.migration.QuestMigrator;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.AsyncSaver;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.CoreComponentLoader;
import org.betonquest.betonquest.kernel.DefaultCoreComponentLoader;
import org.betonquest.betonquest.kernel.component.AsyncSaverComponent;
import org.betonquest.betonquest.kernel.component.CommandsComponent;
import org.betonquest.betonquest.kernel.component.ConversationColorsComponent;
import org.betonquest.betonquest.kernel.component.DatabaseComponent;
import org.betonquest.betonquest.kernel.component.ExecutionCacheComponent;
import org.betonquest.betonquest.kernel.component.FontRegistryComponent;
import org.betonquest.betonquest.kernel.component.GlobalDataComponent;
import org.betonquest.betonquest.kernel.component.ListenersComponent;
import org.betonquest.betonquest.kernel.component.UpdaterComponent;
import org.betonquest.betonquest.kernel.component.types.ActionTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ConditionTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ConversationIOTypesComponent;
import org.betonquest.betonquest.kernel.component.types.InterceptorTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ItemTypesComponent;
import org.betonquest.betonquest.kernel.component.types.NotifyIOTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ObjectiveTypeComponent;
import org.betonquest.betonquest.kernel.component.types.PlaceholderTypeComponent;
import org.betonquest.betonquest.kernel.component.types.ScheduleTypesComponent;
import org.betonquest.betonquest.kernel.component.types.TextParserTypesComponent;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.lib.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.HandlerFactory;
import org.betonquest.betonquest.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.logger.handler.chat.ChatHandler;
import org.betonquest.betonquest.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.betonquest.betonquest.profile.UUIDProfileProvider;
import org.betonquest.betonquest.quest.CoreQuestTypeHandler;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.versioning.java.JREVersionPrinter;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.InstantSource;
import java.util.logging.Handler;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.GodClass", "PMD.TooManyMethods",
        "PMD.TooManyFields", "NullAway.Init"})
public class BetonQuest extends JavaPlugin implements LanguageProvider {

    /**
     * The BetonQuest Plugin instance.
     */
    private static BetonQuest instance;

    /**
     * Factory to create new class-specific loggers.
     */
    private BetonQuestLoggerFactory loggerFactory;

    /**
     * Factory to create new file accessors.
     */
    private ConfigAccessorFactory configAccessorFactory;

    /**
     * The custom logger for the plugin.
     */
    private BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private FileConfigAccessor config;

    /**
     * The default language from the config.
     */
    private String defaultLanguage;

    /**
     * The used Connector for the Database.
     */
    private Connector connector;

    /**
     * The database saver for Quest Data.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private AsyncSaver saver;

    /**
     * The plugin updater.
     */
    private Updater updater;

    /**
     * The Player Hider instance.
     */
    private PlayerHider playerHider;

    /**
     * Cache for action schedulers, holding the last execution of an action.
     */
    private LastExecutionCache lastExecutionCache;

    /**
     * The profile provider instance.
     */
    private ProfileProvider profileProvider;

    /**
     * The quest manager instance.
     */
    private QuestManager questManager;

    /**
     * The colors for conversations.
     */
    private ConversationColors conversationColors;

    /**
     * The Compatibility instance for hooking into other plugins.
     */
    private Compatibility compatibility;

    /**
     * The core quest type handler instance.
     */
    private CoreQuestTypeHandler coreQuestTypeHandler;

    /**
     * The betonQuestApi instance.
     */
    private BetonQuestApi betonQuestApi;

    /**
     * The core component loader instance.
     */
    private CoreComponentLoader coreComponentLoader;

    /**
     * The required default constructor without arguments for plugin creation.
     */
    public BetonQuest() {
        super();
    }

    /**
     * Get the plugin's instance.
     *
     * @return The plugin's instance.
     */
    public static BetonQuest getInstance() {
        return instance;
    }

    private <T> T registerAndGetService(final Class<T> clazz, final T service) {
        final ServicesManager servicesManager = getServer().getServicesManager();
        servicesManager.register(clazz, service, this, ServicePriority.Lowest);
        return servicesManager.load(clazz);
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    @Override
    public void onEnable() {
        instance = this;

        this.loggerFactory = registerAndGetService(BetonQuestLoggerFactory.class, new CachingBetonQuestLoggerFactory(new DefaultBetonQuestLoggerFactory()));
        this.log = loggerFactory.create(this);
        if (!isPaper()) {
            throw new IllegalStateException("Only Paper is supported!");
        }

        this.configAccessorFactory = registerAndGetService(ConfigAccessorFactory.class, new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class)));
        this.profileProvider = registerAndGetService(ProfileProvider.class, new UUIDProfileProvider(getServer()));

        final JREVersionPrinter jreVersionPrinter = new JREVersionPrinter();
        final String jreInfo = jreVersionPrinter.getMessage();
        log.info(jreInfo);

        migrate();

        try {
            config = configAccessorFactory.createPatching(new File(getDataFolder(), "config.yml"), this, "config.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            throw new IllegalStateException("Could not load the config.yml file!", e);
        }
        defaultLanguage = config.getString("language", "en-US");

        final HistoryHandler debugHistoryHandler = HandlerFactory.createHistoryHandler(loggerFactory, this,
                this.getServer().getScheduler(), config, new File(getDataFolder(), "/logs"), InstantSource.system());
        registerLogHandler(getServer(), debugHistoryHandler);
        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final ChatHandler chatHandler = HandlerFactory.createChatHandler(this, getServer(), receiverSelector);
        registerLogHandler(getServer(), chatHandler);

        final String version = getDescription().getVersion();
        log.debug("BetonQuest " + version + " is starting...");
        log.debug(jreInfo);

        questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), configAccessorFactory,
                getDataFolder(), new QuestMigrator(loggerFactory.create(QuestMigrator.class), getDescription()));
        Notify.load(config, questManager.getPackages().values());

        final DefaultCoreComponentLoader coreComponentLoader = new DefaultCoreComponentLoader(loggerFactory.create(DefaultCoreComponentLoader.class));
        this.coreComponentLoader = coreComponentLoader;
        this.coreQuestTypeHandler = new CoreQuestTypeHandler(loggerFactory.create(CoreQuestTypeHandler.class), coreComponentLoader);
        initPluginDependencies(coreComponentLoader);
        registerComponents(coreComponentLoader);
        registerTypesComponents(coreComponentLoader);
        registerCommands(coreComponentLoader, receiverSelector, debugHistoryHandler);
        coreQuestTypeHandler.init();

        this.betonQuestApi = coreComponentLoader.get(BetonQuestApi.class);
        this.compatibility = coreComponentLoader.get(Compatibility.class);
        this.updater = coreComponentLoader.get(Updater.class);
        this.lastExecutionCache = coreComponentLoader.get(LastExecutionCache.class);
        this.saver = coreComponentLoader.get(AsyncSaver.class);
        this.connector = coreComponentLoader.get(Connector.class);

        conversationColors = coreComponentLoader.get(ConversationColors.class);

        // schedule quest data loading on the first tick, so all other
        // plugins can register their types
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            compatibility.postHook();
            loadData();
            coreQuestTypeHandler.getPlayerDataStorage().initProfiles(profileProvider.getOnlineProfiles(), betonQuestApi.conversations());

            try {
                playerHider = new PlayerHider(this, betonQuestApi.conditions().manager(), betonQuestApi.instructions(), questManager, profileProvider, config);
            } catch (final QuestException e) {
                log.error("Could not start PlayerHider! " + e.getMessage(), e);
            }
        });

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            log.warn("Could not disable /betonquestanswer logging", e);
        }

        log.info("BetonQuest successfully enabled!");
    }

    private void initPluginDependencies(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.init(JavaPlugin.class, this);
        coreComponentLoader.init(Server.class, getServer());
        coreComponentLoader.init(PluginManager.class, getServer().getPluginManager());
        coreComponentLoader.init(BukkitScheduler.class, getServer().getScheduler());
        coreComponentLoader.init(PluginDescriptionFile.class, getDescription());
        coreComponentLoader.init(ServicesManager.class, getServer().getServicesManager());
    }

    private void registerTypesComponents(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.register(new ActionTypesComponent());
        coreComponentLoader.register(new ConditionTypesComponent());
        coreComponentLoader.register(new ObjectiveTypeComponent());
        coreComponentLoader.register(new PlaceholderTypeComponent());
        coreComponentLoader.register(new ConversationIOTypesComponent());
        coreComponentLoader.register(new InterceptorTypesComponent());
        coreComponentLoader.register(new ItemTypesComponent());
        coreComponentLoader.register(new NotifyIOTypesComponent());
        coreComponentLoader.register(new ScheduleTypesComponent());
        coreComponentLoader.register(new TextParserTypesComponent());
    }

    private void registerComponents(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.init(LanguageProvider.class, this);
        coreComponentLoader.init(BetonQuestLoggerFactory.class, loggerFactory);
        coreComponentLoader.init(ConfigAccessorFactory.class, configAccessorFactory);
        coreComponentLoader.init(QuestManager.class, questManager);
        coreComponentLoader.init(ProfileProvider.class, profileProvider);
        coreComponentLoader.init(FileConfigAccessor.class, config);

        coreComponentLoader.register(new DatabaseComponent());
        coreComponentLoader.register(new AsyncSaverComponent());
        coreComponentLoader.register(new GlobalDataComponent());
        coreComponentLoader.register(new FontRegistryComponent());
        coreComponentLoader.register(new ListenersComponent());
        coreComponentLoader.register(new UpdaterComponent(this.getFile()));
        coreComponentLoader.register(new ConversationColorsComponent());
        coreComponentLoader.register(new ExecutionCacheComponent());
    }

    private void registerCommands(final CoreComponentLoader coreComponentLoader, final AccumulatingReceiverSelector receiverSelector, final HistoryHandler debugHistoryHandler) {
        coreComponentLoader.init(AccumulatingReceiverSelector.class, receiverSelector);
        coreComponentLoader.init(HistoryHandler.class, debugHistoryHandler);
        coreComponentLoader.register(new CommandsComponent(this::reload));
    }

    private void migrate() {
        try {
            new Migrator(loggerFactory).migrate();
        } catch (final IOException e) {
            log.error("There was an exception while migrating from a previous version! Reason: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    private void registerLogHandler(final Server server, final Handler handler) {
        final java.util.logging.Logger serverLogger = server.getLogger().getParent();
        serverLogger.addHandler(handler);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serverLogger.removeHandler(handler);
            handler.close();
        }));
    }

    /**
     * Loads QuestPackages and refreshes player objectives.
     */
    public void loadData() {
        new LoadDataEvent(LoadDataEvent.State.PRE_LOAD).callEvent();
        coreQuestTypeHandler.loadData(questManager.getPackages().values());
        new LoadDataEvent(LoadDataEvent.State.POST_LOAD).callEvent();
        coreQuestTypeHandler.getPlayerDataStorage().startObjectives();
        coreQuestTypeHandler.getRpgMenu().syncCommands();
    }

    /**
     * Reloads the plugin.
     */
    public void reload() {
        log.debug("Reloading configuration");
        try {
            config.reload();
        } catch (final IOException e) {
            log.warn("Could not reload config! " + e.getMessage(), e);
        }
        defaultLanguage = config.getString("language", "en-US");
        questManager.reload();
        try {
            coreQuestTypeHandler.getPluginMessage().reload();
        } catch (final IOException | QuestException e) {
            log.error("Could not reload the plugin messages!", e);
        }
        Notify.load(config, questManager.getPackages().values());
        lastExecutionCache.reload();

        updater.search();
        conversationColors.load();
        compatibility.reload();
        loadData();
        coreQuestTypeHandler.getPlayerDataStorage().reloadProfiles(profileProvider.getOnlineProfiles());

        playerHider.stop();
        try {
            playerHider = new PlayerHider(this, coreQuestTypeHandler.getConditionProcessor(), betonQuestApi.instructions(),
                    questManager, profileProvider, config);
        } catch (final QuestException e) {
            log.error("Could not start PlayerHider! " + e.getMessage(), e);
        }
        log.debug("Reload complete");
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.AvoidCatchingGenericException"})
    public void onDisable() {
        if (coreQuestTypeHandler != null) {
            try {
                coreQuestTypeHandler.getScheduleProcessor().clear();
            } catch (final Exception ignored) {
                // Empty
            }
        }

        if (profileProvider != null) {
            for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
                final Conversation conv = coreQuestTypeHandler == null ? null : coreQuestTypeHandler.getConversationProcessor().getActiveConversation(onlineProfile);
                if (conv != null) {
                    conv.suspend();
                }
                onlineProfile.getPlayer().closeInventory();
            }
        }

        if (saver != null) {
            saver.end();
        }
        if (compatibility != null) {
            compatibility.disable();
        }
        if (connector != null) {
            connector.getDatabase().closeConnection();
        }
        if (playerHider != null) {
            playerHider.stop();
        }

        if (coreQuestTypeHandler != null) {
            try {
                coreQuestTypeHandler.getRpgMenu().onDisable();
            } catch (final Exception ignored) {
                // Empty
            }
        }
        log.info("BetonQuest successfully disabled!");
    }

    /**
     * Adds a Processor to re-/load data on BetonQuest re-/load.
     *
     * @param processor the processor to register
     */
    public void addProcessor(final QuestProcessor<?, ?> processor) {
        coreQuestTypeHandler.getAdditionalProcessors().add(processor);
    }

    /**
     * Returns the {@link CoreComponentLoader} instance.
     *
     * @return the {@link CoreComponentLoader} instance
     */
    public CoreComponentLoader getComponentLoader() {
        return coreComponentLoader;
    }

    /**
     * Returns the {@link BetonQuestLoggerFactory} instance.
     *
     * @return the {@link BetonQuestLoggerFactory} instance
     */
    public BetonQuestLoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * Get the plugin configuration file.
     *
     * @return config file
     */
    public ConfigAccessor getPluginConfig() {
        return config;
    }

    @Override
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Returns the {@link CoreQuestTypeHandler} instance.
     *
     * @return the {@link CoreQuestTypeHandler} instance
     */
    public CoreQuestTypeHandler getCoreQuestTypeHandler() {
        return coreQuestTypeHandler;
    }

    /**
     * Returns the {@link BetonQuestApi} instance.
     *
     * @return the {@link BetonQuestApi} instance
     */
    public BetonQuestApi getBetonQuestApi() {
        return betonQuestApi;
    }

    /**
     * Get the plugin messages provider.
     *
     * @return plugin messages provider
     */
    public PluginMessage getPluginMessage() {
        return coreQuestTypeHandler.getPluginMessage();
    }

    /**
     * Returns the {@link ProfileProvider} instance.
     *
     * @return the {@link ProfileProvider} instance
     */
    public ProfileProvider getProfileProvider() {
        return profileProvider;
    }

    /**
     * Returns the {@link QuestPackageManager} instance.
     *
     * @return the {@link QuestPackageManager} instance
     */
    public QuestPackageManager getQuestPackageManager() {
        return questManager;
    }

    /**
     * Returns the connector for the database.
     *
     * @return Connector instance
     */
    public Connector getDBConnector() {
        return connector;
    }

    /**
     * Checks if MySQL is used or not.
     *
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return coreComponentLoader.get(DatabaseComponent.class).usesMySQL();
    }

    /**
     * Returns the {@link Saver} instance used by BetonQuest.
     *
     * @return the database saver
     */
    public Saver getSaver() {
        return saver;
    }

    /**
     * Gets the stored player data.
     *
     * @return storage for currently loaded player data
     */
    public PlayerDataStorage getPlayerDataStorage() {
        return coreQuestTypeHandler.getPlayerDataStorage();
    }

    private boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (final ClassNotFoundException exception) {
            try {
                Class.forName("io.papermc.paper.configuration.Configuration");
                return true;
            } catch (final ClassNotFoundException e) {
                return false;
            }
        }
    }

    /**
     * Get the colors used in conversations.
     *
     * @return the colors used in conversations
     */
    public ConversationColors getConversationColors() {
        return conversationColors;
    }

    /**
     * Get the registry for fonts to calculate width of text.
     *
     * @return the font registry
     */
    public FontRegistry getFontRegistry() {
        return coreComponentLoader.get(FontRegistry.class);
    }

    /**
     * Get the Compatibility to add plugins and initialize it.
     *
     * @return the compatibility
     */
    protected Compatibility getCompatibility() {
        return compatibility;
    }
}
