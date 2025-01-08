package org.betonquest.betonquest.api.conversation.interceptor;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Factory to create a new {@link Interceptor}.
 */
public interface InterceptorFactory {
    /**
     * Creates a new {@link Interceptor}.
     *
     * @param profile the profile of the player
     * @return the new interceptor
     * @throws QuestRuntimeException if the interceptor could not be created
     */
    Interceptor create(OnlineProfile profile) throws QuestRuntimeException;
}
