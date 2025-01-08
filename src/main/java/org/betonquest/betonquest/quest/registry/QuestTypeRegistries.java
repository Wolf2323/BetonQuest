package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.api.conversation.interceptor.InterceptorRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.VariableTypeRegistry;

/**
 * Registry for quest core elements.
 */
public class QuestTypeRegistries {
    /**
     * Condition type registry.
     */
    private final ConditionTypeRegistry conditionTypes;

    /**
     * Event type registry.
     */
    private final EventTypeRegistry eventTypes;

    /**
     * Variable type registry.
     */
    private final VariableTypeRegistry variableRegistry;

    /**
     * Interceptor type registry.
     */
    private final InterceptorRegistry interceptorRegistry;

    /**
     * Create a new quest registry for quest core elements.
     *
     * @param loggerFactory the logger factory to create individual class logger
     */
    public QuestTypeRegistries(final BetonQuestLoggerFactory loggerFactory) {
        this.conditionTypes = new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class), loggerFactory);
        this.eventTypes = new EventTypeRegistry(loggerFactory.create(EventTypeRegistry.class), loggerFactory);
        this.variableRegistry = new VariableTypeRegistry(loggerFactory.create(VariableTypeRegistry.class), loggerFactory);
        this.interceptorRegistry = new InterceptorRegistry(loggerFactory.create(InterceptorRegistry.class));
    }

    /**
     * Gets the Registry holding registered condition types.
     *
     * @return registry containing usable condition types
     */
    public ConditionTypeRegistry getConditionTypes() {
        return conditionTypes;
    }

    /**
     * Gets the Registry holding registered event types.
     *
     * @return registry containing usable event types
     */
    public EventTypeRegistry getEventTypes() {
        return eventTypes;
    }

    /**
     * Gets the Registry holding registered variable types.
     *
     * @return registry containing usable variable types
     */
    public VariableTypeRegistry getVariableTypes() {
        return variableRegistry;
    }

    /**
     * Gets the Registry holding registered interceptor types.
     *
     * @return registry containing usable interceptor types
     */
    public InterceptorRegistry getInterceptorTypes() {
        return interceptorRegistry;
    }
}
