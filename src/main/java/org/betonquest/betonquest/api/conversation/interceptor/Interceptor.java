package org.betonquest.betonquest.api.conversation.interceptor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The interceptor is used to intercept chat messages that are sent to the player.
 */
public interface Interceptor {

    /**
     * Send a message to player bypassing Interceptor.
     *
     * @param message the message
     */
    default void sendMessage(final String message) {
        sendMessage(TextComponent.fromLegacyText(message));
    }

    /**
     * Send a message to player bypassing Interceptor.
     *
     * @param message the message
     */
    void sendMessage(BaseComponent... message);

    /**
     * Ends the work of this interceptor.
     */
    void end();
}
