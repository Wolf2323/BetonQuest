package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.plugin.Plugin;

/**
 * SlowTellraw conversation output.
 */
public class SlowTellrawConvIOFactory implements ConversationIOFactory {

    /**
     * The logger that will be used for logging.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin configuration accessor.
     */
    private final ConfigAccessor config;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The plugin message instance.
     */
    private final PluginMessage message;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new SlowTellraw conversation IO factory.
     *
     * @param log          the logger that will be used for logging
     * @param config       the plugin configuration accessor
     * @param plugin       the plugin instance
     * @param message      the plugin message instance
     * @param fontRegistry The font registry used for the conversation.
     * @param colors       The colors used for the conversation.
     */
    public SlowTellrawConvIOFactory(final BetonQuestLogger log, final ConfigAccessor config, final Plugin plugin,
                                    final PluginMessage message, final FontRegistry fontRegistry, final ConversationColors colors) {
        this.log = log;
        this.config = config;
        this.plugin = plugin;
        this.message = message;
        this.fontRegistry = fontRegistry;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 320);
        int messageDelay = config.getInt("conversation.io.slowtellraw.message_delay", 10);
        if (messageDelay <= 0) {
            log.warn("Invalid message delay of " + messageDelay + " for SlowTellraw Conversation IO, using default value of 10 ticks");
            messageDelay = 10;
        }
        return new SlowTellrawConvIO(log, config, plugin, message, conversation, onlineProfile, messageDelay, componentLineWrapper, colors);
    }
}
