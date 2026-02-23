package org.betonquest.betonquest;

import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.command.BackpackCommand;
import org.betonquest.betonquest.command.CancelQuestCommand;
import org.betonquest.betonquest.command.CompassCommand;
import org.betonquest.betonquest.command.JournalCommand;
import org.betonquest.betonquest.command.LangCommand;
import org.betonquest.betonquest.command.QuestCommand;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.QuestManager;
import org.betonquest.betonquest.config.patcher.migration.Migrator;
import org.betonquest.betonquest.config.patcher.migration.QuestMigrator;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.AsyncSaver;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.kernel.CoreComponentLoader;
import org.betonquest.betonquest.kernel.DefaultCoreComponentLoader;
import org.betonquest.betonquest.kernel.component.ConversationColorsComponent;
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
import org.betonquest.betonquest.lib.font.FontRetriever;
import org.betonquest.betonquest.lib.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.listener.CustomDropListener;
import org.betonquest.betonquest.listener.JoinQuitListener;
import org.betonquest.betonquest.listener.MobKillListener;
import org.betonquest.betonquest.listener.QuestItemConvertListener;
import org.betonquest.betonquest.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.HandlerFactory;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.logger.handler.chat.ChatHandler;
import org.betonquest.betonquest.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.betonquest.betonquest.profile.UUIDProfileProvider;
import org.betonquest.betonquest.quest.CoreQuestTypeHandler;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.java.JREVersionPrinter;
import org.betonquest.betonquest.web.DownloadSource;
import org.betonquest.betonquest.web.TempFileDownloadSource;
import org.betonquest.betonquest.web.WebContentSource;
import org.betonquest.betonquest.web.WebDownloadSource;
import org.betonquest.betonquest.web.updater.UpdateDownloader;
import org.betonquest.betonquest.web.updater.UpdateSourceHandler;
import org.betonquest.betonquest.web.updater.Updater;
import org.betonquest.betonquest.web.updater.UpdaterConfig;
import org.betonquest.betonquest.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.web.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.web.updater.source.implementations.GitHubReleaseSource;
import org.betonquest.betonquest.web.updater.source.implementations.ReposiliteReleaseAndDevelopmentSource;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.InstantSource;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.GodClass", "PMD.TooManyMethods",
        "PMD.TooManyFields", "NullAway.Init"})
public class BetonQuest extends JavaPlugin implements LanguageProvider {

    /**
     * The indicator for dev versions.
     */
    private static final String DEV_INDICATOR = "DEV";

    /**
     * The File where last executions should be cached.
     */
    private static final String CACHE_FILE = ".cache/schedules.yml";

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
     * If MySQL is used.
     */
    private boolean usesMySQL;

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
     * The Global Quest Data.
     */
    private GlobalData globalData;

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
     * The registry for fonts to calculate width of text.
     */
    private FontRegistry fontRegistry;

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

    @SuppressWarnings({"PMD.NcssCount", "PMD.DoNotUseThreads", "PMD.CyclomaticComplexity"})
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

        setupDatabase();

        saver = new AsyncSaver(loggerFactory.create(AsyncSaver.class, "Database"), config.getLong("mysql.reconnect_interval"), connector);
        saver.start();
        new Backup(loggerFactory.create(Backup.class), configAccessorFactory, getDataFolder(), connector)
                .loadDatabaseFromBackup();

        globalData = new GlobalData(loggerFactory.create(GlobalData.class), saver, connector);

        final FileConfigAccessor cache;
        try {
            final Path cacheFile = new File(getDataFolder(), CACHE_FILE).toPath();
            if (!Files.exists(cacheFile)) {
                Files.createDirectories(Optional.ofNullable(cacheFile.getParent()).orElseThrow());
                Files.createFile(cacheFile);
            }
            cache = configAccessorFactory.create(cacheFile.toFile());
        } catch (final IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Error while loading schedule cache: " + e.getMessage(), e);
        }
        lastExecutionCache = new LastExecutionCache(loggerFactory.create(LastExecutionCache.class, "Cache"), cache);

        setupFontRegistry();

        final DefaultCoreComponentLoader coreComponentLoader = new DefaultCoreComponentLoader(loggerFactory.create(DefaultCoreComponentLoader.class));
        this.coreQuestTypeHandler = new CoreQuestTypeHandler(loggerFactory.create(CoreQuestTypeHandler.class), coreComponentLoader);

        coreComponentLoader.init(JavaPlugin.class, this);
        coreComponentLoader.init(Server.class, getServer());
        coreComponentLoader.init(PluginManager.class, getServer().getPluginManager());
        coreComponentLoader.init(BukkitScheduler.class, getServer().getScheduler());
        coreComponentLoader.init(PluginDescriptionFile.class, getDescription());
        coreComponentLoader.init(LanguageProvider.class, this);
        coreComponentLoader.init(ServicesManager.class, getServer().getServicesManager());
        coreComponentLoader.init(BetonQuestLoggerFactory.class, loggerFactory);
        coreComponentLoader.init(ConfigAccessorFactory.class, configAccessorFactory);
        coreComponentLoader.init(QuestManager.class, questManager);
        coreComponentLoader.init(ProfileProvider.class, profileProvider);
        coreComponentLoader.init(GlobalData.class, globalData);
        coreComponentLoader.init(Connector.class, connector);
        coreComponentLoader.init(AsyncSaver.class, saver);
        coreComponentLoader.init(LastExecutionCache.class, lastExecutionCache);
        coreComponentLoader.init(FileConfigAccessor.class, config);
        coreComponentLoader.init(FontRegistry.class, fontRegistry);

        registerCoreQuestTypes(coreComponentLoader);
        coreComponentLoader.register(new ConversationColorsComponent());
        registerFeatureQuestTypes(coreComponentLoader);

        coreQuestTypeHandler.init();
        this.betonQuestApi = coreComponentLoader.get(BetonQuestApi.class);
        this.compatibility = coreComponentLoader.get(Compatibility.class);

        setupUpdater();
        registerListener();

        conversationColors = coreComponentLoader.get(ConversationColors.class);

        registerCommands(receiverSelector, debugHistoryHandler, coreQuestTypeHandler.getPlayerDataFactory());

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

    private void registerCoreQuestTypes(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.register(new ActionTypesComponent());
        coreComponentLoader.register(new ConditionTypesComponent());
        coreComponentLoader.register(new ObjectiveTypeComponent());
        coreComponentLoader.register(new PlaceholderTypeComponent());
    }

    private void registerFeatureQuestTypes(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.register(new ConversationIOTypesComponent());
        coreComponentLoader.register(new InterceptorTypesComponent());
        coreComponentLoader.register(new ItemTypesComponent());
        coreComponentLoader.register(new NotifyIOTypesComponent());
        coreComponentLoader.register(new ScheduleTypesComponent());
        coreComponentLoader.register(new TextParserTypesComponent());
    }

    private void setupFontRegistry() {
        final Key defaultkey = Key.key("default");
        final File fontFolder = new File(getDataFolder(), "fonts");
        final FontRetriever fontRetriever = new FontRetriever();
        fontRegistry = new FontRegistry(defaultkey);
        saveResource("fonts/default.font.bin", true);
        final List<Pair<Key, Font>> fonts = fontRetriever.loadFonts(fontFolder.toPath());
        fonts.forEach(pair -> fontRegistry.registerFont(pair.getKey(), pair.getValue()));
        log.info("Loaded " + fonts.size() + " font index files.");
        if (fonts.isEmpty()) {
            throw new IllegalStateException("Could not load fonts!");
        }
    }

    private void setupDatabase() {
        final boolean mySQLEnabled = config.getBoolean("mysql.enabled", true);
        Database database = null;
        if (mySQLEnabled) {
            log.debug("Connecting to MySQL database");
            final Database mySql = new MySQL(loggerFactory.create(MySQL.class, "Database"), this,
                    config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.base"),
                    config.getString("mysql.user"),
                    config.getString("mysql.pass"));
            try {
                mySql.getConnection();
                database = mySql;
                usesMySQL = true;
                log.info("Successfully connected to MySQL database!");
            } catch (final IllegalStateException e) {
                log.warn("MySQL: " + e.getMessage(), e);
            }
        }
        if (database == null) {
            database = new SQLite(loggerFactory.create(SQLite.class, "Database"), this, "database.db");
            if (mySQLEnabled) {
                log.warn("No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                log.info("Using SQLite for storing data!");
            }
        }

        database.createTables();
        this.connector = new Connector(loggerFactory.create(Connector.class), config.getString("mysql.prefix"), database);
    }

    private void registerListener() {
        final IdentifierFactory<ItemIdentifier> itemIdentifierFactory;
        try {
            itemIdentifierFactory = betonQuestApi.identifiers().getFactory(ItemIdentifier.class);
        } catch (final QuestException e) {
            throw new IllegalStateException("Could not register the listeners!", e);
        }
        final PluginManager pluginManager = getServer().getPluginManager();
        List.of(
                new CombatTagger(profileProvider, config.getInt("conversation.damage.combat_delay")),
                new MobKillListener(profileProvider),
                new CustomDropListener(loggerFactory.create(CustomDropListener.class), this, betonQuestApi.items().manager(), itemIdentifierFactory),
                new QuestItemHandler(config, coreQuestTypeHandler.getPlayerDataStorage(), profileProvider),
                new QuestItemConvertListener(loggerFactory.create(QuestItemConvertListener.class),
                        () -> config.getBoolean("item.quest.update_legacy_on_join"), coreQuestTypeHandler.getPluginMessage(), profileProvider),
                new JoinQuitListener(config, coreQuestTypeHandler.getObjectiveProcessor(), coreQuestTypeHandler.getPlayerDataStorage(),
                        betonQuestApi.conversations(), profileProvider, updater)
        ).forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    private void registerCommands(final AccumulatingReceiverSelector receiverSelector, final HistoryHandler debugHistoryHandler,
                                  final PlayerDataFactory playerDataFactory) {
        final PlayerDataStorage playerDataStorage = coreQuestTypeHandler.getPlayerDataStorage();
        final PluginMessage pluginMessage = coreQuestTypeHandler.getPluginMessage();
        final QuestCommand.ConstructorParams questCommandParams = new QuestCommand.ConstructorParams(loggerFactory, configAccessorFactory, playerDataFactory, playerDataStorage,
                profileProvider, pluginMessage, updater, compatibility, connector, saver, questManager, config,
                debugHistoryHandler, new PlayerLogWatcher(receiverSelector), betonQuestApi.identifiers(), globalData,
                coreQuestTypeHandler.getJournalEntryProcessor(), coreQuestTypeHandler.getItemRegistry(), betonQuestApi.actions().manager(),
                betonQuestApi.conditions().manager(), betonQuestApi.objectives().manager(), betonQuestApi.items().manager(), this::reload);
        final QuestCommand questCommand = new QuestCommand(this, loggerFactory.create(QuestCommand.class), questCommandParams);
        getCommand("betonquest").setExecutor(questCommand);
        getCommand("betonquest").setTabCompleter(questCommand);
        getCommand("journal").setExecutor(new JournalCommand(playerDataStorage, profileProvider));
        getCommand("backpack").setExecutor(new BackpackCommand(this, loggerFactory, loggerFactory.create(BackpackCommand.class),
                config, pluginMessage, profileProvider, playerDataStorage, coreQuestTypeHandler.getCancelerProcessor(),
                coreQuestTypeHandler.getCompassProcessor(), betonQuestApi.items().manager(), betonQuestApi.identifiers()));
        getCommand("cancelquest").setExecutor(new CancelQuestCommand(this, config, pluginMessage, profileProvider,
                loggerFactory, playerDataStorage, coreQuestTypeHandler.getCancelerProcessor(), coreQuestTypeHandler.getCompassProcessor(),
                betonQuestApi.identifiers(), betonQuestApi.items().manager()));
        getCommand("compass").setExecutor(new CompassCommand(this, loggerFactory,
                config, pluginMessage, profileProvider, playerDataStorage, coreQuestTypeHandler.getCancelerProcessor(),
                coreQuestTypeHandler.getCompassProcessor(), betonQuestApi.items().manager(), betonQuestApi.identifiers()));
        final LangCommand langCommand = new LangCommand(loggerFactory.create(LangCommand.class), playerDataStorage, pluginMessage, profileProvider, this);
        getCommand("questlang").setExecutor(langCommand);
        getCommand("questlang").setTabCompleter(langCommand);
        getCommand("betonquestanswer").setTabCompleter((sender, command, label, args) -> List.of());
    }

    private void migrate() {
        try {
            new Migrator(loggerFactory).migrate();
        } catch (final IOException e) {
            log.error("There was an exception while migrating from a previous version! Reason: " + e.getMessage(), e);
        }
    }

    private void setupUpdater() {
        final File updateFolder = getServer().getUpdateFolderFile();
        final File file = new File(updateFolder, this.getFile().getName());
        final DownloadSource downloadSource = new TempFileDownloadSource(new WebDownloadSource());
        final UpdateDownloader updateDownloader = new UpdateDownloader(downloadSource, file);

        final ReposiliteReleaseAndDevelopmentSource reposiliteReleaseAndDevelopmentSource =
                new ReposiliteReleaseAndDevelopmentSource("https://repo.betonquest.org",
                        "betonquest", "BetonQuest", new WebContentSource());
        final GitHubReleaseSource gitHubReleaseSource = new GitHubReleaseSource(
                "https://api.github.com/repos/BetonQuest/BetonQuest",
                new WebContentSource(GitHubReleaseSource.HTTP_CODE_HANDLER));
        final List<ReleaseUpdateSource> releaseHandlers = List.of(reposiliteReleaseAndDevelopmentSource, gitHubReleaseSource);
        final List<DevelopmentUpdateSource> developmentHandlers = List.of(reposiliteReleaseAndDevelopmentSource);
        final UpdateSourceHandler updateSourceHandler = new UpdateSourceHandler(loggerFactory.create(UpdateSourceHandler.class),
                releaseHandlers, developmentHandlers);

        final Version pluginVersion = new Version(this.getDescription().getVersion());
        final UpdaterConfig updaterConfig = new UpdaterConfig(loggerFactory.create(UpdaterConfig.class), config, pluginVersion, DEV_INDICATOR);
        updater = new Updater(loggerFactory.create(Updater.class), updaterConfig, pluginVersion, updateSourceHandler, updateDownloader,
                this, getServer().getScheduler(), InstantSource.system());
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
        log.debug("Restarting global locations");
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
        return usesMySQL;
    }

    /**
     * Retrieves GlobalData object which handles all global tags and points.
     *
     * @return GlobalData object
     */
    public GlobalData getGlobalData() {
        return globalData;
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
        return fontRegistry;
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
