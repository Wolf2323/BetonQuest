package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.id.journal.JournalEntryIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalMainPageIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent}
 * for {@link JournalEntryProcessor}, {@link JournalMainPageProcessor} and {@link JournalFactory}.
 */
public class JournalsComponent extends AbstractCoreComponent {

    /**
     * The journal entry processor to load.
     */
    @Nullable
    private JournalEntryProcessor journalEntryProcessor;

    /**
     * The journal main page processor to load.
     */
    @Nullable
    private JournalMainPageProcessor journalMainPageProcessor;

    /**
     * The journal factory to load.
     */
    @Nullable
    private JournalFactory journalFactory;

    /**
     * Create a new JournalsComponent.
     */
    public JournalsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, ConfigAccessor.class,
                Identifiers.class, Instructions.class, PluginMessage.class, TextParser.class,
                ParsedSectionTextCreator.class, FontRegistry.class, ConditionProcessor.class);
    }

    @Override
    public boolean isLoaded() {
        return journalEntryProcessor != null && journalMainPageProcessor != null && journalFactory != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final TextParser textParser = getDependency(TextParser.class);
        final FontRegistry fontRegistry = getDependency(FontRegistry.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);

        final JournalEntryIdentifierFactory journalEntryIdentifierFactory = new JournalEntryIdentifierFactory(questPackageManager);
        identifiers.register(JournalEntryIdentifier.class, journalEntryIdentifierFactory);
        this.journalEntryProcessor = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class),
                journalEntryIdentifierFactory, parsedSectionTextCreator);

        final JournalMainPageIdentifierFactory journalMainPageIdentifierFactory = new JournalMainPageIdentifierFactory(questPackageManager);
        identifiers.register(JournalMainPageIdentifier.class, journalMainPageIdentifierFactory);
        this.journalMainPageProcessor = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class),
                instructions, parsedSectionTextCreator, journalMainPageIdentifierFactory);

        this.journalFactory = new JournalFactory(loggerFactory, pluginMessage, conditionProcessor, journalMainPageProcessor, journalEntryProcessor, config, textParser, fontRegistry);

        providerCallback.take(JournalEntryIdentifierFactory.class, journalEntryIdentifierFactory);
        providerCallback.take(JournalMainPageIdentifierFactory.class, journalMainPageIdentifierFactory);
        providerCallback.take(JournalEntryProcessor.class, journalEntryProcessor);
        providerCallback.take(JournalMainPageProcessor.class, journalMainPageProcessor);
        providerCallback.take(JournalFactory.class, journalFactory);
    }
}
