package org.betonquest.betonquest.quest;

import com.google.common.base.Supplier;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.feature.DefaultActions;
import org.betonquest.betonquest.api.service.feature.DefaultConditions;
import org.betonquest.betonquest.api.service.feature.DefaultItems;
import org.betonquest.betonquest.api.service.feature.DefaultNpcs;
import org.betonquest.betonquest.api.service.feature.DefaultObjectives;
import org.betonquest.betonquest.api.service.feature.DefaultPlaceholders;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.DefaultCoreComponentLoader;
import org.betonquest.betonquest.kernel.component.ActionsComponent;
import org.betonquest.betonquest.kernel.component.ArgumentParsersComponent;
import org.betonquest.betonquest.kernel.component.BetonQuestApiComponent;
import org.betonquest.betonquest.kernel.component.CancelersComponent;
import org.betonquest.betonquest.kernel.component.CompassComponent;
import org.betonquest.betonquest.kernel.component.ConditionsComponent;
import org.betonquest.betonquest.kernel.component.ConversationsComponent;
import org.betonquest.betonquest.kernel.component.IdentifiersComponent;
import org.betonquest.betonquest.kernel.component.InstructionsComponent;
import org.betonquest.betonquest.kernel.component.ItemsComponent;
import org.betonquest.betonquest.kernel.component.JournalsComponent;
import org.betonquest.betonquest.kernel.component.NotificationsComponent;
import org.betonquest.betonquest.kernel.component.NpcsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
import org.betonquest.betonquest.kernel.component.PlaceholdersComponent;
import org.betonquest.betonquest.kernel.component.PlayerDataStorageComponent;
import org.betonquest.betonquest.kernel.component.PluginMessageComponent;
import org.betonquest.betonquest.kernel.component.RPGMenuComponent;
import org.betonquest.betonquest.kernel.component.SchedulesComponent;
import org.betonquest.betonquest.kernel.component.TextParserComponent;
import org.betonquest.betonquest.kernel.component.TextSectionParserComponent;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.kernel.processor.StartTask;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.kernel.registry.feature.TextParserRegistryImpl;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.schedule.ActionScheduling;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Responsible for loading the quest factory types in the correct order.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.TooManyMethods", "PMD.TooManyFields"})
public class CoreQuestTypeHandler {

    /**
     * The plugin instance primarily used to handle scheduling and bukkit events.
     */
    private final Plugin plugin;

    /**
     * The logger used for the {@link CoreQuestTypeHandler}.
     */
    private final BetonQuestLogger log;

    /**
     * The logger factory to create loggers for each individual type.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The profile provider offering access to player profiles.
     */
    private final ProfileProvider profileProvider;

    /**
     * The quest package manager to access quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The bukkit scheduler to schedule tasks.
     */
    private final BukkitScheduler bukkitScheduler;

    /**
     * The bukkit server instance.
     */
    private final Server server;

    /**
     * The bukkit plugin manager to register listeners.
     */
    private final PluginManager pluginManager;

    /**
     * The config accessor factory handles configuration.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * The config of BetonQuest.
     */
    private final ConfigAccessor config;

    /**
     * The language provider.
     */
    private final LanguageProvider languageProvider;

    /**
     * The Saver is responsible for accessing the database.
     */
    private final Saver saver;

    /**
     * The font registry is responsible for loading fonts.
     */
    private final FontRegistry fontRegistry;

    /**
     * Contains all processors after initialization to execute functions for all of them iteratively.
     */
    private final Collection<QuestProcessor<?, ?>> allProcessors;

    /**
     * Contains all processors that are not part of the main quest processing chain.
     */
    private final Collection<QuestProcessor<?, ?>> additionalProcessors;

    /**
     * The default core component loader to load the components in this class.
     */
    private final DefaultCoreComponentLoader coreComponentLoader;

    /**
     * The identifier factory for conversations.
     */
    @Nullable
    private IdentifierFactory<ConversationIdentifier> conversationIdentifierFactory;

    /**
     * The identifier factory for items.
     */
    @Nullable
    private IdentifierFactory<ItemIdentifier> itemIdentifierFactory;

    /**
     * Enables accessing player data.
     */
    @Nullable
    private PlayerDataStorage playerDataStorage;

    /**
     * Used to create player data instances.
     */
    @Nullable
    private PlayerDataFactory playerDataFactory;

    /**
     * Loads sections of text and identifies languages in them as well.
     */
    @Nullable
    private ParsedSectionTextCreator parsedSectionTextCreator;

    /**
     * The plugin message instance handling internal messages.
     */
    @Nullable
    private PluginMessage pluginMessage;

    /**
     * The text parser is responsible for parsing text.
     */
    @Nullable
    private TextParser textParser;

    /**
     * The instructions to use to create and parse instructions.
     */
    @Nullable
    private Instructions instructions;

    /**
     * The argument parsers are responsible for parsing arguments.
     */
    @Nullable
    private Supplier<ArgumentParsers> argumentParsers;

    /**
     * The action type registry allows the registration of new action types.
     */
    @Nullable
    private ActionTypeRegistry actionTypeRegistry;

    /**
     * The condition type registry allows the registration of new condition types.
     */
    @Nullable
    private ConditionTypeRegistry conditionTypeRegistry;

    /**
     * The objective type registry allows the registration of new objective types.
     */
    @Nullable
    private ObjectiveTypeRegistry objectiveTypeRegistry;

    /**
     * The placeholder type registry allows the registration of new placeholder types.
     */
    @Nullable
    private PlaceholderTypeRegistry placeholderTypeRegistry;

    /**
     * The identifier type registry allows the registration of new identifier types.
     */
    @Nullable
    private IdentifierTypeRegistry identifierTypeRegistry;

    /**
     * The item type registry allows the registration of new item types.
     */
    @Nullable
    private ItemTypeRegistry itemRegistry;

    /**
     * The npc type registry allows the registration of new npc types.
     */
    @Nullable
    private NpcTypeRegistry npcRegistry;

    /**
     * The conversation io registry allows the registration of new conversation IOs.
     */
    @Nullable
    private ConversationIORegistry conversationIORegistry;

    /**
     * The interceptor registry allows the registration of new interceptors.
     */
    @Nullable
    private InterceptorRegistry interceptorRegistry;

    /**
     * The notify io registry allows the registration of new notify IOs.
     */
    @Nullable
    private NotifyIORegistry notifyIORegistry;

    /**
     * The schedule registry allows the registration of new schedules.
     */
    @Nullable
    private ScheduleRegistry scheduleRegistry;

    /**
     * The text parser registry allows the registration of new text parsers.
     */
    @Nullable
    private TextParserRegistryImpl textParserRegistry;

    /**
     * The action processor is responsible for managing actions.
     */
    @Nullable
    private ActionProcessor actionProcessor;

    /**
     * The condition processor is responsible for managing conditions.
     */
    @Nullable
    private ConditionProcessor conditionProcessor;

    /**
     * The objective processor is responsible for managing objectives.
     */
    @Nullable
    private ObjectiveProcessor objectiveProcessor;

    /**
     * The placeholder processor is responsible for managing placeholders.
     */
    @Nullable
    private PlaceholderProcessor placeholderProcessor;

    /**
     * The item processor is responsible for managing items.
     */
    @Nullable
    private ItemProcessor itemProcessor;

    /**
     * The npc processor is responsible for managing npcs.
     */
    @Nullable
    private NpcProcessor npcProcessor;

    /**
     * The conversation processor is responsible for managing conversations.
     */
    @Nullable
    private ConversationProcessor conversationProcessor;

    /**
     * The compass processor is responsible for managing compasses.
     */
    @Nullable
    private CompassProcessor compassProcessor;

    /**
     * The journal entry processor is responsible for managing journal entries.
     */
    @Nullable
    private JournalEntryProcessor journalEntryProcessor;

    /**
     * The journal main page processor is responsible for managing journal main pages.
     */
    @Nullable
    private JournalMainPageProcessor journalMainPageProcessor;

    /**
     * The canceler processor is responsible for managing cancelers.
     */
    @Nullable
    private CancelerProcessor cancelerProcessor;

    /**
     * The schedule processor is responsible for managing schedules.
     */
    @Nullable
    private ActionScheduling scheduleProcessor;

    /**
     * The rpg menu instance.
     */
    @Nullable
    private RPGMenu rpgMenu;

    /**
     * The beton quest api instance.
     */
    @Nullable
    private BetonQuestApi betonQuestApi;

    /**
     * Sole constructor.
     *
     * @param log    the logger
     * @param params the constructor parameters record
     */
    public CoreQuestTypeHandler(final BetonQuestLogger log, final ConstructorParams params) {
        this.log = log;
        this.plugin = params.plugin();
        this.loggerFactory = params.loggerFactory();
        this.profileProvider = params.profileProvider();
        this.packManager = params.packageManager();
        this.server = plugin.getServer();
        this.bukkitScheduler = server.getScheduler();
        this.pluginManager = server.getPluginManager();
        this.configAccessorFactory = params.configAccessorFactory();
        this.config = params.config();
        this.languageProvider = params.languageProvider();
        this.saver = params.saver();
        this.fontRegistry = params.fontRegistry();
        this.allProcessors = new ArrayList<>();
        this.additionalProcessors = new ArrayList<>();
        this.coreComponentLoader = new DefaultCoreComponentLoader();
    }

    private void initCoreTypes() {
        coreComponentLoader.register(new IdentifiersComponent());
        coreComponentLoader.register(new ConditionsComponent());
        coreComponentLoader.register(new ActionsComponent());
        coreComponentLoader.register(new ObjectivesComponent());
        coreComponentLoader.register(new PlaceholdersComponent());
    }

    private void initFeatures() {
        coreComponentLoader.register(new TextParserComponent());
        coreComponentLoader.register(new PlayerDataStorageComponent());
        coreComponentLoader.register(new TextSectionParserComponent());
        coreComponentLoader.register(new SchedulesComponent());
        coreComponentLoader.register(new NotificationsComponent());
        coreComponentLoader.register(new JournalsComponent());
        coreComponentLoader.register(new PluginMessageComponent());
        coreComponentLoader.register(new ItemsComponent());
        coreComponentLoader.register(new CompassComponent());
        coreComponentLoader.register(new ConversationsComponent());
        coreComponentLoader.register(new NpcsComponent());
        coreComponentLoader.register(new CancelersComponent());
        coreComponentLoader.register(new RPGMenuComponent());
    }

    /**
     * Initializes the quest types in a strict order to maintain dependencies between them.
     */
    public void init() {
        coreComponentLoader.register(new ArgumentParsersComponent());
        coreComponentLoader.register(new InstructionsComponent());
        initCoreTypes();
        initFeatures();
        coreComponentLoader.register(new BetonQuestApiComponent());
        coreComponentLoader.load();
        coreComponentLoader.getAll(QuestProcessor.class).forEach(allProcessors::add);
    }

    /**
     * Clears all loaded data from all processors.
     */
    public void clear() {
        allProcessors.forEach(QuestProcessor::clear);
        additionalProcessors.forEach(QuestProcessor::clear);
    }

    /**
     * Lets all processors load their data from the given quest package.
     *
     * @param questPackage the quest package to load from
     */
    public void loadPackage(final QuestPackage questPackage) {
        allProcessors.forEach(processor -> processor.load(questPackage));
        additionalProcessors.forEach(processor -> processor.load(questPackage));
    }

    /**
     * Loads the processors with the given quest packages.
     * <br> <br>
     * Remove previously loaded data entirely using {@link #clear()}.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        Objects.requireNonNull(conversationProcessor, "Conversation processor must be initialized before loading data!");
        Objects.requireNonNull(scheduleProcessor, "Schedule processor must be initialized before loading data!");

        clear();

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            loadPackage(pack);
            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversationProcessor.checkExternalPointers();

        log.info("There are " + readableSize()
                + " (Additional: " + additionalProcessors.stream().map(QuestProcessor::readableSize).collect(Collectors.joining(", ")) + ")"
                + " loaded from " + packages.size() + " packages.");

        scheduleProcessor.startAll();
        additionalProcessors.forEach(questProcessor -> {
            if (questProcessor instanceof final StartTask startTask) {
                startTask.startAll();
            }
        });
    }

    /**
     * Gets the number of loaded Core Quest Types with their readable names.
     *
     * @return all loaded amounts
     */
    public String readableSize() {
        return allProcessors.stream().map(QuestProcessor::readableSize).collect(Collectors.joining(", "));
    }

    /**
     * Gets the all available metrics to provide to bStats.
     *
     * @return the metrics
     */
    public Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> metricsSupplier() {
        return allProcessors.stream()
                .filter(questProcessor -> questProcessor instanceof TypedQuestProcessor<?, ?>)
                .map(questProcessor -> (TypedQuestProcessor<?, ?>) questProcessor)
                .map(TypedQuestProcessor::metricsSupplier)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets a modifiable list of additional processors.
     * May be used to add more processors or remove some.
     *
     * @return all additional processors
     */
    public Collection<QuestProcessor<?, ?>> getAdditionalProcessors() {
        return additionalProcessors;
    }

    /**
     * Gets the BetonQuest API.
     *
     * @return the API
     */
    public BetonQuestApi getBetonQuestApi() {
        Objects.requireNonNull(betonQuestApi, "BetonQuest API is not initialized yet!");
        return betonQuestApi;
    }

    /**
     * Gets the action processor.
     *
     * @return the action processor
     */
    public ActionProcessor getActionProcessor() {
        Objects.requireNonNull(actionProcessor, "Action processor is not initialized yet!");
        return actionProcessor;
    }

    /**
     * Gets the condition processor.
     *
     * @return the condition processor
     */
    public ConditionProcessor getConditionProcessor() {
        Objects.requireNonNull(conditionProcessor, "Condition processor is not initialized yet!");
        return conditionProcessor;
    }

    /**
     * Gets the objective processor.
     *
     * @return the objective processor
     */
    public ObjectiveProcessor getObjectiveProcessor() {
        Objects.requireNonNull(objectiveProcessor, "Objective processor is not initialized yet!");
        return objectiveProcessor;
    }

    /**
     * Gets the placeholder processor.
     *
     * @return the placeholder processor
     */
    public PlaceholderProcessor getPlaceholderProcessor() {
        Objects.requireNonNull(placeholderProcessor, "Placeholder processor is not initialized yet!");
        return placeholderProcessor;
    }

    /**
     * Gets the canceler processor.
     *
     * @return the canceler processor
     */
    public CancelerProcessor getCancelerProcessor() {
        Objects.requireNonNull(cancelerProcessor, "Canceler processor is not initialized yet!");
        return cancelerProcessor;
    }

    /**
     * Gets the compass processor.
     *
     * @return the compass processor
     */
    public CompassProcessor getCompassProcessor() {
        Objects.requireNonNull(compassProcessor, "Compass processor is not initialized yet!");
        return compassProcessor;
    }

    /**
     * Gets the npc processor.
     *
     * @return the npc processor
     */
    public NpcProcessor getNpcProcessor() {
        Objects.requireNonNull(npcProcessor, "NPC processor is not initialized yet!");
        return npcProcessor;
    }

    /**
     * Gets the schedule processor.
     *
     * @return the schedule processor
     */
    public ActionScheduling getScheduleProcessor() {
        Objects.requireNonNull(scheduleProcessor, "Schedule processor is not initialized yet!");
        return scheduleProcessor;
    }

    /**
     * Gets the conversation processor.
     *
     * @return the conversation processor
     */
    public ConversationProcessor getConversationProcessor() {
        Objects.requireNonNull(conversationProcessor, "Conversation processor is not initialized yet!");
        return conversationProcessor;
    }

    /**
     * Gets the journal entry processor.
     *
     * @return the journal entry processor
     */
    public JournalEntryProcessor getJournalEntryProcessor() {
        Objects.requireNonNull(journalEntryProcessor, "Journal entry processor is not initialized yet!");
        return journalEntryProcessor;
    }

    /**
     * Gets the plugin message instance.
     *
     * @return the plugin message instance
     */
    public PluginMessage getPluginMessage() {
        Objects.requireNonNull(pluginMessage, "Plugin message is not initialized yet!");
        return pluginMessage;
    }

    /**
     * Gets the text parser.
     *
     * @return the text parser
     */
    public TextParser getTextParser() {
        Objects.requireNonNull(textParser, "Text parser is not initialized yet!");
        return textParser;
    }

    /**
     * Gets the text creator.
     *
     * @return the text creator
     */
    public ParsedSectionTextCreator getTextCreator() {
        Objects.requireNonNull(parsedSectionTextCreator, "Parsed section text creator is not initialized yet!");
        return parsedSectionTextCreator;
    }

    /**
     * Gets the player data factory.
     *
     * @return the player data factory
     */
    public PlayerDataFactory getPlayerDataFactory() {
        Objects.requireNonNull(playerDataFactory, "Player data factory is not initialized yet!");
        return playerDataFactory;
    }

    /**
     * Gets the player data storage.
     *
     * @return the player data storage
     */
    public PlayerDataStorage getPlayerDataStorage() {
        Objects.requireNonNull(playerDataStorage, "Player data storage is not initialized yet!");
        return playerDataStorage;
    }

    /**
     * Gets the conversation io registry.
     *
     * @return the conversation io registry
     */
    public ConversationIORegistry getConversationIORegistry() {
        Objects.requireNonNull(conversationIORegistry, "Conversation IO registry is not initialized yet!");
        return conversationIORegistry;
    }

    /**
     * Gets the interceptor registry.
     *
     * @return the interceptor registry
     */
    public InterceptorRegistry getInterceptorRegistry() {
        Objects.requireNonNull(interceptorRegistry, "Interceptor registry is not initialized yet!");
        return interceptorRegistry;
    }

    /**
     * Gets the notify io registry.
     *
     * @return the notify io registry
     */
    public NotifyIORegistry getNotifyIORegistry() {
        Objects.requireNonNull(notifyIORegistry, "Notify IO registry is not initialized yet!");
        return notifyIORegistry;
    }

    /**
     * Gets the schedule registry.
     *
     * @return the schedule registry
     */
    public ScheduleRegistry getScheduleRegistry() {
        Objects.requireNonNull(scheduleRegistry, "Schedule registry is not initialized yet!");
        return scheduleRegistry;
    }

    /**
     * Gets the item registry.
     *
     * @return the item registry
     */
    public ItemTypeRegistry getItemRegistry() {
        Objects.requireNonNull(itemRegistry, "Item registry is not initialized yet!");
        return itemRegistry;
    }

    /**
     * Gets the text parser registry.
     *
     * @return the text parser registry
     */
    public TextParserRegistry getTextParserRegistry() {
        Objects.requireNonNull(textParserRegistry, "Text parser registry is not initialized yet!");
        return textParserRegistry;
    }

    /**
     * Gets the rpg menu.
     *
     * @return the rpg menu
     */
    public RPGMenu getRpgMenu() {
        Objects.requireNonNull(rpgMenu, "RPG menu is not initialized yet!");
        return rpgMenu;
    }

    /**
     * Gets the conditions feature. <br>
     * The {@link Conditions} combine the registry with the processor
     * to provide access to all features related to conditions.
     *
     * @return the conditions feature
     */
    private Conditions getConditions() {
        Objects.requireNonNull(this.conditionTypeRegistry, "Condition type registry is not initialized yet!");
        Objects.requireNonNull(this.conditionProcessor, "Condition processor is not initialized yet!");
        return new DefaultConditions(this.conditionProcessor, this.conditionTypeRegistry);
    }

    /**
     * Gets the actions feature. <br>
     * The {@link Actions} combine the registry with the processor
     * to provide access to all features related to actions.
     *
     * @return the actions feature
     */
    private Actions getActions() {
        Objects.requireNonNull(this.actionTypeRegistry, "Action type registry is not initialized yet!");
        Objects.requireNonNull(this.actionProcessor, "Action processor is not initialized yet!");
        return new DefaultActions(this.actionProcessor, this.actionTypeRegistry);
    }

    /**
     * Gets the objectives feature. <br>
     * The {@link Objectives} combine the registry with the processor
     * to provide access to all features related to objectives.
     *
     * @return the objectives feature
     */
    private Objectives getObjectives() {
        Objects.requireNonNull(this.objectiveTypeRegistry, "Objective type registry is not initialized yet!");
        Objects.requireNonNull(this.objectiveProcessor, "Objective processor is not initialized yet!");
        return new DefaultObjectives(objectiveProcessor, objectiveTypeRegistry);
    }

    /**
     * Gets the placeholder feature. <br>
     * The {@link Placeholders} combine the registry with the processor
     * to provide access to all features related to placeholders.
     *
     * @return the placeholder feature
     */
    private Placeholders getPlaceholders() {
        Objects.requireNonNull(this.placeholderTypeRegistry, "Placeholder type registry is not initialized yet!");
        Objects.requireNonNull(this.placeholderProcessor, "Placeholder processor is not initialized yet!");
        return new DefaultPlaceholders(placeholderProcessor, placeholderTypeRegistry);
    }

    /**
     * Gets the item feature. <br>
     * The {@link Items} combine the registry with the processor
     * to provide access to all features related to items.
     *
     * @return the item feature
     */
    private Items getItems() {
        Objects.requireNonNull(this.itemProcessor, "Item processor is not initialized yet!");
        Objects.requireNonNull(this.itemRegistry, "Item registry is not initialized yet!");
        return new DefaultItems(itemProcessor, itemRegistry);
    }

    /**
     * Gets the compass feature. <br>
     * The {@link Npcs} combine the registry with the processor
     * to provide access to all features related to compasses.
     *
     * @return the compass feature
     */
    private Npcs getNpcs() {
        Objects.requireNonNull(this.npcProcessor, "Npc processor is not initialized yet!");
        Objects.requireNonNull(this.npcRegistry, "Npc registry is not initialized yet!");
        return new DefaultNpcs(npcProcessor, npcRegistry);
    }

    /**
     * The default {@link CoreQuestTypeHandler} constructor parameters record.
     *
     * @param plugin                the plugin instance
     * @param loggerFactory         the logger factory to create loggers for each individual type
     * @param profileProvider       the profile provider offering access to player profiles
     * @param configAccessorFactory the config accessor factory handles configuration
     * @param packageManager        the quest package manager to access quest packages from
     * @param config                the config accessor of the BetonQuest config
     * @param languageProvider      the language provider
     * @param saver                 the database saver
     * @param fontRegistry          the font registry
     */
    public record ConstructorParams(Plugin plugin, BetonQuestLoggerFactory loggerFactory,
                                    ProfileProvider profileProvider, ConfigAccessorFactory configAccessorFactory,
                                    QuestPackageManager packageManager, ConfigAccessor config,
                                    LanguageProvider languageProvider, Saver saver, FontRegistry fontRegistry) {

    }
}
