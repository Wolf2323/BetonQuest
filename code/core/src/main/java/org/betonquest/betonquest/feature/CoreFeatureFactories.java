package org.betonquest.betonquest.feature;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.interceptor.NonInterceptingInterceptorFactory;
import org.betonquest.betonquest.conversation.interceptor.SimpleInterceptorFactory;
import org.betonquest.betonquest.conversation.io.InventoryConvIOFactory;
import org.betonquest.betonquest.conversation.io.SimpleConvIOFactory;
import org.betonquest.betonquest.conversation.io.SlowTellrawConvIOFactory;
import org.betonquest.betonquest.conversation.io.TellrawConvIOFactory;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemSerializer;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.notify.SuppressNotifyIOFactory;
import org.betonquest.betonquest.notify.io.ActionBarNotifyIOFactory;
import org.betonquest.betonquest.notify.io.AdvancementNotifyIOFactory;
import org.betonquest.betonquest.notify.io.BossBarNotifyIOFactory;
import org.betonquest.betonquest.notify.io.ChatNotifyIOFactory;
import org.betonquest.betonquest.notify.io.SoundIOFactory;
import org.betonquest.betonquest.notify.io.SubTitleNotifyIOFactory;
import org.betonquest.betonquest.notify.io.TitleNotifyIOFactory;
import org.betonquest.betonquest.notify.io.TotemNotifyIOFactory;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduleFactory;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduler;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduleFactory;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduler;
import org.betonquest.betonquest.text.parser.LegacyParser;
import org.betonquest.betonquest.text.parser.MineDownParser;
import org.betonquest.betonquest.text.parser.MiniMessageParser;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Registers the stuff that is not built from Instructions.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class CoreFeatureFactories {

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Cache to catch up missed schedulers.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * The {@link PlaceholderManager} to create and resolve placeholders.
     */
    private final PlaceholderManager placeholders;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The plugin manager instance.
     */
    private final PluginManager pluginManager;

    /**
     * The item manager instance.
     */
    private final ItemManager itemManager;

    /**
     * The profile provider to use.
     */
    private final ProfileProvider profileProvider;

    /**
     * The conversation api.
     */
    private final Conversations conversations;

    /**
     * The Config.
     */
    private final ConfigAccessor config;

    /**
     * The colors to use for the conversation.
     */
    private final ConversationColors colors;

    /**
     * The instructions instance to use.
     */
    private final Instructions instructions;

    /**
     * The action manager handling actions.
     */
    private final ActionManager actionManager;

    /**
     * The message parser to use for parsing messages.
     */
    private final TextParser textParser;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create a new Core Other Factories class for registering.
     *
     * @param loggerFactory      the factory to create new class specific loggers
     * @param packManager        the quest package manager to get quest packages from
     * @param lastExecutionCache the cache to catch up missed schedulers
     * @param plugin             the plugin instance
     * @param profileProvider    the profile provider to use
     * @param placeholders       the {@link PlaceholderManager} to create and resolve placeholders
     * @param conversations      the conversation api
     * @param config             the config
     * @param colors             the colors to use for the conversation
     * @param instructions       the instructions instance to use
     * @param actionManager      the action manager handling actions
     * @param pluginManager      the plugin manager instance
     * @param itemManager        the item manager instance
     * @param textParser         the text parser to use for parsing text
     * @param fontRegistry       the font registry to use for the conversation
     * @param pluginMessage      the {@link PluginMessage} instance
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CoreFeatureFactories(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                final LastExecutionCache lastExecutionCache, final Plugin plugin, final ProfileProvider profileProvider,
                                final PlaceholderManager placeholders, final Conversations conversations,
                                final ConfigAccessor config, final ConversationColors colors, final Instructions instructions,
                                final ActionManager actionManager, final PluginManager pluginManager, final ItemManager itemManager,
                                final TextParser textParser, final FontRegistry fontRegistry, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
        this.lastExecutionCache = lastExecutionCache;
        this.plugin = plugin;
        this.profileProvider = profileProvider;
        this.placeholders = placeholders;
        this.conversations = conversations;
        this.config = config;
        this.colors = colors;
        this.instructions = instructions;
        this.actionManager = actionManager;
        this.pluginManager = pluginManager;
        this.itemManager = itemManager;
        this.textParser = textParser;
        this.fontRegistry = fontRegistry;
        this.pluginMessage = pluginMessage;
    }

    /**
     * Register all the factories.
     *
     * @param conversationIORegistry the conversation io registry
     * @param interceptorRegistry    the interceptor registry
     * @param itemRegistry           the item registry
     * @param notifyIORegistry       the notify io registry
     * @param scheduleRegistry       the schedule registry
     * @param textParserRegistry     the text parser registry
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void register(final ConversationIORegistry conversationIORegistry, final InterceptorRegistry interceptorRegistry,
                         final ItemRegistry itemRegistry, final NotifyIORegistry notifyIORegistry, final ScheduleRegistry scheduleRegistry,
                         final TextParserRegistry textParserRegistry) {
        conversationIORegistry.register("simple", new SimpleConvIOFactory(colors));
        conversationIORegistry.register("tellraw", new TellrawConvIOFactory(colors));
        final InventoryConvIOFactory.ConstructorParameters inventoryConvParams = new InventoryConvIOFactory.ConstructorParameters(
                loggerFactory, config, fontRegistry, colors, plugin, pluginManager, pluginMessage, instructions, conversations, itemManager, profileProvider);
        conversationIORegistry.register("chest", new InventoryConvIOFactory(inventoryConvParams, false));
        conversationIORegistry.register("combined", new InventoryConvIOFactory(inventoryConvParams, true));
        conversationIORegistry.register("slowtellraw", new SlowTellrawConvIOFactory(fontRegistry, colors));

        interceptorRegistry.register("simple", new SimpleInterceptorFactory());
        interceptorRegistry.register("none", new NonInterceptingInterceptorFactory());

        final BookPageWrapper bookPageWrapper = new BookPageWrapper(fontRegistry, 114, 14);
        itemRegistry.register("simple", new SimpleQuestItemFactory(placeholders, packManager, textParser, bookPageWrapper,
                () -> config.getBoolean("item.quest.lore") ? pluginMessage : null));
        itemRegistry.registerSerializer("simple", new SimpleQuestItemSerializer(textParser, bookPageWrapper));

        final Plugin plugin = BetonQuest.getInstance();
        notifyIORegistry.register("suppress", new SuppressNotifyIOFactory());
        notifyIORegistry.register("chat", new ChatNotifyIOFactory(placeholders, conversations));
        notifyIORegistry.register("advancement", new AdvancementNotifyIOFactory(placeholders, plugin));
        notifyIORegistry.register("actionbar", new ActionBarNotifyIOFactory(placeholders));
        notifyIORegistry.register("bossbar", new BossBarNotifyIOFactory(placeholders, plugin));
        notifyIORegistry.register("title", new TitleNotifyIOFactory(placeholders));
        notifyIORegistry.register("totem", new TotemNotifyIOFactory(placeholders));
        notifyIORegistry.register("subtitle", new SubTitleNotifyIOFactory(placeholders));
        notifyIORegistry.register("sound", new SoundIOFactory(placeholders));

        scheduleRegistry.register("realtime-daily", new RealtimeDailyScheduleFactory(),
                new RealtimeDailyScheduler(loggerFactory.create(RealtimeDailyScheduler.class, "Schedules"), actionManager, lastExecutionCache)
        );
        scheduleRegistry.register("realtime-cron", new RealtimeCronScheduleFactory(),
                new RealtimeCronScheduler(loggerFactory.create(RealtimeCronScheduler.class, "Schedules"), actionManager, lastExecutionCache)
        );

        registerTextParsers(textParserRegistry);
    }

    private void registerTextParsers(final TextParserRegistry textParserRegistry) {
        final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .extractUrls()
                .build();
        textParserRegistry.register("legacy", new LegacyParser(legacySerializer));
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        textParserRegistry.register("minimessage", new MiniMessageParser(miniMessage));
        final MiniMessage legacyMiniMessage = MiniMessage.builder()
                .preProcessor(input -> {
                    final TextComponent deserialize = legacySerializer.deserialize(ChatColor.translateAlternateColorCodes('&', input.replaceAll("(?<!\\\\)\\\\n", "\n")));
                    final String serialize = miniMessage.serialize(deserialize);
                    return serialize.replaceAll("\\\\<", "<");
                })
                .build();
        textParserRegistry.register("legacyminimessage", new MiniMessageParser(legacyMiniMessage));
        textParserRegistry.register("minedown", new MineDownParser());
    }
}
