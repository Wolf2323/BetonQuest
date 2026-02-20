package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;

/**
 * Factory for creating {@link HolographicDisplaysIntegrator} instances.
 */
public class HolographicDisplaysIntegratorFactory implements IntegratorFactory {

    /**
     * Logger factory to create class-specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The instruction api to use.
     */
    private final Instructions instructions;

    /**
     * The identifier factory for placeholders.
     */
    private final IdentifierFactory<PlaceholderIdentifier> identifierFactory;

    /**
     * The placeholder processor.
     */
    private final PlaceholderProcessor placeholderProcessor;

    /**
     * Creates a new instance of the factory.
     *
     * @param loggerFactory        the logger factory to create class-specific logger
     * @param instructions         the instruction api to use
     * @param identifierFactory    the identifier factory to create placeholders
     * @param placeholderProcessor the placeholder processor
     */
    public HolographicDisplaysIntegratorFactory(final BetonQuestLoggerFactory loggerFactory,
                                                final Instructions instructions,
                                                final IdentifierFactory<PlaceholderIdentifier> identifierFactory,
                                                final PlaceholderProcessor placeholderProcessor) {
        this.loggerFactory = loggerFactory;
        this.instructions = instructions;
        this.identifierFactory = identifierFactory;
        this.placeholderProcessor = placeholderProcessor;
    }

    @Override
    public Integrator getIntegrator() {
        return new HolographicDisplaysIntegrator(loggerFactory.create(HolographicDisplaysIntegrator.class),
                instructions, identifierFactory, placeholderProcessor);
    }
}
