package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.ActionManager;
import org.betonquest.betonquest.api.service.BetonQuestInstructions;
import org.betonquest.betonquest.api.service.ConditionManager;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

/**
 * Does the load logic around {@link T} from a configuration section.
 *
 * @param <I> the {@link Identifier} identifying the type
 * @param <T> the type
 */
public abstract class RPGMenuProcessor<I extends Identifier, T> extends SectionProcessor<I, T> {

    /**
     * Logger Factory to create Menu Item Logger.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Text creator to parse text.
     */
    protected final ParsedSectionTextCreator textCreator;

    /**
     * The ActionManager.
     */
    protected final ActionManager actionManager;

    /**
     * The ConditionManager.
     */
    protected final ConditionManager conditionManager;

    /**
     * The Item Parser instance.
     */
    protected final InstructionArgumentParser<ItemWrapper> itemParser;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log               the custom logger for this class
     * @param instructionApi    the instruction api to use
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     * @param loggerFactory     the logger factory to class specific loggers with
     * @param textCreator       the text creator to parse text
     * @param parsers           the argument parsers
     * @param identifierFactory the identifier factory
     * @param actionManager     the ActionManager
     * @param conditionManager  the ConditionManager
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public RPGMenuProcessor(final BetonQuestLogger log, final BetonQuestInstructions instructionApi, final String readable,
                            final String internal, final BetonQuestLoggerFactory loggerFactory,
                            final ParsedSectionTextCreator textCreator, final ArgumentParsers parsers,
                            final IdentifierFactory<I> identifierFactory, final ActionManager actionManager, final ConditionManager conditionManager) {
        super(log, instructionApi, identifierFactory, readable, internal);
        this.loggerFactory = loggerFactory;
        this.textCreator = textCreator;
        this.itemParser = parsers.item();
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
    }
}
