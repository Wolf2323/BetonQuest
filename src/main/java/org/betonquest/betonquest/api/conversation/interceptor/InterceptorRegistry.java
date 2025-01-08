package org.betonquest.betonquest.api.conversation.interceptor;

import net.md_5.bungee.api.chat.BaseComponent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.registry.type.FactoryRegistry;

import java.lang.reflect.InvocationTargetException;

/**
 * Stores the interceptor types that can be used in BetonQuest.
 */
public class InterceptorRegistry extends FactoryRegistry<InterceptorFactory> {
    /**
     * Create a new type registry.
     *
     * @param log the logger that will be used for logging
     */
    public InterceptorRegistry(final BetonQuestLogger log) {
        super(log, "interceptor");
    }

    /**
     * Registers a type with its name and the class used to create instances of the type.
     *
     * @param name   the name of the type
     * @param lClass the class object for the type
     */
    @Deprecated
    public void register(final String name, final Class<? extends org.betonquest.betonquest.conversation.Interceptor> lClass) {
        log.debug("Registering " + name + " [legacy]" + typeName + " type");
        types.put(name, new FromLegacyFactory(lClass));
    }

    private static class FromLegacyFactory implements InterceptorFactory {
        private final Class<? extends org.betonquest.betonquest.conversation.Interceptor> legacyInterceptor;

        private FromLegacyFactory(final Class<? extends org.betonquest.betonquest.conversation.Interceptor> legacyInterceptor) {
            this.legacyInterceptor = legacyInterceptor;
        }

        @Override
        public Interceptor create(final OnlineProfile onlineProfile) throws QuestRuntimeException {
            try {
                final org.betonquest.betonquest.conversation.Interceptor legacy =
                        legacyInterceptor.getConstructor(Conversation.class, OnlineProfile.class)
                                .newInstance(conv, onlineProfile);
                return new LegacyAdapter(legacy);
            } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
                           | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new QuestRuntimeException(e);
            }
        }

        private static class LegacyAdapter implements Interceptor {
            private final org.betonquest.betonquest.conversation.Interceptor legacy;

            private LegacyAdapter(final org.betonquest.betonquest.conversation.Interceptor legacy) {
                this.legacy = legacy;
            }

            @Override
            public void sendMessage(final BaseComponent... message) {
                legacy.sendMessage(message);
            }

            @Override
            public void end() {
                legacy.end();
            }
        }
    }
}
