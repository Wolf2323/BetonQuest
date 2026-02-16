package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.legacy.LegacyFeatures;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.api.service.Instructions;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.registry.feature.BaseFeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.betonquest.betonquest.schedule.ActionScheduling;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stores the active Processors to store and execute type logic.
 *
 * @param log              The custom {@link BetonQuestLogger} instance for this class.
 * @param core             The core quest type processors.
 * @param actionScheduling Action scheduling module.
 * @param cancelers        Quest Canceler logic.
 * @param compasses        Compasses.
 * @param conversations    Conversation Data logic.
 * @param items            Quest Item logic.
 * @param journalEntries   Journal Entries.
 * @param journalMainPages Journal Main Pages.
 * @param npcs             Npc getting.
 * @param additional       Additional quest processors.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public record QuestRegistry(
        BetonQuestLogger log,
        CoreQuestRegistry core,
        ActionScheduling actionScheduling,
        CancelerProcessor cancelers,
        CompassProcessor compasses,
        ConversationProcessor conversations,
        ItemProcessor items,
        JournalEntryProcessor journalEntries,
        JournalMainPageProcessor journalMainPages,
        NpcProcessor npcs,
        List<QuestProcessor<?, ?>> additional
) implements LegacyFeatures {

    /**
     * Create a new Registry for storing and using Processors.
     *
     * @param log               the custom logger for this registry
     * @param loggerFactory     the logger factory used for new custom logger instances
     * @param plugin            the plugin used to create new conversation data
     * @param coreQuestRegistry the core quest type processors
     * @param otherRegistries   the available other types
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param textCreator       the text creator to parse text
     * @param profileProvider   the profile provider instance
     * @param playerDataStorage the storage to get player data
     * @param identifiers       the available identifiers
     * @param instructions      the betonquest instructions instance
     * @return the newly created QuestRegistry
     * @throws QuestException if identifier factories are not registered
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static QuestRegistry create(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuest plugin, final CoreQuestRegistry coreQuestRegistry,
                                       final BaseFeatureRegistries otherRegistries, final PluginMessage pluginMessage,
                                       final ParsedSectionTextCreator textCreator, final ProfileProvider profileProvider,
                                       final PlayerDataStorage playerDataStorage, final IdentifierTypeRegistry identifiers,
                                       final Instructions instructions) throws QuestException {

        final ItemProcessor items = new ItemProcessor(loggerFactory.create(ItemProcessor.class),
                identifiers.getFactory(ItemIdentifier.class), otherRegistries.item(), instructions);
        final ActionScheduling actionScheduling = new ActionScheduling(
                loggerFactory.create(ActionScheduling.class, "Schedules"), instructions,
                otherRegistries.actionScheduling(), identifiers.getFactory(ScheduleIdentifier.class));
        final CancelerProcessor cancelers = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class),
                loggerFactory, pluginMessage, instructions, coreQuestRegistry.actions(), coreQuestRegistry.conditions(),
                coreQuestRegistry.objectives(), items, textCreator, playerDataStorage, identifiers.getFactory(QuestCancelerIdentifier.class));
        final CompassProcessor compasses = new CompassProcessor(loggerFactory.create(CompassProcessor.class),
                instructions, textCreator, identifiers.getFactory(CompassIdentifier.class));
        final ConversationProcessor conversations = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class),
                loggerFactory, plugin, textCreator, otherRegistries.conversationIO(), otherRegistries.interceptor(),
                instructions, pluginMessage, coreQuestRegistry.actions(), coreQuestRegistry.conditions(), identifiers.getFactory(ConversationIdentifier.class));
        final NpcProcessor npcs = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory,
                identifiers.getFactory(NpcIdentifier.class), identifiers.getFactory(ConversationIdentifier.class), otherRegistries.npc(), pluginMessage,
                plugin, profileProvider, coreQuestRegistry.actions(), coreQuestRegistry.conditions(), conversations.getStarter(), instructions);
        final JournalEntryProcessor journalEntries = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class),
                identifiers.getFactory(JournalEntryIdentifier.class), textCreator);
        final JournalMainPageProcessor journalMainPages = new JournalMainPageProcessor(
                loggerFactory.create(JournalMainPageProcessor.class), instructions, textCreator,
                identifiers.getFactory(JournalMainPageIdentifier.class));
        return new QuestRegistry(log, coreQuestRegistry, actionScheduling, cancelers, compasses, conversations,
                items, journalEntries, journalMainPages, npcs, new ArrayList<>());
    }

    /**
     * Loads the Processors with the QuestPackages.
     * <p>
     * Removes previous data and loads the given QuestPackages.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        actionScheduling.clear();
        core.clear();
        cancelers.clear();
        conversations.clear();
        compasses.clear();
        items.clear();
        journalEntries.clear();
        journalMainPages.clear();
        npcs.clear();
        additional.forEach(QuestProcessor::clear);

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            cancelers.load(pack);
            core.load(pack);
            compasses.load(pack);
            conversations.load(pack);
            items.load(pack);
            journalEntries.load(pack);
            journalMainPages.load(pack);
            npcs.load(pack);
            actionScheduling.load(pack);
            additional.forEach(questProcessor -> questProcessor.load(pack));

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversations.checkExternalPointers();

        log.info("There are " + String.join(", ", core.readableSize(),
                cancelers.readableSize(), compasses.readableSize(), conversations.readableSize(), items.readableSize(),
                journalEntries.readableSize(), journalMainPages.readableSize(), npcs.readableSize())
                + " (Additional: " + additional.stream().map(QuestProcessor::readableSize).collect(Collectors.joining(", ")) + ")"
                + " loaded from " + packages.size() + " packages.");

        actionScheduling.startAll();
        additional.forEach(questProcessor -> {
            if (questProcessor instanceof final StartTask startTask) {
                startTask.startAll();
            }
        });
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return available instruction metrics
     */
    public Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> metricsSupplier() {
        final Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> map = new HashMap<>(core.metricsSupplier());
        map.putAll(Map.ofEntries(
                items.metricsSupplier(),
                npcs.metricsSupplier())
        );
        return map;
    }

    @Override
    public Map<QuestCancelerIdentifier, QuestCanceler> getCancelers() {
        return new HashMap<>(cancelers().getValues());
    }

    @Override
    public QuestCanceler getCanceler(final QuestCancelerIdentifier cancelerID) throws QuestException {
        return cancelers().get(cancelerID);
    }

    @Override
    public Map<CompassIdentifier, QuestCompass> getCompasses() {
        return new HashMap<>(compasses().getValues());
    }

    @Override
    public Text getJournalEntry(final JournalEntryIdentifier journalEntryID) throws QuestException {
        return journalEntries().get(journalEntryID);
    }

    @Override
    public void renameJournalEntry(final JournalEntryIdentifier name, final JournalEntryIdentifier rename) {
        journalEntries().renameJournalEntry(name, rename);
    }

    @Override
    public Map<JournalMainPageIdentifier, JournalMainPageEntry> getJournalMainPages() {
        return new HashMap<>(journalMainPages().getValues());
    }

    @Override
    public DefaultNpcHider getNpcHider() {
        return npcs().getNpcHider();
    }
}
