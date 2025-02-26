package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.config.PluginMessage;

/**
 * Notification sender that suppresses notifications instead of sending them.
 */
public class NoNotificationSender implements NotificationSender {

    /**
     * Create the no notification sender.
     */
    public NoNotificationSender() {
    }

    @Override
    public void sendNotification(final Profile profile, final PluginMessage.Replacement... variables) {
        // null object pattern
    }
}
