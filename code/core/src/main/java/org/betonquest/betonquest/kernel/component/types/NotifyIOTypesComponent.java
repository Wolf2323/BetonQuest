package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.notify.SuppressNotifyIOFactory;
import org.betonquest.betonquest.notify.io.ActionBarNotifyIOFactory;
import org.betonquest.betonquest.notify.io.AdvancementNotifyIOFactory;
import org.betonquest.betonquest.notify.io.BossBarNotifyIOFactory;
import org.betonquest.betonquest.notify.io.ChatNotifyIOFactory;
import org.betonquest.betonquest.notify.io.SoundIOFactory;
import org.betonquest.betonquest.notify.io.SubTitleNotifyIOFactory;
import org.betonquest.betonquest.notify.io.TitleNotifyIOFactory;
import org.betonquest.betonquest.notify.io.TotemNotifyIOFactory;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading notifyIO types.
 */
public class NotifyIOTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new NotifyIOTypesComponent.
     */
    public NotifyIOTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, NotifyIORegistry.class, Conversations.class, PlaceholderProcessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final NotifyIORegistry notifyIORegistry = getDependency(NotifyIORegistry.class);
        final Conversations conversations = getDependency(Conversations.class);
        final PlaceholderProcessor placeholderProcessor = getDependency(PlaceholderProcessor.class);

        notifyIORegistry.register("suppress", new SuppressNotifyIOFactory());
        notifyIORegistry.register("chat", new ChatNotifyIOFactory(placeholderProcessor, conversations));
        notifyIORegistry.register("advancement", new AdvancementNotifyIOFactory(placeholderProcessor, plugin));
        notifyIORegistry.register("actionbar", new ActionBarNotifyIOFactory(placeholderProcessor));
        notifyIORegistry.register("bossbar", new BossBarNotifyIOFactory(placeholderProcessor, plugin));
        notifyIORegistry.register("title", new TitleNotifyIOFactory(placeholderProcessor));
        notifyIORegistry.register("totem", new TotemNotifyIOFactory(placeholderProcessor));
        notifyIORegistry.register("subtitle", new SubTitleNotifyIOFactory(placeholderProcessor));
        notifyIORegistry.register("sound", new SoundIOFactory(placeholderProcessor));
    }
}
