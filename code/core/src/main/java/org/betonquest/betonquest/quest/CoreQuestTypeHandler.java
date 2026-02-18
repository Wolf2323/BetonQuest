package org.betonquest.betonquest.quest;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.service.DefaultObjectiveServiceProvider;
import org.betonquest.betonquest.api.service.DefaultBetonQuestApi;
import org.betonquest.betonquest.api.service.DefaultInstructions;
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
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.id.action.ActionIdentifierFactory;
import org.betonquest.betonquest.id.cancel.QuestCancelerIdentifierFactory;
import org.betonquest.betonquest.id.compass.CompassIdentifierFactory;
import org.betonquest.betonquest.id.condition.ConditionIdentifierFactory;
import org.betonquest.betonquest.id.conversation.ConversationIdentifierFactory;
import org.betonquest.betonquest.id.conversation.ConversationOptionIdentifierFactory;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalEntryIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalMainPageIdentifierFactory;
import org.betonquest.betonquest.id.menu.MenuIdentifierFactory;
import org.betonquest.betonquest.id.menu.MenuItemIdentifierFactory;
import org.betonquest.betonquest.id.npc.NpcIdentifierFactory;
import org.betonquest.betonquest.id.objective.ObjectiveIdentifierFactory;
import org.betonquest.betonquest.id.placeholder.PlaceholderIdentifierFactory;
import org.betonquest.betonquest.id.schedule.ScheduleIdentifierFactory;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
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
import org.betonquest.betonquest.text.DecidingTextParser;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.betonquest.betonquest.text.TagTextParserDecider;
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
public class CoreQuestTypeHandler {

    /**
     * The plugin instance primarily used to handle scheduling and bukkit events.
     */
    private final Plugin plugin;

    /**
     * The logger used for the {@link CoreQuestTypeHandler}.
     */
    private final BetonQuestLogger logger;

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
     * @param plugin                the plugin instance
     * @param logger                the logger
     * @param loggerFactory         the logger factory
     * @param profileProvider       the profile provider
     * @param packManager           the quest package manager
     * @param bukkitScheduler       the scheduler
     * @param server                the server instance
     * @param pluginManager         the plugin manager
     * @param configAccessorFactory the config accessor factory
     * @param languageProvider      the language provider
     * @param saver                 the database saver
     * @param fontRegistry          the font registry
     * @param config                the config accessor of the BetonQuest config
     */
    public CoreQuestTypeHandler(final Plugin plugin, final BetonQuestLogger logger, final BetonQuestLoggerFactory loggerFactory,
                                final ProfileProvider profileProvider, final QuestPackageManager packManager,
                                final BukkitScheduler bukkitScheduler, final Server server, final PluginManager pluginManager,
                                final ConfigAccessorFactory configAccessorFactory, final ConfigAccessor config,
                                final LanguageProvider languageProvider, final Saver saver, final FontRegistry fontRegistry) {
        this.plugin = plugin;
        this.logger = logger;
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
        this.packManager = packManager;
        this.bukkitScheduler = bukkitScheduler;
        this.server = server;
        this.pluginManager = pluginManager;
        this.configAccessorFactory = configAccessorFactory;
        this.config = config;
        this.languageProvider = languageProvider;
        this.saver = saver;
        this.fontRegistry = fontRegistry;
        this.allProcessors = new ArrayList<>();
    }

    private void initIdentifiers() {
        identifierTypeRegistry = new IdentifierTypeRegistry(loggerFactory.create(IdentifierTypeRegistry.class));
    }

    private void initConditions() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before conditions!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before conditions!");
        final ConditionIdentifierFactory conditionIdentifierFactory = new ConditionIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ConditionIdentifier.class, conditionIdentifierFactory);
        this.conditionTypeRegistry = new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class));
        this.conditionProcessor = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class),
                conditionTypeRegistry, bukkitScheduler, conditionIdentifierFactory, plugin, instructions);
        this.allProcessors.add(conditionProcessor);
    }

    private void initActions() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before actions!");
        Objects.requireNonNull(this.conditionProcessor, "Condition processor must be initialized before actions!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before actions!");
        final ActionIdentifierFactory actionIdentifierFactory = new ActionIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ActionIdentifier.class, actionIdentifierFactory);
        this.actionTypeRegistry = new ActionTypeRegistry(loggerFactory.create(ActionTypeRegistry.class), loggerFactory, conditionProcessor);
        this.actionProcessor = new ActionProcessor(loggerFactory.create(ActionProcessor.class),
                actionIdentifierFactory, actionTypeRegistry, bukkitScheduler, instructions, plugin);
        this.allProcessors.add(actionProcessor);
    }

    private void initObjectives() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before objectives!");
        Objects.requireNonNull(this.conditionProcessor, "Condition processor must be initialized before objectives!");
        Objects.requireNonNull(this.actionProcessor, "Action processor must be initialized before objectives!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before objectives!");
        final DefaultObjectiveServiceProvider objectiveServiceProvider = new DefaultObjectiveServiceProvider(plugin, conditionProcessor, actionProcessor,
                loggerFactory, profileProvider, instructions);
        final ObjectiveIdentifierFactory objectiveIdentifierFactory = new ObjectiveIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ObjectiveIdentifier.class, objectiveIdentifierFactory);
        this.objectiveTypeRegistry = new ObjectiveTypeRegistry(loggerFactory.create(ObjectiveTypeRegistry.class));
        this.objectiveProcessor = new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class),
                objectiveTypeRegistry, objectiveIdentifierFactory, pluginManager,
                objectiveServiceProvider, instructions, plugin);
        this.allProcessors.add(objectiveProcessor);
    }

    private void initPlaceholders() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before placeholders!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before placeholders!");
        final PlaceholderIdentifierFactory placeholderIdentifierFactory = new PlaceholderIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(PlaceholderIdentifier.class, placeholderIdentifierFactory);
        this.placeholderTypeRegistry = new PlaceholderTypeRegistry(loggerFactory.create(PlaceholderTypeRegistry.class));
        this.placeholderProcessor = new PlaceholderProcessor(loggerFactory.create(PlaceholderProcessor.class),
                packManager, placeholderTypeRegistry, bukkitScheduler, placeholderIdentifierFactory, instructions, plugin);
        this.allProcessors.add(placeholderProcessor);
    }

    private void initConversations() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before conversations!");
        Objects.requireNonNull(this.parsedSectionTextCreator, "Parsed section text creator must be initialized before conversations!");
        Objects.requireNonNull(this.placeholderProcessor, "Placeholder processor must be initialized before conversations!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before conversations!");
        Objects.requireNonNull(this.pluginMessage, "Plugin message must be initialized before conversations!");
        Objects.requireNonNull(this.actionProcessor, "Action processor must be initialized before conversations!");
        Objects.requireNonNull(this.conditionProcessor, "Condition processor must be initialized before conversations!");
        this.conversationIdentifierFactory = new ConversationIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ConversationIdentifier.class, conversationIdentifierFactory);
        final ConversationOptionIdentifierFactory conversationOptionIdentifierFactory = new ConversationOptionIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ConversationOptionIdentifier.class, conversationOptionIdentifierFactory);
        this.conversationIORegistry = new ConversationIORegistry(loggerFactory.create(ConversationIORegistry.class));
        this.interceptorRegistry = new InterceptorRegistry(loggerFactory.create(InterceptorRegistry.class));
        this.conversationProcessor = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class),
                loggerFactory, plugin, parsedSectionTextCreator, packManager, placeholderProcessor, profileProvider, config, conversationIORegistry, interceptorRegistry,
                instructions, pluginMessage, actionProcessor, conditionProcessor, conversationIdentifierFactory);
    }

    private void initNpcs() throws QuestException {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before npcs!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before npcs!");
        Objects.requireNonNull(this.actionProcessor, "Action processor must be initialized before npcs!");
        Objects.requireNonNull(this.conditionProcessor, "Condition processor must be initialized before npcs!");
        Objects.requireNonNull(this.conversationProcessor, "Conversation processor must be initialized before npcs!");
        Objects.requireNonNull(this.conversationIdentifierFactory, "Conversation identifier factory must be initialized before npcs!");
        Objects.requireNonNull(this.pluginMessage, "Plugin message must be initialized before npcs!");
        final NpcIdentifierFactory npcIdentifierFactory = new NpcIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(NpcIdentifier.class, npcIdentifierFactory);
        this.npcRegistry = new NpcTypeRegistry(loggerFactory.create(NpcTypeRegistry.class), instructions);
        this.npcProcessor = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory, plugin,
                npcIdentifierFactory, conversationIdentifierFactory, npcRegistry, pluginMessage,
                profileProvider, actionProcessor, conditionProcessor, conversationProcessor.getStarter(), instructions,
                identifierTypeRegistry, config);
    }

    private void initItems() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before items!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before items!");
        final ItemIdentifierFactory itemIdentifierFactory = new ItemIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ItemIdentifier.class, itemIdentifierFactory);
        this.itemRegistry = new ItemTypeRegistry(loggerFactory.create(ItemTypeRegistry.class));
        this.itemProcessor = new ItemProcessor(loggerFactory.create(ItemProcessor.class),
                itemIdentifierFactory, itemRegistry, instructions);
    }

    private void initCompasses() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before compasses!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before compasses!");
        Objects.requireNonNull(this.parsedSectionTextCreator, "Parsed section text creator must be initialized before compasses!");
        final CompassIdentifierFactory compassIdentifierFactory = new CompassIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(CompassIdentifier.class, compassIdentifierFactory);
        this.compassProcessor = new CompassProcessor(loggerFactory.create(CompassProcessor.class),
                instructions, parsedSectionTextCreator, compassIdentifierFactory);
    }

    private void initJournalEntries() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before journal entries!");
        Objects.requireNonNull(this.parsedSectionTextCreator, "Parsed section text creator must be initialized before journal entries!");
        final JournalEntryIdentifierFactory journalEntryIdentifierFactory = new JournalEntryIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(JournalEntryIdentifier.class, journalEntryIdentifierFactory);
        this.journalEntryProcessor = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class),
                journalEntryIdentifierFactory, parsedSectionTextCreator);
    }

    private void initJournalMainPages() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before journal main pages!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before journal main pages!");
        Objects.requireNonNull(this.parsedSectionTextCreator, "Parsed section text creator must be initialized before journal main pages!");
        final JournalMainPageIdentifierFactory journalMainPageIdentifierFactory = new JournalMainPageIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(JournalMainPageIdentifier.class, journalMainPageIdentifierFactory);
        this.journalMainPageProcessor = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class),
                instructions, parsedSectionTextCreator, journalMainPageIdentifierFactory);
    }

    private void initSchedules() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before schedules!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before schedules!");
        final ScheduleIdentifierFactory scheduleIdentifierFactory = new ScheduleIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(ScheduleIdentifier.class, scheduleIdentifierFactory);
        this.scheduleRegistry = new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class));
        this.scheduleProcessor = new ActionScheduling(loggerFactory.create(ActionScheduling.class, "Schedules"),
                instructions, scheduleRegistry, scheduleIdentifierFactory);
    }

    private void initNotifyIOs() {
        this.notifyIORegistry = new NotifyIORegistry(loggerFactory.create(NotifyIORegistry.class));
    }

    private void initTextParser() {
        this.textParserRegistry = new TextParserRegistryImpl(loggerFactory.create(TextParserRegistryImpl.class));
        final String defaultParser = config.getString("text_parser", "legacyminimessage");
        this.textParser = new DecidingTextParser(textParserRegistry, new TagTextParserDecider(defaultParser));
    }

    private void initTextSectionParser() {
        Objects.requireNonNull(this.textParser, "Text parser must be initialized before text section parser!");
        Objects.requireNonNull(this.playerDataStorage, "Player data storage must be initialized before text section parser!");
        Objects.requireNonNull(this.placeholderProcessor, "Placeholder processor must be initialized before text section parser!");
        this.parsedSectionTextCreator = new ParsedSectionTextCreator(textParser, playerDataStorage, languageProvider, placeholderProcessor);
    }

    private void initPlayerDataStorage() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before player data storage!");
        Objects.requireNonNull(this.objectiveProcessor, "Objective processor must be initialized before player data storage!");
        final PlayerDataFactory playerDataFactory = new PlayerDataFactory(loggerFactory, saver, server,
                identifierTypeRegistry, objectiveProcessor, Suppliers.memoize(() -> {
            Objects.requireNonNull(this.pluginMessage, "Plugin message must be initialized before player data storage!");
            Objects.requireNonNull(this.conditionProcessor, "Condition processor must be initialized before player data storage!");
            Objects.requireNonNull(this.journalMainPageProcessor, "Journal main page processor must be initialized before player data storage!");
            Objects.requireNonNull(this.journalEntryProcessor, "Journal entry processor must be initialized before player data storage!");
            Objects.requireNonNull(this.textParser, "Text parser must be initialized before player data storage!");
            return new JournalFactory(loggerFactory, pluginMessage, conditionProcessor, journalMainPageProcessor, journalEntryProcessor, config, textParser, fontRegistry);
        }));
        playerDataStorage = new PlayerDataStorage(loggerFactory.create(PlayerDataStorage.class), config,
                playerDataFactory, objectiveProcessor, profileProvider);
    }

    private void initPluginMessage() throws QuestException {
        Objects.requireNonNull(this.placeholderProcessor, "Placeholder processor must be initialized before plugin message!");
        Objects.requireNonNull(this.playerDataStorage, "Player data storage must be initialized before plugin message!");
        Objects.requireNonNull(this.textParser, "Text parser must be initialized before plugin message!");
        pluginMessage = new PluginMessage(loggerFactory.create(PluginMessage.class), plugin, placeholderProcessor,
                playerDataStorage, textParser, configAccessorFactory, languageProvider);
    }

    private void initCancelers() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before cancelers!");
        Objects.requireNonNull(this.pluginMessage, "Plugin message must be initialized before cancelers!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before cancelers!");
        Objects.requireNonNull(this.actionProcessor, "Action processor must be initialized before cancelers!");
        Objects.requireNonNull(this.conditionProcessor, "Condition processor must be initialized before cancelers!");
        Objects.requireNonNull(this.parsedSectionTextCreator, "Parsed section text creator must be initialized before cancelers!");
        Objects.requireNonNull(this.objectiveProcessor, "Objective processor must be initialized before cancelers!");
        Objects.requireNonNull(this.itemProcessor, "Item processor must be initialized before cancelers!");
        Objects.requireNonNull(this.playerDataStorage, "Player data storage must be initialized before cancelers!");
        final QuestCancelerIdentifierFactory questCancelerIdentifierFactory = new QuestCancelerIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(QuestCancelerIdentifier.class, questCancelerIdentifierFactory);
        this.cancelerProcessor = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class),
                loggerFactory, pluginMessage, instructions, actionProcessor, conditionProcessor,
                objectiveProcessor, itemProcessor, parsedSectionTextCreator, playerDataStorage, questCancelerIdentifierFactory);
    }

    private void initRPGMenu() {
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before RPG menu!");
        Objects.requireNonNull(this.pluginMessage, "Plugin message must be initialized before RPG menu!");
        Objects.requireNonNull(this.parsedSectionTextCreator, "Parsed section text creator must be initialized before RPG menu!");
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before RPG menu!");
        Objects.requireNonNull(this.argumentParsers, "Argument parsers must be initialized before RPG menu!");
        final MenuIdentifierFactory menuIdentifierFactory = new MenuIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(MenuIdentifier.class, menuIdentifierFactory);
        final MenuItemIdentifierFactory menuItemIdentifierFactory = new MenuItemIdentifierFactory(packManager);
        this.identifierTypeRegistry.register(MenuItemIdentifier.class, menuItemIdentifierFactory);
        this.rpgMenu = new RPGMenu(loggerFactory.create(RPGMenu.class), loggerFactory, instructions, config,
                pluginMessage, parsedSectionTextCreator, profileProvider, this.argumentParsers.get(),
                menuIdentifierFactory, menuItemIdentifierFactory);
    }

    private void initInstructions() {
        Objects.requireNonNull(this.argumentParsers, "Argument parsers must be initialized before instructions!");
        this.instructions = new DefaultInstructions(() -> placeholderProcessor, () -> packManager, argumentParsers, () -> loggerFactory);
    }

    private void initArguments() {
        this.argumentParsers = () -> {
            Objects.requireNonNull(this.itemProcessor, "Item processor must be initialized before arguments!");
            Objects.requireNonNull(this.itemIdentifierFactory, "Item identifier factory must be initialized before arguments!");
            Objects.requireNonNull(this.textParser, "Text parser must be initialized before arguments!");
            Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before arguments!");
            return new DefaultArgumentParsers(itemProcessor, itemIdentifierFactory, textParser, server, identifierTypeRegistry);
        };
    }

    private void initBetonQuestApi() {
        Objects.requireNonNull(this.instructions, "Instructions must be initialized before BetonQuest API!");
        Objects.requireNonNull(this.conversationProcessor, "Conversation processor must be initialized before BetonQuest API!");
        Objects.requireNonNull(this.identifierTypeRegistry, "Identifier registry must be initialized before BetonQuest API!");
        this.betonQuestApi = new DefaultBetonQuestApi(profileProvider, packManager, loggerFactory, instructions,
                getActions(), getConditions(), getObjectives(), getPlaceholders(), getItems(), getNpcs(),
                conversationProcessor, identifierTypeRegistry);
    }

    private void initCoreTypes() {
        initIdentifiers();
        initConditions();
        initActions();
        initObjectives();
        initPlaceholders();
    }

    private void initFeatures() throws QuestException {
        initSchedules();
        initNotifyIOs();
        initJournalEntries();
        initJournalMainPages();
        initPluginMessage();
        initTextParser();
        initPlayerDataStorage();
        initTextSectionParser();
        initItems();
        initCompasses();
        initConversations();
        initNpcs();
        initCancelers();
        initRPGMenu();
    }

    /**
     * Initializes the quest types in a strict order to maintain dependencies between them.
     */
    public void init() {
        initArguments();
        initInstructions();
        initCoreTypes();
        try {
            initFeatures();
        } catch (final QuestException e) {
            logger.error("Failed to initialize features!", e);
        }
    }

    /**
     * Clears all loaded data from all processors.
     */
    public void clear() {
        allProcessors.forEach(QuestProcessor::clear);
    }

    /**
     * Lets all processors load their data from the given quest package.
     *
     * @param questPackage the quest package to load from
     */
    public void load(final QuestPackage questPackage) {
        allProcessors.forEach(processor -> processor.load(questPackage));
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
     * Gets the BetonQuest API.
     *
     * @return the API
     */
    public BetonQuestApi getBetonQuestApi() {
        Objects.requireNonNull(betonQuestApi, "BetonQuest API is not initialized yet!");
        return betonQuestApi;
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
}
